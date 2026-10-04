"""数据集数据源抽象基类"""
from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any, Dict, Optional


@dataclass
class SourceCredentials:
    """数据源认证凭据"""
    username: Optional[str] = None
    password: Optional[str] = None
    token: Optional[str] = None
    extra: Dict[str, Any] = field(default_factory=dict)


class DatasetSource(ABC):
    """数据集数据源抽象基类

    所有数据源需实现 probe/download/get_local_path 三个方法。
    """

    def __init__(self, uri: str, credentials: Optional[SourceCredentials] = None):
        self.uri = uri
        self.credentials = credentials or SourceCredentials()

    @abstractmethod
    def probe(self) -> int:
        """探测数据源，返回可获取的文件数量"""
        ...

    @abstractmethod
    def download(self, target_dir: str) -> str:
        """下载数据到目标目录，返回本地路径"""
        ...

    @abstractmethod
    def get_local_path(self) -> str:
        """获取本地可直接访问的路径"""
        ...
