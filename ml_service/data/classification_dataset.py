"""分类数据集加载（CSV 标签）。

支持从 CSV 文件（格式：image_name,label）加载图片分类标签，
返回 (image_tensor, label_tensor) 元组，用于分类模型训练。
"""
import os

import pandas as pd
import torch
import torchvision.transforms as T
from PIL import Image
from torch.utils.data import Dataset


IMG_EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".gif", ".tiff", ".webp"}


def _collect_image_names(image_dir: str) -> list[str]:
    """按字母数字顺序收集目录下的图片文件名。"""
    names = [
        f for f in os.listdir(image_dir)
        if os.path.splitext(f)[1].lower() in IMG_EXTENSIONS
    ]
    return sorted(names, key=lambda s: [int(c) if c.isdigit() else c for c in s])


class ClassificationDataset(Dataset):
    """分类数据集，从 CSV 文件读取标签。

    CSV 格式：第一行为表头 ``image_name,label``，后续每行对应一张图片。
    图片按文件名字典序排列，与 CSV 行序一一对应。
    """

    def __init__(
        self,
        image_dir: str,
        label_csv: str,
        transform: T.Compose | None = None,
    ) -> None:
        self.image_dir = image_dir
        self.transform = transform if transform is not None else T.ToTensor()

        self.image_names = _collect_image_names(image_dir)

        if not os.path.exists(label_csv):
            raise FileNotFoundError(f"标签文件不存在：{label_csv}")

        label_data = pd.read_csv(label_csv)
        if "image_name" not in label_data.columns or "label" not in label_data.columns:
            raise ValueError("CSV 必须包含 image_name 和 label 列")

        csv_names = set(label_data["image_name"])
        dir_names = set(self.image_names)
        missing_in_csv = dir_names - csv_names
        if missing_in_csv:
            raise ValueError(f"以下图片在 CSV 中缺少标签：{sorted(missing_in_csv)}")
        extra_in_csv = csv_names - dir_names
        if extra_in_csv:
            raise ValueError(f"CSV 中存在目录中不存在的图片：{sorted(extra_in_csv)}")

        name_to_label = dict(zip(label_data["image_name"], label_data["label"]))
        self.labels = [name_to_label[name] for name in self.image_names]

    def __len__(self) -> int:
        return len(self.image_names)

    def __getitem__(self, idx: int) -> tuple[torch.Tensor, torch.Tensor]:
        image_path = os.path.join(self.image_dir, self.image_names[idx])
        image = Image.open(image_path).convert("RGB")
        image_tensor = self.transform(image)
        label_tensor = torch.tensor(self.labels[idx], dtype=torch.long)
        return image_tensor, label_tensor
