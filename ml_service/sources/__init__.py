"""数据源注册模块

在导入时自动将所有数据源注册到 SourceFactory。
"""
from sources.local_source import LocalSource
from sources.hdfs_source import HDFSSource
from sources.s3_source import S3Source
from sources.http_source import HTTPSource
from core.source_factory import SourceFactory

# 注册所有数据源
SourceFactory.register("file", LocalSource)
SourceFactory.register("hdfs", HDFSSource)
SourceFactory.register("s3", S3Source)
SourceFactory.register("minio", S3Source)
SourceFactory.register("http", HTTPSource)
SourceFactory.register("https", HTTPSource)
