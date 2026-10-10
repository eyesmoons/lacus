"""ML 服务配置模块"""
import os
from dataclasses import dataclass, field


# 图像处理配置
IMG_H = 64
IMG_W = 64

# 随机性相关配置
# 固定种子（保证训练可复现）。默认 27 是在"小猫数据集"上实测不塌缩的值；
# 换数据集需重新挑选（自编码器对相似图片容易塌缩，种子决定成败）。
SEED = int(os.environ.get("RANDOM_SEED", "27"))
TRAIN_RATIO = 0.7
VAL_RATIO = 0.15
TEST_RATIO = 0.15

# 超参数
LEARNING_RATE = 1e-3
TRAIN_BATCH_SIZE = 32
VAL_BATCH_SIZE = 32
TEST_BATCH_SIZE = 32
EPOCHS = 30

# 对比损失（在重建损失基础上，拉开不同图片的 embedding，缓解塌缩）
CONTRASTIVE_WEIGHT = float(os.environ.get("CONTRASTIVE_WEIGHT", "5.0"))
CONTRASTIVE_TEMPERATURE = float(os.environ.get("CONTRASTIVE_TEMPERATURE", "0.1"))

# 模型配置
EMBEDDING_DIM = 512

# ChromaDB 相关配置
CHROMA_BACKEND_PATH = os.environ.get("CHROMA_BACKEND_PATH", "chroma_backend")
IMAGE_COLLECTION_NAME = "image_collection"
CHROMA_INSERT_BATCH_SIZE = 5000

# 模型缓存配置
MODEL_CACHE_MAX_SIZE = int(os.environ.get("MODEL_CACHE_MAX_SIZE", "5"))


@dataclass
class AppConfig:
    """应用全局配置"""
    img_h: int = IMG_H
    img_w: int = IMG_W
    seed: int = SEED
    train_ratio: float = TRAIN_RATIO
    val_ratio: float = VAL_RATIO
    test_ratio: float = TEST_RATIO
    learning_rate: float = LEARNING_RATE
    train_batch_size: int = TRAIN_BATCH_SIZE
    val_batch_size: int = VAL_BATCH_SIZE
    test_batch_size: int = TEST_BATCH_SIZE
    epochs: int = EPOCHS
    contrastive_weight: float = CONTRASTIVE_WEIGHT
    contrastive_temperature: float = CONTRASTIVE_TEMPERATURE
    embedding_dim: int = EMBEDDING_DIM
    chroma_backend_path: str = CHROMA_BACKEND_PATH
    image_collection_name: str = IMAGE_COLLECTION_NAME
    chroma_insert_batch_size: int = CHROMA_INSERT_BATCH_SIZE
    model_cache_max_size: int = MODEL_CACHE_MAX_SIZE
    # 默认本地数据集路径
    default_dataset_path: str = os.environ.get("DEFAULT_DATASET_PATH", "./dataset")
    # 模型存储目录
    model_dir: str = os.environ.get("MODEL_DIR", "./model_weights")
    # 训练任务临时目录
    task_temp_dir: str = os.environ.get("TASK_TEMP_DIR", "./task_temp")


# 全局默认配置实例
default_config = AppConfig()
