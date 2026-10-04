"""图像分类模型

基于简单 CNN 的图像分类器，输出各类别 logits。
"""
import torch
import torch.nn as nn


class Classifier(nn.Module):
    """图像分类器

    使用两层卷积 + 全连接层输出分类 logits。
    输入: (N, 3, 64, 64) 图像张量
    输出: (N, n_classes) 类别 logits
    """

    def __init__(self, n_classes: int = 5):
        super().__init__()
        self.model = nn.Sequential(
            nn.Conv2d(3, 8, kernel_size=3, stride=1, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(2, 2),
            nn.Conv2d(8, 16, kernel_size=3, stride=1, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(2, 2),
            nn.Flatten(),
            nn.Linear(4096, n_classes),
        )

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        return self.model(x)
