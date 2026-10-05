"""训练 API 路由

提供训练任务的创建、查询和取消接口。
"""
import os
import threading
import uuid
from typing import Dict
from urllib.parse import urlparse

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from torch.utils.data import DataLoader, random_split

from core.task_registry import TaskRegistry
from core.trainer_base import TrainingProgress

router = APIRouter()

# 任务状态存储
task_store: Dict[str, TrainingProgress] = {}


class TrainRequest(BaseModel):
    """训练请求体"""
    trainer_type: str = "similarity"
    dataset_uri: str
    epochs: int = 10
    batch_size: int = 32
    learning_rate: float = 1e-3
    device: str = "cpu"


class TrainResponse(BaseModel):
    """训练响应"""
    task_id: str
    status: str
    message: str


def _create_and_start_training(request: TrainRequest, task_id: str) -> str:
    """创建并启动训练任务"""
    task_store[task_id] = TrainingProgress(status="training", message="训练已启动")

    def _run():
        try:
            # 1. 解析数据集本地路径
            parsed = urlparse(request.dataset_uri)
            dataset_path = parsed.path or request.dataset_uri
            if not os.path.isdir(dataset_path):
                task_store[task_id].status = "failed"
                task_store[task_id].message = f"数据集路径不存在: {dataset_path}"
                return

            # 2. 创建 ImageDataset 和 DataLoader
            from utils.vector_builder import ImageDataset
            from config import default_config

            dataset = ImageDataset(dataset_path)
            if len(dataset) == 0:
                task_store[task_id].status = "failed"
                task_store[task_id].message = "数据集中没有图片文件"
                return

            # 按 train/val/test 比例划分
            n = len(dataset)
            train_end = int(n * default_config.train_ratio)
            val_end = int(n * (default_config.train_ratio + default_config.val_ratio))
            train_set, val_set, _ = random_split(
                dataset,
                [train_end, val_end - train_end, n - val_end],
            )
            train_loader = DataLoader(train_set, batch_size=request.batch_size, shuffle=True)
            val_loader = DataLoader(val_set, batch_size=request.batch_size, shuffle=False)

            # 3. 通过 TaskRegistry 创建 SimilarityTrainer
            trainer = TaskRegistry.create_trainer(
                request.trainer_type,
                task_id=task_id,
                task_store=task_store,
                train_loader=train_loader,
                val_loader=val_loader,
                epochs=request.epochs,
                learning_rate=request.learning_rate,
                device=request.device,
            )

            # 4. 注册活跃训练器，使取消训练生效
            _active_trainers[task_id] = trainer

            # 5. 启动训练（内部会更新 task_store 状态）
            trainer.train()
        except Exception as e:
            task_store[task_id].status = "failed"
            task_store[task_id].message = str(e)

    thread = threading.Thread(target=_run, daemon=True)
    thread.start()
    return task_id


@router.post("/api/train", response_model=TrainResponse)
async def create_train(request: TrainRequest):
    """创建训练任务"""
    # 使用传入的 task_id（来自 Java 端），如果没有则生成新的
    task_id = request.task_id if hasattr(request, 'task_id') and request.task_id else str(uuid.uuid4())
    _create_and_start_training(request, task_id)
    return TrainResponse(
        task_id=task_id,
        status="training",
        message="训练任务已创建",
    )


@router.get("/api/train/{task_id}")
async def get_train_status(task_id: str):
    """获取训练任务状态"""
    if task_id not in task_store:
        raise HTTPException(status_code=404, detail=f"任务 {task_id} 不存在")
    progress = task_store[task_id]
    return {
        "task_id": task_id,
        "epoch": progress.epoch,
        "total_epochs": progress.total_epochs,
        "train_loss": progress.train_loss,
        "val_loss": progress.val_loss,
        "status": progress.status,
        "message": progress.message,
    }


@router.post("/api/train/{task_id}/cancel")
async def cancel_train(task_id: str):
    """取消训练任务"""
    if task_id not in task_store:
        raise HTTPException(status_code=404, detail=f"任务 {task_id} 不存在")

    progress = task_store[task_id]
    progress.status = "cancelled"
    progress.message = "训练已取消"

    # 如果有训练器实例，调用取消
    trainer = _active_trainers.get(task_id)
    if trainer:
        trainer.cancel()

    return {"task_id": task_id, "status": "cancelled"}


# 活跃训练器实例
_active_trainers: Dict[str, object] = {}
