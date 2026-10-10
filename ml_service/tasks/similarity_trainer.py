"""相似度训练器

继承 BaseTrainer，使用 SimilarityAutoEncoder + MSELoss 进行训练。
"""
import logging
import torch
import torch.nn.functional as F
from torch import nn, optim
from torch.utils.data import DataLoader

from config import default_config
from core.trainer_base import BaseTrainer, resolve_device
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
        self.device = resolve_device(device)

        # 创建模型
        self.model = SimilarityAutoEncoder().to(self.device)
        self.loss_fn = nn.MSELoss()

        # 优化器
        self.optimizer = optim.AdamW(self.model.parameters(), lr=learning_rate)

        # 对比损失超参
        self.contrastive_weight = default_config.contrastive_weight
        self.temperature = default_config.contrastive_temperature

    def _augment(self, x: torch.Tensor) -> torch.Tensor:
        """随机增强，产生一个视图（水平翻转 + 随机裁剪缩放）"""
        n, _, h, w = x.shape
        out = x.clone()
        for i in range(n):
            if torch.rand(1).item() < 0.5:
                out[i] = torch.flip(out[i], dims=[2])
            scale = 0.6 + 0.4 * torch.rand(1).item()  # 0.6~1.0
            ch = max(8, int(h * scale))
            cw = max(8, int(w * scale))
            top = torch.randint(0, h - ch + 1, (1,)).item()
            left = torch.randint(0, w - cw + 1, (1,)).item()
            crop = out[i:i + 1, :, top:top + ch, left:left + cw]
            out[i:i + 1] = F.interpolate(crop, size=(h, w), mode="bilinear", align_corners=False)
        return out

    def _contrastive_loss(self, z1: torch.Tensor, z2: torch.Tensor) -> torch.Tensor:
        """NT-Xent（SimCLR）：两个视图互为正样本，批内其余为负样本"""
        z = F.normalize(torch.cat([z1, z2], dim=0), dim=1)
        n = z.size(0)
        sim = z @ z.t() / self.temperature
        sim = sim.masked_fill(torch.eye(n, dtype=torch.bool, device=z.device), float("-inf"))
        labels = torch.cat([torch.arange(n // 2, n), torch.arange(0, n // 2)]).to(z.device)
        return F.cross_entropy(sim, labels)

    def _train_one_epoch(self):
        """训练一个轮次：重建损失（自编码器）+ 对比损失（拉开不同图片）

        返回 (总损失, 重建损失, 对比损失) 的批次加权平均，
        便于外部区分 train_loss 中两部分的量级。
        """
        self.model.train()
        total_loss = 0.0
        total_recon = 0.0
        total_contrastive = 0.0
        total_num = 0

        for input in self.train_loader:
            self._check_cancelled()
            input = input.to(self.device)

            # 两个增强视图
            v1 = self._augment(input)
            v2 = self._augment(input)
            z1 = self.model.encode(v1)
            z2 = self.model.encode(v2)

            recon = (self.loss_fn(self.model.decode(z1), v1)
                     + self.loss_fn(self.model.decode(z2), v2)) / 2
            contrastive = self._contrastive_loss(z1, z2)
            loss = recon + self.contrastive_weight * contrastive

            # 反向传播
            loss.backward()
            self.optimizer.step()
            self.optimizer.zero_grad()

            batch_num = input.shape[0]
            total_loss += loss.item() * batch_num
            total_recon += recon.item() * batch_num
            total_contrastive += contrastive.item() * batch_num
            total_num += batch_num

        if total_num == 0:
            return 0.0, 0.0, 0.0
        return total_loss / total_num, total_recon / total_num, total_contrastive / total_num

    def _validate(self) -> float:
        """验证"""
        self.model.eval()
        total_loss = 0.0
        total_num = 0

        with torch.no_grad():
            for input in self.val_loader:
                input = input.to(self.device)

                embedding = self.model.encode(input)
                output = self.model.decode(embedding)
                loss = self.loss_fn(output, input)

                total_loss += loss.item() * input.shape[0]
                total_num += input.shape[0]

        return total_loss / total_num if total_num > 0 else 0.0

    def _run_training(self) -> None:
        """执行训练循环"""
        logger.info("[train] 开始训练循环: task_id=%s, epochs=%s, device=%s", self.task_id, self.epochs, self.device)

        for epoch in range(1, self.epochs + 1):
            self._check_cancelled()

            train_loss, recon_loss, contrastive_loss = self._train_one_epoch()
            val_loss = self._validate()

            self._update_progress(
                epoch=epoch,
                total_epochs=self.epochs,
                train_loss=train_loss,
                val_loss=val_loss,
                recon_loss=recon_loss,
                contrastive_loss=contrastive_loss,
                status="training",
                message=f"Epoch {epoch}/{self.epochs}",
            )
            logger.info("[train] task_id=%s, epoch=%s/%s, train_loss=%.6f, val_loss=%.6f, recon_loss=%.6f, contrastive_loss=%.6f",
                        self.task_id, epoch, self.epochs, train_loss, val_loss, recon_loss, contrastive_loss)

        # 训练结束只保存一个模型文件（时间戳命名）
        path = self.save_model(self.model)
        logger.info("[train] 保存模型: task_id=%s, path=%s", self.task_id, path)
        logger.info("[train] 训练循环完成: task_id=%s", self.task_id)
