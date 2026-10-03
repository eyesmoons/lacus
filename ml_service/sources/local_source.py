"""本地文件系统数据源"""
import glob
import os
import shutil
from urllib.parse import urlparse

from core.dataset_source import DatasetSource

# 支持的图片扩展名
IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".bmp", ".gif", ".tiff", ".webp"}


class LocalSource(DatasetSource):
    """本地文件数据源"""

    def _extract_path(self) -> str:
        """从 file:// URI 提取本地路径"""
        parsed = urlparse(self.uri)
        return parsed.path

    def probe(self) -> int:
        """探测本地目录中图片文件数量"""
        path = self._extract_path()
        if not os.path.isdir(path):
            return 0

        count = 0
        for entry in os.listdir(path):
            ext = os.path.splitext(entry)[1].lower()
            if ext in IMAGE_EXTENSIONS:
                count += 1
        return count

    def download(self, target_dir: str) -> str:
        """复制图片到目标目录"""
        path = self._extract_path()
        os.makedirs(target_dir, exist_ok=True)

        for entry in os.listdir(path):
            ext = os.path.splitext(entry)[1].lower()
            if ext in IMAGE_EXTENSIONS:
                src = os.path.join(path, entry)
                dst = os.path.join(target_dir, entry)
                shutil.copy2(src, dst)

        return target_dir

    def get_local_path(self) -> str:
        """直接返回本地路径"""
        return self._extract_path()
