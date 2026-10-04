"""向量和检索 API 路由测试"""
import pytest
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


class TestVectorsRouter:
    """测试向量构建 API 路由"""

    def test_build_vectors_endpoint_exists(self):
        """验证 POST /api/vectors/build 端点存在"""
        from routers.vectors import router
        routes = [r.path for r in router.routes]
        assert "/api/vectors/build" in routes

    def test_get_build_status_endpoint_exists(self):
        """验证 GET /api/vectors/build/{task_id} 端点存在"""
        from routers.vectors import router
        routes = [r.path for r in router.routes]
        assert "/api/vectors/build/{task_id}" in routes

    def test_build_vectors_returns_task_id(self):
        """测试启动向量构建返回 task_id"""
        from fastapi.testclient import TestClient
        from app import app

        client = TestClient(app)

        with patch("routers.vectors._start_vector_build") as mock_start:
            response = client.post(
                "/api/vectors/build",
                json={
                    "dataset_uri": "file:///data/images",
                    "collection_name": "test_collection",
                }
            )

        assert response.status_code == 200
        data = response.json()
        assert "task_id" in data
        assert data["status"] == "building"


class TestSearchRouter:
    """测试检索 API 路由"""

    def test_search_endpoint_exists(self):
        """验证 POST /api/search 端点存在"""
        from routers.search import router
        routes = [r.path for r in router.routes]
        assert "/api/search" in routes

    def test_search_returns_results(self):
        """测试搜索返回结果"""
        from fastapi.testclient import TestClient
        from app import app

        client = TestClient(app)

        # 模拟搜索结果
        with patch("routers.search._perform_search") as mock_search:
            mock_search.return_value = {
                "results": [
                    {"id": "0", "distance": 0.1},
                    {"id": "1", "distance": 0.2},
                    {"id": "2", "distance": 0.3},
                ]
            }
            response = client.post(
                "/api/search",
                data={
                    "image_id": "query_image",
                    "top_k": 3,
                }
            )

        assert response.status_code == 200
        data = response.json()
        assert len(data["results"]) == 3


from unittest.mock import patch
