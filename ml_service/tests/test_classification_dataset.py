"""ClassificationDataset 分类数据集加载测试"""
import os
import tempfile

import pytest
import torch
import torchvision.transforms as T
from PIL import Image

from data.classification_dataset import ClassificationDataset


def _write_csv(path: str, rows: list[tuple[str, int]]) -> None:
    """写入 CSV 标签文件（image_name,label 格式）"""
    with open(path, "w", encoding="utf-8") as f:
        f.write("image_name,label\n")
        for name, label in rows:
            f.write(f"{name},{label}\n")


def _make_image(path: str, size: tuple[int, int] = (64, 64)) -> None:
    """生成一张纯色测试图片"""
    Image.new("RGB", size, color=(123, 45, 67)).save(path)


class TestClassificationDataset:
    """测试分类数据集（CSV 标签）"""

    def _setup_dataset(self, tmpdir, rows):
        """准备图片目录、CSV 标签文件与数据集实例"""
        for name, _ in rows:
            _make_image(os.path.join(tmpdir, name))
        csv_path = os.path.join(tmpdir, "labels.csv")
        _write_csv(csv_path, rows)
        transform = T.Compose([T.Resize((32, 32)), T.ToTensor()])
        dataset = ClassificationDataset(
            image_dir=tmpdir, label_csv=csv_path, transform=transform
        )
        return dataset

    def test_length_matches_csv_rows(self):
        """CSV 行数决定数据集长度"""
        with tempfile.TemporaryDirectory() as tmpdir:
            rows = [(f"img_{i:02d}.png", i % 3) for i in range(5)]
            dataset = self._setup_dataset(tmpdir, rows)
            assert len(dataset) == 5

    def test_getitem_returns_tensor_pair(self):
        """__getitem__ 返回 (image_tensor, label_tensor) 元组"""
        with tempfile.TemporaryDirectory() as tmpdir:
            rows = [("a.png", 0), ("b.png", 1)]
            dataset = self._setup_dataset(tmpdir, rows)
            image, label = dataset[0]

            assert isinstance(image, torch.Tensor), "图片应为 Tensor"
            assert image.shape == (3, 32, 32), f"图片 shape 应为 (3,32,32)，实际 {image.shape}"
            assert isinstance(label, torch.Tensor), "标签应为 Tensor"
            assert label.dtype == torch.long, f"标签 dtype 应为 long，实际 {label.dtype}"

    def test_labels_match_csv_values(self):
        """标签值与 CSV 中写入的值一致"""
        with tempfile.TemporaryDirectory() as tmpdir:
            rows = [("cat.png", 2), ("dog.png", 0), ("pig.png", 1)]
            dataset = self._setup_dataset(tmpdir, rows)
            labels = [dataset[i][1].item() for i in range(len(dataset))]
            assert labels == [2, 0, 1]

    def test_image_sorted_by_name(self):
        """图片按文件名字典序排列（与 CSV 行序对应）"""
        with tempfile.TemporaryDirectory() as tmpdir:
            rows = [("b.png", 0), ("a.png", 1), ("c.png", 2)]
            dataset = self._setup_dataset(tmpdir, rows)
            # 期望按 a,b,c 排序，对应标签 1,0,2
            labels = [dataset[i][1].item() for i in range(len(dataset))]
            assert labels == [1, 0, 2]

    def test_rgb_conversion(self):
        """RGBA 模式图片应被转换为 RGB 三通道"""
        with tempfile.TemporaryDirectory() as tmpdir:
            name = "rgba.png"
            Image.new("RGBA", (8, 8), color=(10, 20, 30, 255)).save(os.path.join(tmpdir, name))
            csv_path = os.path.join(tmpdir, "labels.csv")
            _write_csv(csv_path, [(name, 0)])
            transform = T.Compose([T.Resize((16, 16)), T.ToTensor()])
            dataset = ClassificationDataset(
                image_dir=tmpdir, label_csv=csv_path, transform=transform
            )
            image, _ = dataset[0]
            assert image.shape[0] == 3, "应为 RGB 三通道"

    def test_missing_csv_raises(self):
        """CSV 文件不存在时应抛出 FileNotFoundError"""
        with tempfile.TemporaryDirectory() as tmpdir:
            _make_image(os.path.join(tmpdir, "x.png"))
            with pytest.raises(FileNotFoundError):
                ClassificationDataset(
                    image_dir=tmpdir,
                    label_csv=os.path.join(tmpdir, "absent.csv"),
                    transform=T.ToTensor(),
                )

    def test_image_count_mismatch_raises(self):
        """图片数量与 CSV 行数不一致时应抛出 ValueError"""
        with tempfile.TemporaryDirectory() as tmpdir:
            _make_image(os.path.join(tmpdir, "only.png"))
            csv_path = os.path.join(tmpdir, "labels.csv")
            _write_csv(csv_path, [("only.png", 0), ("ghost.png", 1)])
            transform = T.Compose([T.Resize((16, 16)), T.ToTensor()])
            with pytest.raises(ValueError):
                ClassificationDataset(
                    image_dir=tmpdir, label_csv=csv_path, transform=transform
                )
