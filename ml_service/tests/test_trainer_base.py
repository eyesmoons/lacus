"""BaseTrainer 测试"""
import pytest
import time
from unittest.mock import MagicMock, patch

from core.trainer_base import BaseTrainer, TrainingProgress


class TestTrainingProgress:
    """测试 TrainingProgress 数据类"""

    def test_default_values(self):
        progress = TrainingProgress()
        assert progress.epoch == 0
        assert progress.total_epochs == 0
        assert progress.train_loss == 0.0
        assert progress.val_loss == 0.0
        assert progress.recon_loss == 0.0
        assert progress.contrastive_loss == 0.0
        assert progress.status == "idle"
        assert progress.message == ""

    def test_custom_values(self):
        progress = TrainingProgress(
            epoch=5, total_epochs=10, train_loss=0.1, val_loss=0.2,
            recon_loss=0.05, contrastive_loss=0.01,
            status="training", message="进行中"
        )
        assert progress.epoch == 5
        assert progress.total_epochs == 10
        assert progress.train_loss == 0.1
        assert progress.val_loss == 0.2
        assert progress.recon_loss == 0.05
        assert progress.contrastive_loss == 0.01
        assert progress.status == "training"
        assert progress.message == "进行中"


class TestBaseTrainer:
    """测试 BaseTrainer 基类"""

    def test_initialization(self):
        task_store = {}
        trainer = BaseTrainer(task_id="test-001", task_store=task_store)
        assert trainer.task_id == "test-001"
        assert trainer.task_store is task_store
        assert trainer._cancelled is False

    def test_cancel(self):
        task_store = {}
        trainer = BaseTrainer(task_id="test-002", task_store=task_store)
        assert trainer._cancelled is False
        trainer.cancel()
        assert trainer._cancelled is True

    def test_is_cancelled(self):
        task_store = {}
        trainer = BaseTrainer(task_id="test-003", task_store=task_store)
        assert not trainer.is_cancelled()
        trainer.cancel()
        assert trainer.is_cancelled()

    def test_progress_callback_called(self):
        """测试训练过程中进度回调被调用"""
        task_store = {}
        callback = MagicMock()
        trainer = BaseTrainer(
            task_id="test-004",
            task_store=task_store,
            progress_callback=callback
        )

        # 模拟一次训练循环
        trainer._update_progress(
            epoch=1, total_epochs=3,
            train_loss=0.5, val_loss=0.4,
            status="training"
        )

        callback.assert_called_once()
        call_args = callback.call_args[0][0]
        assert call_args.epoch == 1
        assert call_args.total_epochs == 3
        assert call_args.train_loss == 0.5
        assert call_args.val_loss == 0.4

    def test_task_store_updated(self):
        """测试 task_store 被更新"""
        task_store = {}
        trainer = BaseTrainer(task_id="test-005", task_store=task_store)

        trainer._update_progress(
            epoch=2, total_epochs=5,
            train_loss=0.3, val_loss=0.25,
            status="training"
        )

        assert "test-005" in task_store
        progress = task_store["test-005"]
        assert progress.epoch == 2
        assert progress.status == "training"

    def test_check_cancelled_stops_training(self):
        """测试取消检查能中断训练"""
        task_store = {}
        trainer = BaseTrainer(task_id="test-006", task_store=task_store)
        trainer.cancel()

        with pytest.raises(InterruptedError, match="已取消"):
            trainer._check_cancelled()

    def test_save_model(self):
        """测试模型保存（每次训练只产生一个文件）"""
        task_store = {}
        trainer = BaseTrainer(task_id="test-007", task_store=task_store)

        mock_model = MagicMock()
        mock_model.state_dict.return_value = {"weight": 1.0}

        with patch("torch.save") as mock_save:
            path = trainer.save_model(mock_model)
            mock_save.assert_called_once()
            assert path.endswith(".pt")
            assert "test-007" in path

    def test_get_progress(self):
        """测试获取当前进度"""
        task_store = {}
        trainer = BaseTrainer(task_id="test-008", task_store=task_store)

        # 初始状态
        progress = trainer.get_progress()
        assert progress.status == "idle"

        # 更新后
        trainer._update_progress(epoch=1, total_epochs=10, status="training")
        progress = trainer.get_progress()
        assert progress.epoch == 1
        assert progress.status == "training"
