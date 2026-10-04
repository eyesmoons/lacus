"""数据源探测 API 路由测试"""
import pytest
import os
import sys
from unittest.mock import patch

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


class TestDatasetSourceRouter:
    """测试数据源探测 API 路由"""

    def test_probe_source_endpoint_exists(self):
        """验证 POST /api/dataset/probe-source 端点存在"""
        from routers.dataset_source import router
        routes = [r.path for r in router.routes]
        assert "/api/dataset/probe-source" in routes

    def test_probe_source_returns_count(self):
        """测试探测数据源返回数量"""
        from fastapi.testclient import TestClient
        from app import app

        client = TestClient(app)

        with patch("routers.dataset_source.SourceFactory.create") as mock_create:
            mock_source = mock_create.return_value
            mock_source.probe.return_value = 42

            response = client.post(
                "/api/dataset/probe-source",
                json={"uri": "file:///data/images"}
            )

        assert response.status_code == 200
        data = response.json()
        assert data["uri"] == "file:///data/images"
        assert data["count"] == 42
        assert data["accessible"] is True

    def test_probe_source_error_returns_accessible_false(self):
        """测试探测失败返回 accessible=False"""
        from fastapi.testclient import TestClient
        from app import app

        client = TestClient(app)

        with patch("routers.dataset_source.SourceFactory.create") as mock_create:
            mock_source = mock_create.return_value
            mock_source.probe.side_effect = Exception("Connection refused")

            response = client.post(
                "/api/dataset/probe-source",
                json={"uri": "hdfs://badhost/data"}
            )

        assert response.status_code == 200
        data = response.json()
        assert data["accessible"] is False
        assert "Connection refused" in data["error"]
