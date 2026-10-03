"""相似度自编码器模型

将 ConvEncoder 和 ConvDecoder 合并为 SimilarityAutoEncoder，
用于图像相似度任务的编码和解码。
"""
import torch
import torch.nn as nn


class _ConvEncoder(nn.Module):
    """6 层卷积编码器，将 64x64 图像编码为 512 维向量"""

    def __init__(self):
        super().__init__()
        self.conv1 = nn.Conv2d(3, 16, kernel_size=3, padding=1)
        self.conv2 = nn.Conv2d(16, 32, kernel_size=3, padding=1)
        self.conv3 = nn.Conv2d(32, 64, kernel_size=3, padding=1)
        self.conv4 = nn.Conv2d(64, 128, kernel_size=3, padding=1)
        self.conv5 = nn.Conv2d(128, 256, kernel_size=3, padding=1)
        self.conv6 = nn.Conv2d(256, 512, kernel_size=3, padding=1)
        self.pool = nn.MaxPool2d(2, 2)

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        x = torch.relu(self.conv1(x))
        x = self.pool(x)
        x = torch.relu(self.conv2(x))
        x = self.pool(x)
        x = torch.relu(self.conv3(x))
        x = self.pool(x)
        x = torch.relu(self.conv4(x))
        x = self.pool(x)
        x = torch.relu(self.conv5(x))
        x = self.pool(x)
        x = torch.relu(self.conv6(x))
        x = self.pool(x)
        # 压缩为 (N, 512)
        x = x.squeeze(-1).squeeze(-1)
        return x


class _ConvDecoder(nn.Module):
    """6 层转置卷积解码器，将 512 维向量解码为 64x64 图像"""

    def __init__(self):
        super().__init__()
        self.conv_t1 = nn.ConvTranspose2d(512, 256, kernel_size=2, stride=2)
        self.conv_t2 = nn.ConvTranspose2d(256, 128, kernel_size=2, stride=2)
        self.conv_t3 = nn.ConvTranspose2d(128, 64, kernel_size=2, stride=2)
        self.conv_t4 = nn.ConvTranspose2d(64, 32, kernel_size=2, stride=2)
        self.conv_t5 = nn.ConvTranspose2d(32, 16, kernel_size=2, stride=2)
        self.conv_t6 = nn.ConvTranspose2d(16, 3, kernel_size=2, stride=2)

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        # 恢复 4 维张量 (N, 512, 1, 1)
        x = x.unsqueeze(-1).unsqueeze(-1)
        x = torch.relu(self.conv_t1(x))
        x = torch.relu(self.conv_t2(x))
        x = torch.relu(self.conv_t3(x))
        x = torch.relu(self.conv_t4(x))
        x = torch.relu(self.conv_t5(x))
        x = torch.sigmoid(self.conv_t6(x))
        return x


class SimilarityAutoEncoder(nn.Module):
    """相似度自编码器

    组合编码器和解码器，提供 encode/decode 便捷接口。
    """

    def __init__(self):
        super().__init__()
        self.encoder = _ConvEncoder()
        self.decoder = _ConvDecoder()

    def encode(self, x: torch.Tensor) -> torch.Tensor:
        """编码图像为 512 维向量"""
        return self.encoder(x)

    def decode(self, z: torch.Tensor) -> torch.Tensor:
        """解码 512 维向量为图像"""
        return self.decoder(z)

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        """前向传播：编码后解码"""
        z = self.encode(x)
        return self.decode(z)
