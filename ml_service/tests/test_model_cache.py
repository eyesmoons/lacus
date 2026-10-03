"""ModelCache 测试"""
import pytest
import os
import sys
from unittest.mock import patch, MagicMock

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


class TestModelCache:
    """测试 LRU 模型缓存"""

    def test_initialization(self):
        from core.model_cache import ModelCache
        cache = ModelCache(max_size=3)
        assert cache.max_size == 3
        assert len(cache.cache) == 0

    def test_get_model_creates_new(self):
        """测试首次获取模型时创建新实例"""
        from core.model_cache import ModelCache
        cache = ModelCache(max_size=3)

        with patch("models.similarity_autoencoder.SimilarityAutoEncoder") as mock_cls:
            mock_instance = MagicMock()
            mock_cls.return_value = mock_instance

            model = cache.get_model("similarity_autoencoder")

            mock_cls.assert_called_once()
            assert model is mock_instance

    def test_get_model_returns_cached(self):
        """测试第二次获取返回缓存实例"""
        from core.model_cache import ModelCache
        cache = ModelCache(max_size=3)

        with patch("models.similarity_autoencoder.SimilarityAutoEncoder") as mock_cls:
            mock_instance = MagicMock()
            mock_cls.return_value = mock_instance

            model1 = cache.get_model("similarity_autoencoder")
            model2 = cache.get_model("similarity_autoencoder")

            # 只创建一次
            mock_cls.assert_called_once()
            assert model1 is model2

    def test_lru_eviction(self):
        """测试 LRU 淘汰"""
        from core.model_cache import ModelCache
        cache = ModelCache(max_size=2)

        # 注册自定义模型创建器
        instances = [MagicMock(), MagicMock(), MagicMock()]
        iter_instances = iter(instances)
        cache.register_creator("model_a", lambda: next(iter_instances))
        cache.register_creator("model_b", lambda: next(iter_instances))
        cache.register_creator("model_c", lambda: next(iter_instances))

        cache.get_model("model_a")
        cache.get_model("model_b")
        # 此时缓存满，再添加 model_c 应淘汰 model_a
        cache.get_model("model_c")

        # model_a 应被淘汰
        assert "model_a" not in cache.cache
        assert "model_b" in cache.cache
        assert "model_c" in cache.cache

    def test_lru_access_updates_order(self):
        """测试访问更新 LRU 顺序"""
        from core.model_cache import ModelCache
        cache = ModelCache(max_size=2)

        instances = [MagicMock(), MagicMock(), MagicMock()]
        iter_instances = iter(instances)
        cache.register_creator("model_a", lambda: next(iter_instances))
        cache.register_creator("model_b", lambda: next(iter_instances))
        cache.register_creator("model_c", lambda: next(iter_instances))

        cache.get_model("model_a")
        cache.get_model("model_b")
        # 访问 model_a，使其变为最近使用
        cache.get_model("model_a")
        # 添加 model_c，此时应淘汰 model_b
        cache.get_model("model_c")

        assert "model_a" in cache.cache
        assert "model_b" not in cache.cache
        assert "model_c" in cache.cache

    def test_clear_cache(self):
        """测试清空缓存"""
        from core.model_cache import ModelCache
        cache = ModelCache(max_size=5)

        cache.register_creator("model_x", MagicMock)
        cache.register_creator("model_y", MagicMock)

        cache.get_model("model_x")
        cache.get_model("model_y")
        assert len(cache.cache) == 2

        cache.clear()
        assert len(cache.cache) == 0

    def test_default_max_size(self):
        """测试默认最大缓存大小"""
        from core.model_cache import ModelCache
        from config import default_config
        cache = ModelCache()
        assert cache.max_size == default_config.model_cache_max_size
