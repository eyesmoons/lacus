"""DatasetSource 和 SourceFactory 测试"""
import pytest
from unittest.mock import MagicMock, patch

# 导入 sources 包以触发注册
import sources  # noqa: F401

from core.dataset_source import SourceCredentials, DatasetSource
from core.source_factory import SourceFactory


class TestSourceCredentials:
    """测试 SourceCredentials 数据类"""

    def test_default_values(self):
        creds = SourceCredentials()
        assert creds.username is None
        assert creds.password is None
        assert creds.token is None
        assert creds.extra == {}

    def test_custom_values(self):
        creds = SourceCredentials(
            username="admin", password="secret",
            token="abc123", extra={"endpoint": "http://localhost:9000"}
        )
        assert creds.username == "admin"
        assert creds.password == "secret"
        assert creds.token == "abc123"
        assert creds.extra["endpoint"] == "http://localhost:9000"


class TestDatasetSource:
    """测试 DatasetSource 抽象基类"""

    def test_is_abstract(self):
        """验证 DatasetSource 不能直接实例化"""
        with pytest.raises(TypeError):
            DatasetSource(uri="file:///test")

    def test_concrete_subclass_must_implement(self):
        """验证子类必须实现所有抽象方法"""
        class IncompleteSource(DatasetSource):
            def probe(self):
                return 0

        with pytest.raises(TypeError):
            IncompleteSource(uri="file:///test")


class TestSourceFactory:
    """测试 SourceFactory 工厂"""

    def test_create_local_source(self):
        source = SourceFactory.create("file:///data/images")
        from sources.local_source import LocalSource
        assert isinstance(source, LocalSource)

    def test_create_hdfs_source(self):
        source = SourceFactory.create("hdfs://namenode:9000/data/images")
        from sources.hdfs_source import HDFSSource
        assert isinstance(source, HDFSSource)

    def test_create_s3_source(self):
        source = SourceFactory.create("s3://mybucket/images")
        from sources.s3_source import S3Source
        assert isinstance(source, S3Source)

    def test_create_minio_source(self):
        source = SourceFactory.create("minio://mybucket/images")
        from sources.s3_source import S3Source
        assert isinstance(source, S3Source)

    def test_create_http_source(self):
        source = SourceFactory.create("http://example.com/data.zip")
        from sources.http_source import HTTPSource
        assert isinstance(source, HTTPSource)

    def test_create_https_source(self):
        source = SourceFactory.create("https://example.com/data.zip")
        from sources.http_source import HTTPSource
        assert isinstance(source, HTTPSource)

    def test_unknown_scheme_raises(self):
        with pytest.raises(ValueError, match="不支持的 URI 协议"):
            SourceFactory.create("ftp://example.com/data")

    def test_register_custom_scheme(self):
        """测试注册自定义协议"""
        mock_class = MagicMock()
        SourceFactory.register("custom", mock_class)
        assert "custom" in SourceFactory.REGISTRY
        # 清理
        del SourceFactory.REGISTRY["custom"]
