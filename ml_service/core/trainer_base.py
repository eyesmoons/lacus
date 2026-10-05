"""训练器基类

提供通用训练循环、进度回调、取消检查点等基础能力。
"""
import logging
import os
from dataclasses import dataclass, field
from typing import Callable, Dict, Optional

import torch

logger = logging.getLogger(__name__)


@dataclass
class TrainingProgress:
    """训练进度数据类"""
    epoch: int = 0
    total_epochs: int = 0
    train_loss: float = 0.0
    val_loss: float = 0.0
    status: str = "idle"  # idle | training | completed | cancelled | failed
    message: str = ""


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
        status: str = "idle",
        message: str = "",
    ) -> None:
        """更新训练进度"""
        progress = TrainingProgress(
            epoch=epoch,
            total_epochs=total_epochs,
            train_loss=train_loss,
            val_loss=val_loss,
            status=status,
            message=message,
        )
        self.task_store[self.task_id] = progress
        if self.progress_callback:
            self.progress_callback(progress)

    def get_progress(self) -> TrainingProgress:
        """获取当前训练进度"""
        return self.task_store.get(self.task_id, TrainingProgress())

    def save_checkpoint(self, model: torch.nn.Module, epoch: int, model_dir: str = "./model_weights") -> str:
        """保存模型检查点"""
        os.makedirs(model_dir, exist_ok=True)
        path = os.path.join(model_dir, f"{self.task_id}_epoch_{epoch}.pt")
        torch.save(model.state_dict(), path)
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
                status="completed",
                message="训练完成",
            )
            logger.info("[train] 训练完成: task_id=%s, epoch=%s/%s, train_loss=%.6f, val_loss=%.6f",
                        self.task_id, current.epoch, current.total_epochs, current.train_loss, current.val_loss)
        except InterruptedError:
            logger.info("[train] 训练已取消: task_id=%s", self.task_id)
            pass
        except Exception as e:
            logger.exception("[train] 训练失败: task_id=%s, error=%s", self.task_id, str(e))
            self._update_progress(status="failed", message=str(e))
            raise
