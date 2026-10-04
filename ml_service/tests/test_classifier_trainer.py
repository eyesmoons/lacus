"""ClassifierTrainer 分类训练器测试"""
import pytest
import torch
from torch import nn
from torch.utils.data import DataLoader, TensorDataset
from unittest.mock import MagicMock

from core.trainer_base import BaseTrainer
from core.task_registry import TaskRegistry
from tasks.classifier_trainer import ClassifierTrainer
from models.classifier import Classifier


def _create_dummy_loader(batch_size: int = 4, num_samples: int = 16, n_classes: int = 5) -> DataLoader:
    """创建模拟分类数据加载器"""
    images = torch.randn(num_samples, 3, 64, 64)
    labels = torch.randint(0, n_classes, (num_samples,))
    dataset = TensorDataset(images, labels)
    return DataLoader(dataset, batch_size=batch_size)


class TestClassifierModel:
    """测试 Classifier 模型"""

    def test_forward_output_shape(self):
        """测试前向传播输出 shape 为 (N, n_classes)"""
        n_classes = 5
        model = Classifier(n_classes=n_classes)
        model.eval()
        batch_size = 8
        input_tensor = torch.randn(batch_size, 3, 64, 64)

        with torch.no_grad():
            output = model(input_tensor)

        assert output.shape == (batch_size, n_classes), \
            f"期望 shape ({batch_size}, {n_classes})，实际得到 {output.shape}"

    def test_default_n_classes(self):
        """测试默认分类数为 5"""
        model = Classifier()
        model.eval()
        input_tensor = torch.randn(2, 3, 64, 64)

        with torch.no_grad():
            output = model(input_tensor)

        assert output.shape == (2, 5)

    def test_custom_n_classes(self):
        """测试自定义分类数"""
        n_classes = 10
        model = Classifier(n_classes=n_classes)
        model.eval()
        input_tensor = torch.randn(4, 3, 64, 64)

        with torch.no_grad():
            output = model(input_tensor)

        assert output.shape == (4, n_classes)

    def test_training_mode_has_gradients(self):
        """测试训练模式下梯度可传播"""
        model = Classifier(n_classes=5)
        model.train()
        input_tensor = torch.randn(2, 3, 64, 64)

        output = model(input_tensor)
        loss = output.mean()
        loss.backward()

        has_grad = any(p.grad is not None for p in model.parameters())
        assert has_grad, "训练模式下参数应能接收梯度"


class TestClassifierTrainer:
    """测试 ClassifierTrainer"""

    def test_inherits_base_trainer(self):
        """测试继承自 BaseTrainer"""
        task_store = {}
        trainer = ClassifierTrainer(
            task_id="cls-001",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            config={"n_classes": 5},
        )
        assert isinstance(trainer, BaseTrainer)

    def test_uses_cross_entropy_loss(self):
        """测试使用 CrossEntropyLoss"""
        task_store = {}
        trainer = ClassifierTrainer(
            task_id="cls-002",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            config={"n_classes": 5},
        )
        assert isinstance(trainer.loss_fn, nn.CrossEntropyLoss)

    def test_model_is_classifier(self):
        """测试内部使用 Classifier 模型"""
        task_store = {}
        trainer = ClassifierTrainer(
            task_id="cls-003",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            config={"n_classes": 5},
        )
        assert isinstance(trainer.model, Classifier)

    def test_model_has_correct_n_classes(self):
        """测试模型分类数与配置一致"""
        task_store = {}
        n_classes = 7
        trainer = ClassifierTrainer(
            task_id="cls-004",
            task_store=task_store,
            train_loader=_create_dummy_loader(n_classes=n_classes),
            val_loader=_create_dummy_loader(n_classes=n_classes),
            config={"n_classes": n_classes},
        )
        # 验证模型输出维度
        trainer.model.eval()
        input_tensor = torch.randn(2, 3, 64, 64)
        with torch.no_grad():
            output = trainer.model(input_tensor)
        assert output.shape == (2, n_classes)

    def test_training_completes(self):
        """测试训练能正常完成"""
        task_store = {}
        trainer = ClassifierTrainer(
            task_id="cls-005",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            config={"n_classes": 5},
            epochs=2,
        )
        trainer.train()

        progress = task_store["cls-005"]
        assert progress.status == "completed"
        assert progress.epoch == 2

    def test_training_can_be_cancelled(self):
        """测试训练可被取消"""
        task_store = {}
        trainer = ClassifierTrainer(
            task_id="cls-006",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            config={"n_classes": 5},
            epochs=10,
        )

        # 在第一次进度更新后取消
        original_update = trainer._update_progress
        def cancel_on_first_update(*args, **kwargs):
            original_update(*args, **kwargs)
            trainer.cancel()

        trainer._update_progress = cancel_on_first_update

        trainer.train()

        progress = task_store["cls-006"]
        assert progress.status == "cancelled"

    def test_progress_updated_during_training(self):
        """测试训练过程中进度被更新"""
        task_store = {}
        trainer = ClassifierTrainer(
            task_id="cls-007",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            config={"n_classes": 5},
            epochs=3,
        )
        trainer.train()

        progress = task_store["cls-007"]
        assert progress.epoch == 3
        assert progress.total_epochs == 3
        assert progress.train_loss > 0

    def test_custom_epochs(self):
        """测试自定义训练轮数"""
        task_store = {}
        trainer = ClassifierTrainer(
            task_id="cls-008",
            task_store=task_store,
            train_loader=_create_dummy_loader(),
            val_loader=_create_dummy_loader(),
            config={"n_classes": 5},
            epochs=5,
        )
        trainer.train()
        progress = task_store["cls-008"]
        assert progress.epoch == 5
        assert progress.total_epochs == 5


class TestClassifierTrainerRegistry:
    """测试 ClassifierTrainer 注册"""

    def test_registry_has_image_classification(self):
        """验证 TRAINER_REGISTRY 包含 image_classification 类型"""
        assert "image_classification" in TaskRegistry.TRAINER_REGISTRY

    def test_registry_value_is_classifier_trainer(self):
        """验证注册值为 ClassifierTrainer"""
        assert TaskRegistry.TRAINER_REGISTRY["image_classification"] is ClassifierTrainer

    def test_create_classifier_trainer_via_registry(self):
        """测试通过注册表创建 ClassifierTrainer"""
        task_store = {}
        train_loader = _create_dummy_loader()
        val_loader = _create_dummy_loader()

        trainer = TaskRegistry.create_trainer(
            trainer_type="image_classification",
            task_id="cls-reg-001",
            task_store=task_store,
            train_loader=train_loader,
            val_loader=val_loader,
            config={"n_classes": 5},
            epochs=3,
        )

        assert isinstance(trainer, ClassifierTrainer)
        assert trainer.task_id == "cls-reg-001"
        assert trainer.epochs == 3
