"""LocalSource 测试"""
import os
import tempfile
import pytest
from PIL import Image

from sources.local_source import LocalSource


class TestLocalSource:
    """测试本地数据源"""

    def _create_image_dir(self, num_images=5):
        """创建临时图片目录"""
        tmpdir = tempfile.mkdtemp()
        for i in range(num_images):
            img = Image.new("RGB", (64, 64), color=(i * 50, i * 50, i * 50))
            img.save(os.path.join(tmpdir, f"image_{i:03d}.png"))
        return tmpdir

    def test_probe_returns_image_count(self):
        """测试 probe() 返回图片文件数量"""
        tmpdir = self._create_image_dir(5)
        source = LocalSource(uri=f"file://{tmpdir}")
        count = source.probe()
        assert count == 5

    def test_probe_with_no_images(self):
        """测试空目录返回 0"""
        tmpdir = tempfile.mkdtemp()
        source = LocalSource(uri=f"file://{tmpdir}")
        count = source.probe()
        assert count == 0

    def test_probe_with_mixed_files(self):
        """测试混合文件只统计图片"""
        tmpdir = tempfile.mkdtemp()
        # 创建 3 张图片
        for i in range(3):
            img = Image.new("RGB", (64, 64))
            img.save(os.path.join(tmpdir, f"img_{i}.jpg"))
        # 创建 2 个非图片文件
        for name in ["readme.txt", "data.csv"]:
            with open(os.path.join(tmpdir, name), "w") as f:
                f.write("content")

        source = LocalSource(uri=f"file://{tmpdir}")
        count = source.probe()
        assert count == 3

    def test_get_local_path(self):
        """测试 get_local_path 返回路径"""
        tmpdir = self._create_image_dir(1)
        source = LocalSource(uri=f"file://{tmpdir}")
        path = source.get_local_path()
        assert path == tmpdir

    def test_download_copies_to_target(self):
        """测试 download 复制到目标目录"""
        tmpdir = self._create_image_dir(3)
        target = tempfile.mkdtemp()

        source = LocalSource(uri=f"file://{tmpdir}")
        result = source.download(target)

        assert os.path.isdir(result)
        # 验证文件被复制
        result_files = [f for f in os.listdir(result) if f.endswith(('.png', '.jpg', '.jpeg'))]
        assert len(result_files) == 3

    def test_nonexistent_dir_probe_returns_zero(self):
        """测试不存在的目录 probe 返回 0"""
        source = LocalSource(uri="file:///nonexistent/path/that/does/not/exist")
        count = source.probe()
        assert count == 0
