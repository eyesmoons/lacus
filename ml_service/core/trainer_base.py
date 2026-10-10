"""训练器基类

提供通用训练循环、进度回调、取消检查点等基础能力。
"""
import logging
import os
import random
from dataclasses import dataclass, field
from typing import Callable, Dict, Optional

import numpy as np
import torch

from config import default_config

logger = logging.getLogger(__name__)


def resolve_device(device: str) -> torch.device:
    """解析训练设备（cpu/cuda/mps）；请求的设备不可用时回退到 CPU"""
    d = (device or "cpu").lower()
    if d == "cuda" and not torch.cuda.is_available():
        logger.warning("CUDA 不可用，回退到 CPU")
        return torch.device("cpu")
    if d == "mps" and not (hasattr(torch.backends, "mps") and torch.backends.mps.is_available()):
        logger.warning("MPS 不可用，回退到 CPU")
        return torch.device("cpu")
    return torch.device(d)


@dataclass
class TrainingProgress:
    """训练进度数据类"""
    epoch: int = 0
    total_epochs: int = 0
    train_loss: float = 0.0
    val_loss: float = 0.0
    # 训练损失的构成项，用于区分重建与对比两部分（train_loss = recon_loss + weight * contrastive_loss）
    recon_loss: float = 0.0
    contrastive_loss: float = 0.0
    status: str = "idle"  # idle | training | completed | cancelled | failed
    message: str = ""
    model_path: str = ""


# 进度回调类型
ProgressCallback = Callable[[TrainingProgress], None]


class BaseTrainer:
    """训练器基类

    提供通用训练框架，子类需实现 _run_training 方法。
    通过 task_store 字典共享进度状态。
    """

    def __init__(
        self,
        task_id: str,
        task_store: Dict[str, TrainingProgress],
        progress_callback: Optional[ProgressCallback] = None,
    ):
        self.task_id = task_id
        self.task_store = task_store
        self.progress_callback = progress_callback
        self._cancelled = False
        # 固定随机种子，保证训练可复现（模型初始化、数据打乱）
        seed = default_config.seed
        random.seed(seed)
        np.random.seed(seed)
        torch.manual_seed(seed)
        # 初始化任务状态
        self.task_store[self.task_id] = TrainingProgress()

    def cancel(self) -> None:
        """请求取消训练"""
        self._cancelled = True

    def is_cancelled(self) -> bool:
        """检查是否已请求取消"""
        return self._cancelled

    def _check_cancelled(self) -> None:
        """检查取消状态，若已取消则抛出异常"""
        if self._cancelled:
            self._update_progress(status="cancelled", message="训练已取消")
            raise InterruptedError("训练已取消")

    def _update_progress(
        self,
        epoch: int = 0,
        total_epochs: int = 0,
        train_loss: float = 0.0,
        val_loss: float = 0.0,
        recon_loss: float = 0.0,
        contrastive_loss: float = 0.0,
        status: str = "idle",
        message: str = "",
    ) -> None:
        """更新训练进度"""
        existing = self.task_store.get(self.task_id)
        progress = TrainingProgress(
            epoch=epoch,
            total_epochs=total_epochs,
            train_loss=train_loss,
            val_loss=val_loss,
            recon_loss=recon_loss,
            contrastive_loss=contrastive_loss,
            status=status,
            message=message,
            model_path=existing.model_path if existing else "",
        )
        self.task_store[self.task_id] = progress
        if self.progress_callback:
            self.progress_callback(progress)

    def get_progress(self) -> TrainingProgress:
        """获取当前训练进度"""
        return self.task_store.get(self.task_id, TrainingProgress())

    def save_model(self, model: torch.nn.Module, model_dir: str = None) -> str:
        """保存模型：每个训练只产生一个文件，以时间戳命名 {task_id}_{yyyyMMddHHmmss}.pt"""
        import datetime
        abs_dir = os.path.abspath(model_dir or default_config.model_dir)
        os.makedirs(abs_dir, exist_ok=True)
        ts = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
        path = os.path.join(abs_dir, f"{self.task_id}_{ts}.pt")
        torch.save(model.state_dict(), path)
        # 更新 task_store 中的 model_path
        if self.task_id in self.task_store:
            self.task_store[self.task_id].model_path = path
        return path

    def _run_training(self) -> None:
        """子类实现具体训练逻辑"""
        raise NotImplementedError

    def train(self) -> None:
        """启动训练"""
        logger.info("[train] 训练器启动: task_id=%s", self.task_id)
        try:
            self._update_progress(status="training", message="训练开始")
            self._run_training()
            # 保留最后一轮的 epoch 信息，仅更新状态
            current = self.get_progress()
            self._update_progress(
                epoch=current.epoch,
                total_epochs=current.total_epochs,
                train_loss=current.train_loss,
                val_loss=current.val_loss,
                recon_loss=current.recon_loss,
                contrastive_loss=current.contrastive_loss,
                status="completed",
                message="训练完成",
            )
            logger.info("[train] 训练完成: task_id=%s, epoch=%s/%s, train_loss=%.6f, val_loss=%.6f, recon_loss=%.6f, contrastive_loss=%.6f",
                        self.task_id, current.epoch, current.total_epochs, current.train_loss, current.val_loss,
                        current.recon_loss, current.contrastive_loss)
        except InterruptedError:
            logger.info("[train] 训练已取消: task_id=%s", self.task_id)
            pass
        except Exception as e:
            logger.exception("[train] 训练失败: task_id=%s, error=%s", self.task_id, str(e))
            self._update_progress(status="failed", message=str(e))
            raise
