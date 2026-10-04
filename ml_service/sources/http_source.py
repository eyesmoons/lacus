"""HTTP 数据源

使用 requests 库下载远程文件，支持 zip 解压。
"""
import os
import tempfile
import zipfile
from urllib.parse import urlparse

from core.dataset_source import DatasetSource

IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".bmp", ".gif", ".tiff", ".webp"}


class HTTPSource(DatasetSource):
    """HTTP/HTTPS 数据源"""

    def __init__(self, uri, credentials=None):
        super().__init__(uri, credentials)
        self.url = uri

    def probe(self) -> int:
        """HTTP 源无法直接探测，返回 -1 表示未知"""
        return -1

    def download(self, target_dir: str) -> str:
        """下载文件到本地目录"""
        import requests

        os.makedirs(target_dir, exist_ok=True)

        kwargs = {"stream": True, "timeout": 300}
        if self.credentials and self.credentials.username:
            kwargs["auth"] = (self.credentials.username, self.credentials.password)

        response = requests.get(self.url, **kwargs)
        response.raise_for_status()

        # 判断是否为 zip 文件
        content_type = response.headers.get("Content-Type", "")
        is_zip = (
            "zip" in content_type
            or self.url.endswith(".zip")
        )

        if is_zip:
            # 流式写入临时 zip 文件，避免将整个文件加载到内存（防止 OOM / zip bomb）
            tmp_fd, tmp_path = tempfile.mkstemp(suffix=".zip", dir=target_dir)
            try:
                with os.fdopen(tmp_fd, "wb") as tmp_file:
                    for chunk in response.iter_content(chunk_size=8192):
                        if chunk:
                            tmp_file.write(chunk)
                with zipfile.ZipFile(tmp_path) as zf:
                    for member in zf.namelist():
                        ext = os.path.splitext(member)[1].lower()
                        if ext in IMAGE_EXTENSIONS:
                            zf.extract(member, target_dir)
            finally:
                if os.path.exists(tmp_path):
                    os.remove(tmp_path)
        else:
            # 直接保存文件
            filename = os.path.basename(urlparse(self.url).path) or "download.bin"
            filepath = os.path.join(target_dir, filename)
            with open(filepath, "wb") as f:
                for chunk in response.iter_content(chunk_size=8192):
                    f.write(chunk)

        return target_dir

    def get_local_path(self) -> str:
        """HTTP 源不支持直接本地访问"""
        raise NotImplementedError("HTTP 数据源不支持直接本地路径访问，请先 download")
