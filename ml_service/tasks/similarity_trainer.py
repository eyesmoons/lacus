"""相似度训练器

继承 BaseTrainer，使用 SimilarityAutoEncoder + MSELoss 进行训练。
"""
import logging
import torch
from torch import nn, optim
from torch.utils.data import DataLoader

from core.trainer_base import BaseTrainer
from models.similarity_autoencoder import SimilarityAutoEncoder

logger = logging.getLogger(__name__)


class SimilarityTrainer(BaseTrainer):
    """相似度自编码器训练器"""

    def __init__(
        self,
        task_id: str,
        task_store: dict,
        train_loader: DataLoader,
        val_loader: DataLoader,
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

        # 创建模型
        self.model = SimilarityAutoEncoder().to(self.device)
        self.loss_fn = nn.MSELoss()

        # 优化器
        self.optimizer = optim.AdamW(self.model.parameters(), lr=learning_rate)

    def _train_one_epoch(self) -> float:
        """训练一个轮次"""
        self.model.train()
        total_loss = 0.0
        total_num = 0

        for input, target in self.train_loader:
            self._check_cancelled()
            input = input.to(self.device)
            target = target.to(self.device)

            # 前向传播
            embedding = self.model.encode(input)
            output = self.model.decode(embedding)
            loss = self.loss_fn(output, target)

            # 反向传播
            loss.backward()
            self.optimizer.step()
            self.optimizer.zero_grad()

            total_loss += loss.item() * input.shape[0]
            total_num += input.shape[0]

        return total_loss / total_num if total_num > 0 else 0.0

    def _validate(self) -> float:
        """验证"""
        self.model.eval()
        total_loss = 0.0
        total_num = 0

        with torch.no_grad():
            for input, target in self.val_loader:
                input = input.to(self.device)
                target = target.to(self.device)

                embedding = self.model.encode(input)
                output = self.model.decode(embedding)
                loss = self.loss_fn(output, target)

                total_loss += loss.item() * input.shape[0]
                total_num += input.shape[0]

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
