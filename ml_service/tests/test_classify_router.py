"""分类推理 API 路由测试"""
import io
import os
import sys
from unittest.mock import MagicMock, patch

import pytest
import torch
import torchvision.transforms as T
from fastapi.testclient import TestClient
from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def _make_test_image_bytes() -> bytes:
    """生成一张 64x64 的测试图片字节"""
    img = Image.new("RGB", (64, 64), color=(128, 64, 32))
    buf = io.BytesIO()
    img.save(buf, format="PNG")
    return buf.getvalue()


class TestClassifyRouter:
    """测试分类推理 API 路由"""

    def test_classify_endpoint_exists(self):
        """验证 POST /api/classify 端点存在"""
        from routers.classify import router
        routes = [r.path for r in router.routes]
        assert "/api/classify" in routes

    def test_classify_returns_class_name_confidence_class_id(self):
        """测试分类推理返回 class_name, confidence, class_id"""
        # 构造一个已知输出的 mock 模型
        mock_model = MagicMock()
        mock_model.eval.return_value = None
        # 模拟 logits 输出：batch=1, n_classes=5，第 2 类置信度最高
        fake_logits = torch.tensor([[0.1, 0.2, 5.0, 0.5, 0.3]])
        mock_model.return_value = fake_logits

        with patch("routers.classify.ModelCache") as mock_cache_cls:
            mock_cache = MagicMock()
            mock_cache.get_model.return_value = mock_model
            mock_cache_cls.return_value = mock_cache

            from app import app
            client = TestClient(app)

            response = client.post(
                "/api/classify",
                files={"image": ("test.png", _make_test_image_bytes(), "image/png")},
                data={"model_id": "cls-001"},
            )

        assert response.status_code == 200, response.text
        data = response.json()
        assert "class_name" in data
        assert "confidence" in data
        assert "class_id" in data
        # 第 2 类 logit 最高
        assert data["class_id"] == 2
        assert 0.0 <= data["confidence"] <= 1.0

    def test_classify_without_image_returns_422(self):
        """测试未提供图片时返回 422"""
        from app import app
        client = TestClient(app)

        response = client.post(
            "/api/classify",
            data={"model_id": "cls-001"},
        )
        assert response.status_code == 422

    def test_classify_uses_model_id(self):
        """测试分类推理使用指定的 model_id 加载模型"""
        mock_model = MagicMock()
        mock_model.eval.return_value = None
        mock_model.return_value = torch.tensor([[1.0, 2.0, 3.0, 4.0, 5.0]])

        with patch("routers.classify.ModelCache") as mock_cache_cls:
            mock_cache = MagicMock()
            mock_cache.get_model.return_value = mock_model
            mock_cache_cls.return_value = mock_cache

            from app import app
            client = TestClient(app)

            response = client.post(
                "/api/classify",
                files={"image": ("test.png", _make_test_image_bytes(), "image/png")},
                data={"model_id": "my-custom-model"},
            )

        assert response.status_code == 200
        # 验证 get_model 被调用时传入了 model_id
        mock_cache.get_model.assert_called_once()
        call_args = mock_cache.get_model.call_args
        assert "my-custom-model" in str(call_args)

    def test_classify_confidence_is_softmax(self):
        """测试 confidence 是 softmax 概率值"""
        mock_model = MagicMock()
        mock_model.eval.return_value = None
        # 构造 logits，第 0 类最高
        mock_model.return_value = torch.tensor([[10.0, 0.0, 0.0, 0.0, 0.0]])

        with patch("routers.classify.ModelCache") as mock_cache_cls:
            mock_cache = MagicMock()
            mock_cache.get_model.return_value = mock_model
            mock_cache_cls.return_value = mock_cache

            from app import app
            client = TestClient(app)

            response = client.post(
                "/api/classify",
                files={"image": ("test.png", _make_test_image_bytes(), "image/png")},
                data={"model_id": "cls-001"},
            )

        assert response.status_code == 200
        data = response.json()
        # softmax 后第 0 类概率应接近 1.0
        assert data["confidence"] > 0.9
        assert data["class_id"] == 0
