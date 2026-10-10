"""向量构建 API 路由

提供向量构建任务的创建和状态查询接口。
"""
import threading
import uuid
from typing import Dict, Optional

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from core.trainer_base import TrainingProgress

router = APIRouter()

# 向量构建任务状态存储
build_task_store: Dict[str, TrainingProgress] = {}


class VectorBuildRequest(BaseModel):
    """向量构建请求体"""
    dataset_uri: str
    collection_name: str = "image_collection"
    batch_size: int = 32
    dataset_id: Optional[int] = None
    model_path: Optional[str] = None
    distance_metric: Optional[str] = "cosine"


class VectorBuildResponse(BaseModel):
    """向量构建响应"""
    task_id: str
    status: str
    message: str


def _start_vector_build(request: VectorBuildRequest, task_id: str) -> str:
    """启动向量构建任务"""
    build_task_store[task_id] = TrainingProgress(status="building", message="向量构建已启动")

    def _run():
        try:
            from utils.vector_builder import do_build_vectors
            do_build_vectors(
                dataset_uri=request.dataset_uri,
                collection_name=request.collection_name,
                task_id=task_id,
                task_store=build_task_store,
                dataset_id=request.dataset_id,
                model_path=request.model_path,
                distance_metric=request.distance_metric,
            )
        except Exception as e:
            build_task_store[task_id].status = "failed"
            build_task_store[task_id].message = str(e)

    thread = threading.Thread(target=_run, daemon=True)
    thread.start()
    return task_id


@router.post("/api/vectors/build", response_model=VectorBuildResponse)
async def build_vectors(request: VectorBuildRequest):
    """启动向量构建任务"""
    task_id = str(uuid.uuid4())
    _start_vector_build(request, task_id)
    return VectorBuildResponse(
        task_id=task_id,
        status="building",
        message="向量构建任务已创建",
    )


@router.get("/api/vectors/build/{task_id}")
async def get_build_status(task_id: str):
    """获取向量构建状态"""
    if task_id not in build_task_store:
        raise HTTPException(status_code=404, detail=f"任务 {task_id} 不存在")
    progress = build_task_store[task_id]
    return {
        "task_id": task_id,
        "progress": progress.epoch,  # 复用 epoch 字段表示进度
        "total": progress.total_epochs,
        "status": progress.status,
        "message": progress.message,
    }
