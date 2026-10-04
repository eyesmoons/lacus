"""HDFSSource 测试"""
import pytest
from unittest.mock import MagicMock, patch
import sys

from core.dataset_source import SourceCredentials
from sources.hdfs_source import HDFSSource

# 模拟 hdfs 模块
_mock_hdfs = MagicMock()
sys.modules.setdefault("hdfs", _mock_hdfs)


class TestHDFSSource:
    """测试 HDFS 数据源"""

    def test_extract_namenode_and_path(self):
        """测试从 URI 提取 namenode 和路径"""
        source = HDFSSource(uri="hdfs://namenode:9000/data/images")
        assert source.namenode == "namenode:9000"
        assert source.hdfs_path == "/data/images"

    def test_extract_with_default_port(self):
        """测试默认端口"""
        source = HDFSSource(uri="hdfs://namenode/data/images")
        assert source.namenode == "namenode:8020"

    def test_probe_returns_file_count(self):
        """测试 probe 返回文件数量"""
        _mock_hdfs.InsecureClient.reset_mock()
        mock_client = MagicMock()
        _mock_hdfs.InsecureClient.return_value = mock_client
        mock_client.list.return_value = [
            ("/data/images/img1.png", {"type": "FILE"}),
            ("/data/images/img2.jpg", {"type": "FILE"}),
            ("/data/images/subdir", {"type": "DIRECTORY"}),
        ]

        source = HDFSSource(uri="hdfs://namenode:9000/data/images")
        count = source.probe()
        assert count == 2

    def test_download(self):
        """测试下载文件"""
        _mock_hdfs.InsecureClient.reset_mock()
        mock_client = MagicMock()
        _mock_hdfs.InsecureClient.return_value = mock_client
        mock_client.list.return_value = [
            ("/data/images/img1.png", {"type": "FILE"}),
            ("/data/images/img2.jpg", {"type": "FILE"}),
        ]

        source = HDFSSource(uri="hdfs://namenode:9000/data/images")
        result = source.download("/tmp/test_download")

        assert result == "/tmp/test_download"
        assert mock_client.download.call_count == 2

    def test_get_local_path_raises(self):
        """测试 get_local_path 抛出异常（HDFS 非本地）"""
        source = HDFSSource(uri="hdfs://namenode:9000/data/images")
        with pytest.raises(NotImplementedError):
            source.get_local_path()

    def test_with_credentials(self):
        """测试使用认证凭据"""
        _mock_hdfs.InsecureClient.reset_mock()
        mock_client = MagicMock()
        _mock_hdfs.InsecureClient.return_value = mock_client
        mock_client.list.return_value = []

        creds = SourceCredentials(username="testuser")
        source = HDFSSource(uri="hdfs://namenode:9000/data", credentials=creds)
        source.probe()

        # 验证 InsecureClient 使用了用户名
        _mock_hdfs.InsecureClient.assert_called_once()
        call_kwargs = _mock_hdfs.InsecureClient.call_args[1]
        assert call_kwargs.get("user") == "testuser"
