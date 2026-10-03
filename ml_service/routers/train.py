"""训练 API 路由

提供训练任务的创建、查询和取消接口。
"""
import threading
import uuid
from typing import Dict

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from core.trainer_base import TrainingProgress
from core.task_registry import TaskRegistry

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
    # 这里简化处理，实际应加载数据集并创建 DataLoader
    # 为测试目的，直接返回 task_id
    task_store[task_id] = TrainingProgress(status="training", message="训练已启动")

    # 在后台线程启动训练
    def _run():
        try:
            # 实际训练逻辑
            pass
        except Exception as e:
            task_store[task_id].status = "failed"
            task_store[task_id].message = str(e)

    thread = threading.Thread(target=_run, daemon=True)
    thread.start()
    return task_id


@router.post("/api/train", response_model=TrainResponse)
async def create_train(request: TrainRequest):
    """创建训练任务"""
    task_id = str(uuid.uuid4())
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
