"""ML 服务应用入口"""
import os
from fastapi import FastAPI

from routers import train, vectors, search, dataset_source


def create_app() -> FastAPI:
    """创建 FastAPI 应用实例"""
    app = FastAPI(
        title="湖智 ML 服务",
        description="图像相似度训练与检索服务",
        version="1.0.0",
    )

    # 注册路由
    app.include_router(train.router)
    app.include_router(vectors.router)
    app.include_router(search.router)
    app.include_router(dataset_source.router)

    # 确保必要目录存在
    from config import default_config
    os.makedirs(default_config.model_dir, exist_ok=True)
    os.makedirs(default_config.task_temp_dir, exist_ok=True)

    return app


app = create_app()
