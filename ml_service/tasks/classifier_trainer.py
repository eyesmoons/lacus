"""分类训练器

继承 BaseTrainer，使用 Classifier + CrossEntropyLoss 进行图像分类训练。
"""
import logging
import torch
from torch import nn, optim
from torch.utils.data import DataLoader

from core.trainer_base import BaseTrainer
from models.classifier import Classifier

logger = logging.getLogger(__name__)


class ClassifierTrainer(BaseTrainer):
    """图像分类训练器"""

    def __init__(
        self,
        task_id: str,
        task_store: dict,
        train_loader: DataLoader,
        val_loader: DataLoader,
        config: dict,
        epochs: int = 10,
        learning_rate: float = 1e-3,
        device: str = "cpu",
        progress_callback=None,
    ):
        super().__init__(task_id, task_store, progress_callback)
        self.train_loader = train_loader
        self.val_loader = val_loader
        self.epochs = epochs
        self.device = torch.device(device)
        self.config = config

        # 构建模型、损失函数和优化器
        self.model = self.build_model().to(self.device)
        self.loss_fn = self.build_loss_fn()
        self.optimizer = optim.AdamW(self.model.parameters(), lr=learning_rate)

    def build_model(self) -> nn.Module:
        """构建分类模型"""
        return Classifier(n_classes=self.config["n_classes"])

    def build_loss_fn(self) -> nn.Module:
        """构建损失函数"""
        return nn.CrossEntropyLoss()

    def _train_one_epoch(self) -> float:
        """训练一个轮次"""
        self.model.train()
        total_loss = 0.0
        total_num = 0

        for images, labels in self.train_loader:
            self._check_cancelled()
            images = images.to(self.device)
            labels = labels.to(self.device)

            # 前向传播
            logits = self.model(images)
            loss = self.loss_fn(logits, labels)

            # 反向传播
            loss.backward()
            self.optimizer.step()
            self.optimizer.zero_grad()

            total_loss += loss.item() * images.shape[0]
            total_num += images.shape[0]

        return total_loss / total_num if total_num > 0 else 0.0

    def _validate(self) -> float:
        """验证"""
        self.model.eval()
        total_loss = 0.0
        total_num = 0

        with torch.no_grad():
            for images, labels in self.val_loader:
                images = images.to(self.device)
                labels = labels.to(self.device)

                logits = self.model(images)
                loss = self.loss_fn(logits, labels)

                total_loss += loss.item() * images.shape[0]
                total_num += images.shape[0]

        return total_loss / total_num if total_num > 0 else 0.0

    def _run_training(self) -> None:
        """执行训练循环"""
        logger.info("[train] 开始训练循环: task_id=%s, epochs=%s, device=%s", self.task_id, self.epochs, self.device)
        min_val_loss = float("inf")

        for epoch in range(1, self.epochs + 1):
            self._check_cancelled()

            train_loss = self._train_one_epoch()
            val_loss = self._validate()

            self._update_progress(
                epoch=epoch,
                total_epochs=self.epochs,
                train_loss=train_loss,
                val_loss=val_loss,
                status="training",
                message=f"Epoch {epoch}/{self.epochs}",
            )
            logger.info("[train] task_id=%s, epoch=%s/%s, train_loss=%.6f, val_loss=%.6f",
                        self.task_id, epoch, self.epochs, train_loss, val_loss)

            # 保存最优模型
            if val_loss < min_val_loss:
                min_val_loss = val_loss
                self.save_checkpoint(self.model, epoch)
                logger.info("[train] 保存最优模型: task_id=%s, epoch=%s, val_loss=%.6f", self.task_id, epoch, val_loss)

        logger.info("[train] 训练循环完成: task_id=%s", self.task_id)
