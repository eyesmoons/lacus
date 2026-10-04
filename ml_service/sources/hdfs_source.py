"""HDFS 数据源

使用 hdfs 库（InsecureClient）访问 HDFS 文件系统。
"""
import os
from urllib.parse import urlparse

from core.dataset_source import DatasetSource

IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".bmp", ".gif", ".tiff", ".webp"}


class HDFSSource(DatasetSource):
    """HDFS 数据源"""

    def __init__(self, uri, credentials=None):
        super().__init__(uri, credentials)
        parsed = urlparse(uri)
        self.namenode = parsed.hostname
        if parsed.port:
            self.namenode = f"{parsed.hostname}:{parsed.port}"
        else:
            self.namenode = f"{parsed.hostname}:8020"
        self.hdfs_path = parsed.path

    def _get_client(self):
        """获取 HDFS 客户端"""
        from hdfs import InsecureClient
        return InsecureClient(
            f"http://{self.namenode}",
            user=self.credentials.username,
        )

    def probe(self) -> int:
        """探测 HDFS 目录中图片文件数量"""
        client = self._get_client()
        try:
            entries = client.list(self.hdfs_path, status=True)
        except Exception:
            return 0

        count = 0
        for name, status in entries:
            basename = os.path.basename(name)
            ext = os.path.splitext(basename)[1].lower()
            if ext in IMAGE_EXTENSIONS and status["type"] == "FILE":
                count += 1
        return count

    def download(self, target_dir: str) -> str:
        """下载 HDFS 目录中的图片到本地"""
        os.makedirs(target_dir, exist_ok=True)
        client = self._get_client()

        entries = client.list(self.hdfs_path, status=True)
        for name, status in entries:
            if status["type"] == "FILE":
                ext = os.path.splitext(name)[1].lower()
                if ext in IMAGE_EXTENSIONS:
                    local_path = os.path.join(target_dir, os.path.basename(name))
                    client.download(name, local_path, overwrite=True)

        return target_dir

    def get_local_path(self) -> str:
        """HDFS 不支持直接本地访问"""
        raise NotImplementedError("HDFS 数据源不支持直接本地路径访问，请先 download")
