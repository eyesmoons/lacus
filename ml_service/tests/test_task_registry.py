"""TaskRegistry 测试"""
import pytest
from torch.utils.data import DataLoader, TensorDataset

from core.task_registry import TaskRegistry, create_trainer
from core.trainer_base import BaseTrainer
from tasks.similarity_trainer import SimilarityTrainer


def _create_dummy_loader(batch_size=4, num_samples=16):
    images = torch.randn(num_samples, 3, 64, 64)
    dataset = TensorDataset(images, images)
    return DataLoader(dataset, batch_size=batch_size)


class TestTaskRegistry:
    """测试 TaskRegistry 和工厂函数"""

    def test_registry_has_similarity_trainer(self):
        """验证 TRAINER_REGISTRY 包含 similarity 类型"""
        assert "similarity" in TaskRegistry.TRAINER_REGISTRY

    def test_registry_value_is_similarity_trainer(self):
        assert TaskRegistry.TRAINER_REGISTRY["similarity"] is SimilarityTrainer

    def test_create_similarity_trainer(self):
        """测试创建 SimilarityTrainer"""
        task_store = {}
        train_loader = _create_dummy_loader()
        val_loader = _create_dummy_loader()

        trainer = TaskRegistry.create_trainer(
            trainer_type="similarity",
            task_id="task-001",
            task_store=task_store,
            train_loader=train_loader,
            val_loader=val_loader,
            epochs=5,
        )

        assert isinstance(trainer, SimilarityTrainer)
        assert trainer.task_id == "task-001"
        assert trainer.epochs == 5

    def test_create_unknown_trainer_raises(self):
        """测试未知训练器类型抛出异常"""
        with pytest.raises(ValueError, match="未知的训练器类型"):
            TaskRegistry.create_trainer(
                trainer_type="unknown",
                task_id="task-002",
                task_store={},
            )

    def test_create_trainer_without_required_args_raises(self):
        """测试缺少必要参数时抛出异常"""
        with pytest.raises(TypeError):
            TaskRegistry.create_trainer(
                trainer_type="similarity",
                task_id="task-003",
                task_store={},
                # 缺少 train_loader 和 val_loader
            )

    def test_register_new_trainer(self):
        """测试动态注册新训练器"""
        class CustomTrainer(BaseTrainer):
            def _run_training(self):
                pass

        TaskRegistry.register("custom", CustomTrainer)
        assert "custom" in TaskRegistry.TRAINER_REGISTRY
        assert TaskRegistry.TRAINER_REGISTRY["custom"] is CustomTrainer

        # 清理
        del TaskRegistry.TRAINER_REGISTRY["custom"]


# 补充 torch import
import torch
