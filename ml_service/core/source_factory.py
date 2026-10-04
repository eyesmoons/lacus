"""数据源工厂

根据 URI 协议路由到对应的数据源实现。
"""
from typing import Dict, Type, Optional
from urllib.parse import urlparse

from core.dataset_source import DatasetSource, SourceCredentials


class SourceFactory:
    """数据源工厂，通过 URI scheme 路由到具体实现"""

    # scheme -> 数据源类
    REGISTRY: Dict[str, Type[DatasetSource]] = {}

    @classmethod
    def register(cls, scheme: str, source_class: Type[DatasetSource]) -> None:
        """注册新的数据源类型"""
        cls.REGISTRY[scheme] = source_class

    @classmethod
    def create(cls, uri: str, credentials: Optional[SourceCredentials] = None) -> DatasetSource:
        """根据 URI 创建对应的数据源实例"""
        parsed = urlparse(uri)
        scheme = parsed.scheme.lower()

        if scheme not in cls.REGISTRY:
            available = ", ".join(sorted(cls.REGISTRY.keys()))
            raise ValueError(
                f"不支持的 URI 协议: '{scheme}'，可用协议: [{available}]"
            )

        source_class = cls.REGISTRY[scheme]
        return source_class(uri=uri, credentials=credentials)
