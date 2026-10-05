# 湖智 ML 服务

图像相似度训练与检索的 Python 后端服务，基于 FastAPI + PyTorch + ChromaDB 构建。

## 目录

- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [API 接口](#api-接口)
- [核心模块](#核心模块)
- [配置说明](#配置说明)
- [开发指南](#开发指南)

---

## 项目结构

```
ml_service/
├── app.py                  # FastAPI 应用入口
├── config.py               # 全局配置（图像尺寸、超参数、路径等）
├── requirements.txt        # Python 依赖
│
├── routers/                # API 路由层
│   ├── train.py            # 训练 API
│   ├── vectors.py          # 向量库构建 API
│   ├── search.py           # 相似检索 API
│   ├── classify.py         # 图像分类 API
│   └── dataset_source.py   # 数据源探测 API
│
├── core/                   # 核心业务逻辑
│   ├── trainer_base.py     # 训练基类（通用训练循环）
│   ├── task_registry.py    # 任务注册工厂
│   ├── dataset_source.py   # 数据源抽象基类
│   ├── source_factory.py   # 数据源工厂（URI 路由）
│   └── model_cache.py      # 模型热加载缓存
│
├── tasks/                  # 各任务训练实现
│   ├── similarity_trainer.py  # 图像相似度训练器
│   └── classifier_trainer.py  # 图像分类训练器
│
├── models/                 # 模型定义
│   ├── similarity_autoencoder.py  # 相似度自编码器
│   └── classifier.py              # 分类 CNN 模型
│
├── sources/                # 数据源实现
│   ├── local_source.py     # 本地文件
│   ├── hdfs_source.py      # HDFS 集群
│   ├── s3_source.py        # S3 / MinIO
│   └── http_source.py      # HTTP URL 下载
│
├── data/                   # 数据集加载
│   └── classification_dataset.py  # 分类数据集（CSV 标签）
│
├── utils/                  # 工具类
│   └── vector_builder.py   # ChromaDB 向量构建服务
│
└── tests/                  # 单元测试（17 个测试文件）
```

---

## 快速开始

### 1. 安装依赖

```bash
cd ml_service
pip install -r requirements.txt
```

### 2. 启动服务

```bash
# 生产模式
uvicorn app:app --host 127.0.0.1 --port 8000

# 开发模式（自动重载）
uvicorn app:app --host 127.0.0.1 --port 8000 --reload
```

### 3. 验证服务

```bash
# Swagger API 文档
open http://127.0.0.1:8000/docs
```

---

## API 接口

### 训练接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/train` | 启动训练任务 |
| GET | `/api/train/{task_id}` | 查询训练进度 |
| POST | `/api/train/{task_id}/cancel` | 取消训练 |

**启动训练请求体**：
```json
{
  "task_type": "IMAGE_SIMILARITY",
  "dataset_id": 1,
  "dataset_path": "/data/datasets/1/images",
  "epochs": 30,
  "learning_rate": 0.001,
  "batch_size": 32,
  "embedding_dim": 512
}
```

### 向量库接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/vectors/build` | 构建向量索引 |
| GET | `/api/vectors/build/{task_id}` | 查询构建进度 |

### 检索接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/search` | 以图搜图（上传图片返回 Top-K） |

### 分类接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/classify` | 图片分类推理 |
| POST | `/api/classify/batch` | 批量分类 |
| GET | `/api/classify/classes` | 获取类别列表 |

### 数据源接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/dataset/probe-source` | 探测远程数据源 |

---

## 核心模块

### 任务注册工厂 (`core/task_registry.py`)

```python
TRAINER_REGISTRY = {
    "IMAGE_SIMILARITY": SimilarityTrainer,
    "IMAGE_CLASSIFICATION": ClassifierTrainer,
}
```

新增任务类型只需：
1. 在 `tasks/` 下实现 Trainer（继承 `BaseTrainer`）
2. 在 `TRAINER_REGISTRY` 注册

### 训练基类 (`core/trainer_base.py`)

所有训练器的基类，提供：
- 通用训练循环
- 进度回调（`task_store` 字典）
- 取消检查点
- 模型保存

子类只需实现：
- `build_model()` → 返回 `nn.Module`
- `build_loss_fn()` → 返回损失函数

### 数据源工厂 (`core/source_factory.py`)

基于 URI scheme 自动路由：

| 协议 | 数据源类 |
|------|----------|
| `file://` / 空 | `LocalSource` |
| `hdfs://` | `HdfsSource` |
| `s3://` / `minio://` | `S3Source` |
| `http://` / `https://` | `HttpSource` |

---

## 配置说明

### 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `CHROMA_BACKEND_PATH` | `chroma_backend` | ChromaDB 存储路径 |
| `MODEL_CACHE_MAX_SIZE` | `5` | 模型缓存数量 |
| `DEFAULT_DATASET_PATH` | `./dataset` | 默认数据集路径 |
| `MODEL_DIR` | `./model_weights` | 模型权重存储目录 |
| `TASK_TEMP_DIR` | `./task_temp` | 训练临时目录 |

### 图像处理配置（`config.py`）

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `IMG_H` / `IMG_W` | 64×64 | 输入图像尺寸 |
| `EMBEDDING_DIM` | 512 | 嵌入向量维度 |
| `TRAIN_RATIO` | 0.7 | 训练集比例 |
| `VAL_RATIO` | 0.15 | 验证集比例 |

### 训练超参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `LEARNING_RATE` | 0.001 | 学习率 |
| `TRAIN_BATCH_SIZE` | 32 | 训练批次 |
| `EPOCHS` | 30 | 训练轮数 |

---

## 开发指南

### 添加新任务类型

1. **实现 Trainer** (`tasks/xxx_trainer.py`)：
   ```python
   from core.trainer_base import BaseTrainer
   
   class MyTrainer(BaseTrainer):
       def build_model(self):
           return MyModel()
       
       def build_loss_fn():
           return nn.CrossEntropyLoss()
   ```

2. **注册到工厂** (`core/task_registry.py`)：
   ```python
   TRAINER_REGISTRY["MY_TASK"] = MyTrainer
   ```

3. **添加路由** (`routers/xxx.py`)（如需要新接口）

### 运行测试

```bash
cd ml_service
python -m pytest tests/ -v
```

### 训练流程

```
用户点击"训练" → Spring Boot 调用 /api/train
    → TaskRegistry.create_trainer() 创建训练器
    → trainer.train() 异步执行训练循环
    → 进度写入 task_store[task_id]
    → Spring Boot 轮询 /api/train/{task_id}/progress
    → 训练完成，保存模型到 model_dir
```

### 推理流程

```
用户上传图片 → Spring Boot 转发到 /api/search
    → ModelCache.load(encoder_path) 加载模型
    → encoder.encode(image) 提取 embedding
    → ChromaDB.query() 检索相似图片
    → 返回 Top-K 结果
```

---

## 依赖说明

| 依赖 | 用途 |
|------|------|
| `torch` + `torchvision` | 深度学习框架 |
| `fastapi` + `uvicorn` | HTTP 服务框架 |
| `chromadb` | 向量数据库 |
| `Pillow` | 图像处理 |
| `hdfs` | HDFS 客户端 |
| `boto3` | S3/MinIO 客户端 |
| `requests` | HTTP 下载 |
| `pytest` + `httpx` | 测试框架 |
