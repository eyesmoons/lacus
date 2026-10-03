"""S3Source 测试"""
import pytest
from unittest.mock import MagicMock, patch
import sys

from core.dataset_source import SourceCredentials
from sources.s3_source import S3Source

# 模拟 boto3 模块
_mock_boto3 = MagicMock()
sys.modules.setdefault("boto3", _mock_boto3)


class TestS3Source:
    """测试 S3/MinIO 数据源"""

    def test_extract_bucket_and_prefix(self):
        """测试从 URI 提取 bucket 和 prefix"""
        source = S3Source(uri="s3://mybucket/images")
        assert source.bucket == "mybucket"
        assert source.prefix == "images"

    def test_extract_with_deep_prefix(self):
        """测试深层前缀"""
        source = S3Source(uri="s3://data/images/train/cats")
        assert source.bucket == "data"
        assert source.prefix == "images/train/cats"

    def test_extract_minio_scheme(self):
        """测试 minio scheme"""
        source = S3Source(uri="minio://mybucket/images")
        assert source.bucket == "mybucket"
        assert source.prefix == "images"

    def test_probe_returns_count(self):
        """测试 probe 返回图片数量"""
        _mock_boto3.reset_mock()
        mock_client = MagicMock()
        _mock_boto3.client.return_value = mock_client

        # 模拟 list_objects_v2 返回
        mock_client.list_objects_v2.return_value = {
            "Contents": [
                {"Key": "images/cat1.png"},
                {"Key": "images/cat2.jpg"},
                {"Key": "images/readme.txt"},
            ]
        }

        creds = SourceCredentials(
            username="AKIAIOSFODNN7EXAMPLE",
            password="wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
            extra={"endpoint_url": "http://localhost:9000"},
        )
        source = S3Source(uri="s3://bucket/images", credentials=creds)
        count = source.probe()
        assert count == 2

    def test_download(self):
        """测试下载文件"""
        _mock_boto3.reset_mock()
        mock_client = MagicMock()
        _mock_boto3.client.return_value = mock_client
        mock_client.list_objects_v2.return_value = {
            "Contents": [
                {"Key": "images/img1.png"},
                {"Key": "images/img2.jpg"},
            ]
        }

        source = S3Source(uri="s3://bucket/images")
        result = source.download("/tmp/s3_download")

        assert result == "/tmp/s3_download"
        assert mock_client.download_file.call_count == 2

    def test_get_local_path_raises(self):
        """测试 get_local_path 不支持"""
        source = S3Source(uri="s3://bucket/images")
        with pytest.raises(NotImplementedError):
            source.get_local_path()

    def test_probe_pagination(self):
        """测试分页获取全部对象"""
        _mock_boto3.reset_mock()
        mock_client = MagicMock()
        _mock_boto3.client.return_value = mock_client

        # 模拟分页响应
        mock_client.list_objects_v2.side_effect = [
            {
                "Contents": [{"Key": f"images/img{i}.png"} for i in range(100)],
                "IsTruncated": True,
                "NextContinuationToken": "token1",
            },
            {
                "Contents": [{"Key": f"images/img{i}.png"} for i in range(100, 150)],
                "IsTruncated": False,
            },
        ]

        source = S3Source(uri="s3://bucket/images")
        count = source.probe()
        assert count == 150
