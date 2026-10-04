"""VectorBuilder 测试"""
import pytest
import os
import sys
import tempfile
from unittest.mock import MagicMock, patch
from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


class TestVectorBuilder:
    """测试向量构建服务"""

    def _create_image_dir(self, num_images=5):
        """创建临时图片目录"""
        tmpdir = tempfile.mkdtemp()
        for i in range(num_images):
            img = Image.new("RGB", (64, 64), color=(i * 50, i * 50, i * 50))
            img.save(os.path.join(tmpdir, f"img_{i:03d}.png"))
        return tmpdir

    @patch("utils.vector_builder.ModelCache")
    @patch("utils.vector_builder.ImageDataset")
    def test_do_build_vectors(self, mock_dataset_cls, mock_cache_cls):
        """测试向量构建主函数"""
        from utils.vector_builder import do_build_vectors
        from core.trainer_base import TrainingProgress

        # 模拟数据集
        mock_dataset = MagicMock()
        mock_dataset.__len__ = MagicMock(return_value=10)
        mock_dataset.__getitem__ = MagicMock(return_value=(MagicMock(), MagicMock()))
        mock_dataset_cls.return_value = mock_dataset

        # 模拟模型缓存
        mock_cache = MagicMock()
        mock_model = MagicMock()
        mock_model.encode.return_value = MagicMock()
        mock_model.encode.return_value.numpy.return_value = MagicMock()
        mock_cache.get_model.return_value = mock_model
        mock_cache_cls.return_value = mock_cache

        task_store = {}
        result = do_build_vectors(
            dataset_uri="file:///data/images",
            collection_name="test_collection",
            task_id="task-vb-001",
            task_store=task_store,
        )

        # 验证任务状态被更新
        assert "task-vb-001" in task_store

    def test_do_build_vectors_with_empty_dataset(self):
        """测试空数据集处理"""
        from utils.vector_builder import do_build_vectors
        from core.trainer_base import TrainingProgress

        task_store = {}

        with patch("utils.vector_builder.ImageDataset") as mock_dataset_cls:
            mock_dataset = MagicMock()
            mock_dataset.__len__ = MagicMock(return_value=0)
            mock_dataset_cls.return_value = mock_dataset

            with patch("utils.vector_builder.ModelCache"):
                result = do_build_vectors(
                    dataset_uri="file:///empty",
                    collection_name="test",
                    task_id="task-vb-002",
                    task_store=task_store,
                )

        progress = task_store["task-vb-002"]
        assert progress.status in ("completed", "failed")
