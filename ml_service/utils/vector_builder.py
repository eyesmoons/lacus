"""向量构建服务

批量提取图像 embedding 并写入 ChromaDB。
"""
import os
from math import ceil
from typing import Dict
from urllib.parse import urlparse

import chromadb
import numpy as np
import torch
from torch.utils.data import DataLoader
from tqdm import tqdm
import torchvision.transforms as T

from core.trainer_base import TrainingProgress
from core.model_cache import ModelCache
from config import default_config


class ImageDataset:
    """图像数据集"""

    def __init__(self, image_dir: str, transform=None):
        from PIL import Image
        self.image_dir = image_dir
        self.transform = transform or self._default_transform()
        self.image_names = sorted([
            f for f in os.listdir(image_dir)
            if f.lower().endswith(('.png', '.jpg', '.jpeg', '.bmp', '.gif'))
        ])

    @staticmethod
    def _default_transform():
        return T.Compose([
            T.Resize((default_config.img_h, default_config.img_w)),
            T.ToTensor(),
        ])

    def __len__(self):
        return len(self.image_names)

    def __getitem__(self, idx):
        from PIL import Image
        image_path = os.path.join(self.image_dir, self.image_names[idx])
        image = Image.open(image_path).convert('RGB')
        return self.transform(image), self.image_names[idx]


def do_build_vectors(
    dataset_uri: str,
    collection_name: str,
    task_id: str,
    task_store: Dict[str, TrainingProgress],
    batch_size: int = 32,
) -> str:
    """执行向量构建

    Args:
        dataset_uri: 数据集 URI
        collection_name: ChromaDB 集合名称
        task_id: 任务 ID
        task_store: 任务状态存储
        batch_size: 批处理大小

    Returns:
        集合名称
    """
    try:
        # 1. 获取本地路径
        parsed = urlparse(dataset_uri)
        local_path = parsed.path

        if not os.path.isdir(local_path):
            task_store[task_id] = TrainingProgress(
                status="failed",
                message=f"数据集路径不存在: {local_path}",
            )
            return collection_name

        # 2. 加载数据集
        dataset = ImageDataset(local_path)
        if len(dataset) == 0:
            task_store[task_id] = TrainingProgress(
                status="completed",
                message="数据集中无图片",
            )
            return collection_name

        dataloader = DataLoader(dataset, batch_size=batch_size, shuffle=False)

        # 3. 获取模型
        cache = ModelCache()
        model = cache.get_model("similarity_autoencoder")
        model.eval()

        # 4. 获取 ChromaDB 集合
        client = chromadb.PersistentClient(path=default_config.chroma_backend_path)
        collection = client.get_or_create_collection(name=collection_name)

        # 5. 批量提取 embedding 并写入
        total = len(dataset)
        processed = 0

        with torch.no_grad():
            for batch_images, batch_names in tqdm(dataloader, desc="构建向量"):
                embeddings = model.encode(batch_images).numpy()

                collection.upsert(
                    ids=list(batch_names),
                    embeddings=embeddings.tolist(),
                )

                processed += len(batch_names)
                task_store[task_id] = TrainingProgress(
                    epoch=processed,
                    total_epochs=total,
                    status="building",
                    message=f"已处理 {processed}/{total}",
                )

        task_store[task_id] = TrainingProgress(
            epoch=total,
            total_epochs=total,
            status="completed",
            message="向量构建完成",
        )

    except Exception as e:
        task_store[task_id] = TrainingProgress(
            status="failed",
            message=str(e),
        )

    return collection_name
