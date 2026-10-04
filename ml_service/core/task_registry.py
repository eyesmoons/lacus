"""任务注册中心

提供训练器注册和工厂创建功能。
"""
from typing import Dict, Type

from core.trainer_base import BaseTrainer
from tasks.classifier_trainer import ClassifierTrainer
from tasks.similarity_trainer import SimilarityTrainer


class TaskRegistry:
    """训练器注册中心"""

    # 训练器注册表：类型名称 -> 训练器类
    TRAINER_REGISTRY: Dict[str, Type[BaseTrainer]] = {
        "similarity": SimilarityTrainer,
        "image_classification": ClassifierTrainer,
    }

    @classmethod
    def register(cls, name: str, trainer_class: Type[BaseTrainer]) -> None:
        """注册新的训练器类型"""
        cls.TRAINER_REGISTRY[name] = trainer_class

    @classmethod
    def create_trainer(cls, trainer_type: str, **kwargs) -> BaseTrainer:
        """根据类型创建训练器实例"""
        if trainer_type not in cls.TRAINER_REGISTRY:
            available = ", ".join(cls.TRAINER_REGISTRY.keys())
            raise ValueError(
                f"未知的训练器类型: '{trainer_type}'，可用类型: [{available}]"
            )
        trainer_class = cls.TRAINER_REGISTRY[trainer_type]
        return trainer_class(**kwargs)


# 便捷工厂函数
def create_trainer(trainer_type: str, **kwargs) -> BaseTrainer:
    """创建训练器实例的便捷函数"""
    return TaskRegistry.create_trainer(trainer_type, **kwargs)
