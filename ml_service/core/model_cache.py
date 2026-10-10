"""模型缓存

提供 LRU 策略的模型缓存，避免重复加载模型。
"""
from collections import OrderedDict
from typing import Any, Callable, Dict

from config import default_config


class ModelCache:
    """LRU 模型缓存

    使用 OrderedDict 实现 LRU 策略，最近访问的模型移到末尾，
    当缓存满时淘汰队首（最久未使用）的模型。
    """

    def __init__(self, max_size: int = None):
        self.max_size = max_size or default_config.model_cache_max_size
        self.cache: OrderedDict[str, Any] = OrderedDict()
        # 自定义模型创建器注册表
        self._creators: Dict[str, Callable[[], Any]] = {}
        # 注册默认模型
        self._register_defaults()

    def _register_defaults(self) -> None:
        """注册默认模型创建器"""
        def _create_similarity():
            import torch
            from models.similarity_autoencoder import SimilarityAutoEncoder
            # 固定种子：未加载训练权重时也保证多个实例一致（建库/检索需用同一编码器）
            torch.manual_seed(0)
            return SimilarityAutoEncoder()
        self._creators["similarity_autoencoder"] = _create_similarity

        def _create_classifier():
            from models.classifier import Classifier
            return Classifier()
        self._creators["classifier"] = _create_classifier

    def register_creator(self, name: str, creator: Callable[[], Any]) -> None:
        """注册自定义模型创建器"""
        self._creators[name] = creator

    def get_model(self, model_name: str, weights_path: str = None, **kwargs) -> Any:
        """获取模型，若不存在则创建。

        weights_path 非空时加载该权重文件；缓存键包含权重路径，
        避免不同权重的同名模型互相覆盖。
        """
        key = model_name if not weights_path else f"{model_name}::{weights_path}"
        if key in self.cache:
            # 移到末尾（最近使用）
            self.cache.move_to_end(key)
            return self.cache[key]

        # 创建新模型
        model = self._create_model(model_name, **kwargs)
        if weights_path:
            import os
            if not os.path.isfile(weights_path):
                raise FileNotFoundError(f"模型权重文件不存在: {weights_path}")
            import torch
            model.load_state_dict(torch.load(weights_path, map_location="cpu"))
        model.eval()
        self._put_model(key, model)
        return model

    def _put_model(self, name: str, model: Any) -> None:
        """添加模型到缓存，若满则淘汰"""
        if len(self.cache) >= self.max_size:
            # 淘汰队首（最久未使用）
            self.cache.popitem(last=False)
        self.cache[name] = model

    def _create_model(self, model_name: str, **kwargs) -> Any:
        """根据名称创建模型实例"""
        if model_name in self._creators:
            return self._creators[model_name]()
        raise ValueError(f"未知模型: {model_name}")

    def clear(self) -> None:
        """清空缓存"""
        self.cache.clear()

    def __len__(self) -> int:
        return len(self.cache)

    def __contains__(self, name: str) -> bool:
        return name in self.cache
