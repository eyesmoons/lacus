"""HTTPSource 测试"""
import os
import pytest
import tempfile
from unittest.mock import MagicMock, patch
import sys
import io
import zipfile

from sources.http_source import HTTPSource

# 模拟 requests 模块
_mock_requests = MagicMock()
sys.modules.setdefault("requests", _mock_requests)


def _create_fake_zip_bytes():
    """创建包含图片的模拟 zip 字节"""
    buf = io.BytesIO()
    with zipfile.ZipFile(buf, "w") as zf:
        # 添加模拟图片文件
        zf.writestr("img1.png", b"fake png data")
        zf.writestr("img2.jpg", b"fake jpg data")
        zf.writestr("readme.txt", b"not an image")
    return buf.getvalue()


class TestHTTPSource:
    """测试 HTTP 数据源"""

    def test_extract_url(self):
        """测试从 URI 提取 URL"""
        source = HTTPSource(uri="http://example.com/data.zip")
        assert source.url == "http://example.com/data.zip"

    def test_https_url(self):
        """测试 HTTPS URL"""
        source = HTTPSource(uri="https://example.com/data.zip")
        assert source.url == "https://example.com/data.zip"

    def test_download_zip(self):
        """测试下载 zip 文件"""
        _mock_requests.reset_mock()
        mock_response = MagicMock()
        mock_response.status_code = 200
        mock_response.content = _create_fake_zip_bytes()
        mock_response.headers = {"Content-Type": "application/zip"}
        _mock_requests.get.return_value = mock_response

        with tempfile.TemporaryDirectory() as tmpdir:
            source = HTTPSource(uri="http://example.com/data.zip")
            result = source.download(tmpdir)

            assert result == tmpdir
            # 验证图片文件被解压
            files = os.listdir(tmpdir)
            assert "img1.png" in files
            assert "img2.jpg" in files
            assert "readme.txt" not in files  # 非图片文件不应被解压

    def test_probe_from_download(self):
        """测试 probe 返回 -1（未知）"""
        source = HTTPSource(uri="http://example.com/data.zip")
        count = source.probe()
        assert count == -1

    def test_get_local_path_raises(self):
        """测试 get_local_path 不支持"""
        source = HTTPSource(uri="http://example.com/data.zip")
        with pytest.raises(NotImplementedError):
            source.get_local_path()

    def test_download_with_auth(self):
        """测试带认证的下载"""
        _mock_requests.reset_mock()
        mock_response = MagicMock()
        mock_response.status_code = 200
        mock_response.content = _create_fake_zip_bytes()
        mock_response.headers = {"Content-Type": "application/zip"}
        _mock_requests.get.return_value = mock_response

        from core.dataset_source import SourceCredentials
        creds = SourceCredentials(username="user", password="pass")
        source = HTTPSource(uri="http://example.com/data.zip", credentials=creds)

        with tempfile.TemporaryDirectory() as tmpdir:
            source.download(tmpdir)

        # 验证带 auth 参数
        call_kwargs = _mock_requests.get.call_args[1]
        assert "auth" in call_kwargs
        assert call_kwargs["auth"] == ("user", "pass")
