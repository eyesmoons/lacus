"""训练 API 路由测试"""
import pytest
from unittest.mock import MagicMock, patch
import sys
import os

# 确保 ml_service 在路径中
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


class TestTrainRouter:
    """测试训练 API 路由"""

    def test_create_train_endpoint_exists(self):
        """验证 POST /api/train 端点存在"""
        from routers.train import router
        routes = [r.path for r in router.routes]
        assert "/api/train" in routes

    def test_get_train_status_endpoint_exists(self):
        """验证 GET /api/train/{task_id} 端点存在"""
        from routers.train import router
        routes = [r.path for r in router.routes]
        assert "/api/train/{task_id}" in routes

    def test_cancel_train_endpoint_exists(self):
        """验证 POST /api/train/{task_id}/cancel 端点存在"""
        from routers.train import router
        routes = [r.path for r in router.routes]
        assert "/api/train/{task_id}/cancel" in routes

    def test_create_train_returns_task_id(self):
        """测试创建训练任务返回 task_id"""
        from fastapi.testclient import TestClient
        from app import app

        client = TestClient(app)

        response = client.post(
            "/api/train",
            json={
                "trainer_type": "similarity",
                "dataset_uri": "file:///data/images",
                "epochs": 5,
            }
        )

        assert response.status_code == 200
        data = response.json()
        assert "task_id" in data
        assert data["status"] == "training"

    def test_get_train_status(self):
        """测试获取训练状态"""
        from fastapi.testclient import TestClient
        from app import app
        from routers.train import task_store

        client = TestClient(app)

        # 预设任务状态
        from core.trainer_base import TrainingProgress
        task_store["task-999"] = TrainingProgress(
            epoch=3, total_epochs=10,
            train_loss=0.1, val_loss=0.08,
            status="training"
        )

        response = client.get("/api/train/task-999")
        assert response.status_code == 200
        data = response.json()
        assert data["epoch"] == 3
        assert data["status"] == "training"

    def test_get_nonexistent_task(self):
        """测试获取不存在的任务"""
        from fastapi.testclient import TestClient
        from app import app

        client = TestClient(app)
        response = client.get("/api/train/nonexistent-task")
        assert response.status_code == 404

    def test_cancel_train(self):
        """测试取消训练任务"""
        from fastapi.testclient import TestClient
        from app import app
        from routers.train import task_store

        client = TestClient(app)

        # 预设任务状态
        from core.trainer_base import TrainingProgress
        task_store["task-cancel"] = TrainingProgress(status="training")

        response = client.post("/api/train/task-cancel/cancel")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "cancelled"
