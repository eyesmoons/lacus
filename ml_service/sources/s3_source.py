"""S3/MinIO 数据源

使用 boto3 库访问 S3 兼容存储。
"""
import os
from urllib.parse import urlparse

from core.dataset_source import DatasetSource

IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".bmp", ".gif", ".tiff", ".webp"}


class S3Source(DatasetSource):
    """S3/MinIO 数据源"""

    def __init__(self, uri, credentials=None):
        super().__init__(uri, credentials)
        parsed = urlparse(uri)
        self.bucket = parsed.hostname
        # 去除开头的 /
        self.prefix = parsed.path.lstrip("/")

    def _get_client(self):
        """获取 S3 客户端"""
        import boto3
        kwargs = {}
        if self.credentials.username:
            kwargs["aws_access_key_id"] = self.credentials.username
        if self.credentials.password:
            kwargs["aws_secret_access_key"] = self.credentials.password
        if self.credentials.extra.get("endpoint_url"):
            kwargs["endpoint_url"] = self.credentials.extra["endpoint_url"]
        if self.credentials.extra.get("region_name"):
            kwargs["region_name"] = self.credentials.extra["region_name"]
        return boto3.client("s3", **kwargs)

    def probe(self) -> int:
        """探测 S3 桶中图片文件数量"""
        client = self._get_client()
        count = 0
        continuation_token = None

        while True:
            kwargs = {"Bucket": self.bucket, "Prefix": self.prefix}
            if continuation_token:
                kwargs["ContinuationToken"] = continuation_token

            response = client.list_objects_v2(**kwargs)
            contents = response.get("Contents", [])

            for obj in contents:
                key = obj["Key"]
                ext = os.path.splitext(key)[1].lower()
                if ext in IMAGE_EXTENSIONS:
                    count += 1

            if not response.get("IsTruncated"):
                break
            continuation_token = response.get("NextContinuationToken")

        return count

    def download(self, target_dir: str) -> str:
        """下载 S3 对象到本地目录"""
        os.makedirs(target_dir, exist_ok=True)
        client = self._get_client()
        continuation_token = None

        while True:
            kwargs = {"Bucket": self.bucket, "Prefix": self.prefix}
            if continuation_token:
                kwargs["ContinuationToken"] = continuation_token

            response = client.list_objects_v2(**kwargs)
            contents = response.get("Contents", [])

            for obj in contents:
                key = obj["Key"]
                ext = os.path.splitext(key)[1].lower()
                if ext in IMAGE_EXTENSIONS:
                    local_path = os.path.join(target_dir, os.path.basename(key))
                    client.download_file(self.bucket, key, local_path)

            if not response.get("IsTruncated"):
                break
            continuation_token = response.get("NextContinuationToken")

        return target_dir

    def get_local_path(self) -> str:
        """S3 不支持直接本地访问"""
        raise NotImplementedError("S3 数据源不支持直接本地路径访问，请先 download")
