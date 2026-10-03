"""SimilarityTrainer 测试"""
import pytest
import torch
from torch.utils.data import DataLoader, TensorDataset
from unittest.mock import MagicMock, patch

from core.trainer_base import BaseTrainer
from tasks.similarity_trainer import SimilarityTrainer


def _create_dummy_loader(batch_size: int = 4, num_samples: int = 16) -> DataLoader:
    """创建模拟数据加载器"""
    images = torch.randn(num_samples, 3, 64, 64)
    dataset = TensorDataset(images, images)
    return DataLoader(dataset, batch_size=batch_size)


class TestSimilarityTrainer:
    """测试 SimilarityTrainer"""

    def test_inherits_base_trainer(self):
        task_store = {}
        trainer = SimilarityTrainer(
            task_id="sim-001",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
        )
        assert isinstance(trainer, BaseTrainer)

    def test_uses_mse_loss(self):
        task_store = {}
        trainer = SimilarityTrainer(
            task_id="sim-002",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
        )
        assert isinstance(trainer.loss_fn, torch.nn.MSELoss)

    def test_training_completes(self):
        """测试训练能正常完成"""
        task_store = {}
        trainer = SimilarityTrainer(
            task_id="sim-003",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            epochs=2,
        )
        trainer.train()

        progress = task_store["sim-003"]
        assert progress.status == "completed"
        assert progress.epoch == 2

    def test_training_can_be_cancelled(self):
        """测试训练可被取消"""
        task_store = {}

        # 创建一个会触发取消的训练器
        trainer = SimilarityTrainer(
            task_id="sim-004",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            epochs=10,
        )

        # 在第一次进度更新后取消
        original_update = trainer._update_progress
        def cancel_on_first_update(*args, **kwargs):
            original_update(*args, **kwargs)
            trainer.cancel()

        trainer._update_progress = cancel_on_first_update

        trainer.train()

        progress = task_store["sim-004"]
        assert progress.status == "cancelled"

    def test_progress_updated_during_training(self):
        """测试训练过程中进度被更新"""
        task_store = {}
        trainer = SimilarityTrainer(
            task_id="sim-005",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            epochs=3,
        )
        trainer.train()

        progress = task_store["sim-005"]
        assert progress.epoch == 3
        assert progress.total_epochs == 3
        assert progress.train_loss > 0

    def test_model_is_autoencoder(self):
        """测试内部使用 SimilarityAutoEncoder"""
        task_store = {}
        trainer = SimilarityTrainer(
            task_id="sim-006",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
        )
        from models.similarity_autoencoder import SimilarityAutoEncoder
        assert isinstance(trainer.model, SimilarityAutoEncoder)

    def test_custom_epochs(self):
        """测试自定义训练轮数"""
        task_store = {}
        trainer = SimilarityTrainer(
            task_id="sim-007",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            epochs=5,
        )
        trainer.train()
        progress = task_store["sim-007"]
        assert progress.epoch == 5
        assert progress.total_epochs == 5
