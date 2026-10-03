---
comet_change: lake-intelligence
role: technical-design
canonical_spec: openspec
archived-with: 2026-10-04-lake-intelligence
status: final
---

# 湖智模块（Lake Intelligence）技术设计文档

> 版本：v1.0
> 日期：2026-10-03
> 状态：设计完成
> Canonical Spec: `docs/openspec/changes/lake-intelligence/specs/`

---

## 1. 概述

### 1.1 设计目标

在 lacus 多模块 Maven 项目中新增"湖智"图像相似度检索模块，遵循现有的 admin/domain/dao 分层架构，通过 Feign 调用独立的 Python ML 服务完成模型训练、向量构建和相似检索。

### 1.2 设计原则

- **遵循现有模式**：Controller → Business → Mapper 分层，复用 `ResponseDTO<?>`、`@PreAuthorize`、Feign 等已有基础设施
- **最小侵入**：新增代码不修改现有代码，使用 `lake_` 前缀命名新表和包
- **可演进**：当前实现简单可靠，预留未来扩展点（Redis 进度存储、线程池并发、MinIO 存储）

---

## 2. 整体架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                         用户浏览器                                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │ 上传图片库│  │ 训练管理  │  │ 相似检索  │  │ 模型管理  │            │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘            │
└───────┼──────────────┼──────────────┼──────────────┼────────────────┘
        │              │              │              │
        ▼              ▼              ▼              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   lacus-admin (Controller 层)                       │
│  controller/lakeintelligence/                                       │
│  ├── DatasetController    ├── TrainController                       │
│  ├── VectorController     ├── SearchController                      │
│  ├── ModelController      └── PageController                        │
└──────────────────────────┬──────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   lacus-domain (业务逻辑层)                          │
│  domain/lakeintelligence/                                           │
│  ├── DatasetBusiness      ├── TrainBusiness                         │
│  ├── VectorIndexBusiness  ├── SearchBusiness                        │
│  ├── ModelBusiness        └── source/SourceDownloadService          │
│  ├── feign/MlServiceFeign                                          │
│  ├── feign/MlServiceFeignConfiguration                             │
│  ├── feign/MlServiceFeignFallbackFactory                           │
│  ├── task/TaskHandler.java                                         │
│  ├── task/TaskHandlerFactory.java                                  │
│  ├── task/SimilarityTaskHandler.java                               │
│  ├── command/ (CreateDatasetRequest, TrainRequest, etc.)           │
│  ├── query/  (DatasetPageQuery, TaskPageQuery, etc.)               │
│  └── dto/    (DatasetDTO, TaskDTO, ModelInfoDTO, etc.)             │
└──────────────────────────┬──────────────────────────────────────────┘
                           │
              ┌────────────┴────────────┐
              │                         │
              ▼                         ▼
┌──────────────────────┐   ┌──────────────────────────────────────────┐
│  lacus-dao (数据层)   │   │  Python ML 服务 (FastAPI :8000)          │
│  ├── lake_datasets    │   │  ├── /api/train        → SimilarityTrainer│
│  ├── lake_tasks       │   │  ├── /api/vectors/build → VectorBuilder  │
│  ├── lake_model_info  │   │  ├── /api/search        → ChromaDB      │
│  └── lake_vector_idx  │   │  └── /api/dataset/probe → SourceFactory  │
│  Entity + Mapper      │   │                                          │
└──────────────────────┘   │  core/: BaseTrainer, TaskRegistry,       │
                           │    SourceFactory, ModelCache             │
                           │  tasks/: SimilarityTrainer               │
                           │  sources/: Local/Hdfs/S3/Http Source    │
                           │  models/: SimilarityAutoEncoder         │
                           └──────────────────────────────────────────┘
```

---

## 3. Python ML 服务设计

### 3.1 目录结构

```
ml_service/
├── app.py                    # FastAPI 入口
├── config.py                 # 配置（存储路径、ChromaDB 路径等）
├── requirements.txt          # Python 依赖
│
├── routers/
│   ├── train.py              # 训练 API
│   ├── vectors.py            # 向量构建 API
│   ├── search.py             # 相似检索 API
│   └── dataset_source.py     # 数据源探测 API
│
├── core/
│   ├── trainer_base.py       # 训练基类（通用训练循环）
│   ├── task_registry.py      # 任务注册工厂
│   ├── dataset_source.py     # 数据源抽象基类
│   ├── source_factory.py     # 数据源工厂（URI 路由）
│   └── model_cache.py        # 模型热加载缓存
│
├── tasks/
│   └── similarity_trainer.py # 图像相似度训练器
│
├── sources/
│   ├── local_source.py       # 本地数据源
│   ├── hdfs_source.py        # HDFS 数据源
│   ├── s3_source.py          # S3/MinIO 数据源
│   └── http_source.py        # HTTP 数据源
│
├── models/
│   └── similarity_autoencoder.py  # 合并的自编码器
│
└── utils/
    ├── chroma_client.py      # ChromaDB 客户端工具
    └── logger.py             # 日志配置
```

### 3.2 核心组件设计

#### SimilarityAutoEncoder（合并自编码器）

```python
class SimilarityAutoEncoder(nn.Module):
    """合并后的自编码器：训练用完整，推理只用前半"""
    def __init__(self):
        self.encoder = nn.Sequential(
            nn.Conv2d(3, 16, 3, padding=1), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(16, 32, 3, padding=1), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(32, 64, 3, padding=1), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(64, 128, 3, padding=1), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(128, 256, 3, padding=1), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(256, 512, 3, padding=1), nn.ReLU(), nn.MaxPool2d(2),
        )
        self.decoder = nn.Sequential(
            nn.ConvTranspose2d(512, 256, 2, stride=2), nn.ReLU(),
            nn.ConvTranspose2d(256, 128, 2, stride=2), nn.ReLU(),
            nn.ConvTranspose2d(128, 64, 2, stride=2), nn.ReLU(),
            nn.ConvTranspose2d(64, 32, 2, stride=2), nn.ReLU(),
            nn.ConvTranspose2d(32, 16, 2, stride=2), nn.ReLU(),
            nn.ConvTranspose2d(16, 3, 2, stride=2), nn.Sigmoid(),
        )

    def encode(self, x):
        return self.encoder(x).squeeze(-1).squeeze(-1)  # → (N, 512)

    def forward(self, x):
        return self.decoder(self.encode(x).unsqueeze(-1).unsqueeze(-1))
```

#### BaseTrainer（训练基类）

```python
class BaseTrainer(ABC):
    def __init__(self, task_id: str, config: dict):
        self.task_id = task_id
        self.config = config
        self._cancelled = False

    @abstractmethod
    def build_model(self) -> nn.Module: ...
    @abstractmethod
    def build_loss_fn(self) -> nn.Module: ...

    async def run(self, dataset_path: str):
        task_store[self.task_id] = {"status": "training", "progress": 0}
        model = self.build_model()
        loss_fn = self.build_loss_fn()
        optimizer = AdamW(model.parameters(), lr=self.config["lr"])
        for epoch in range(self.config["epochs"]):
            if self._cancelled:
                break
            train_loss = self._train_one_epoch(model, dataset_path, loss_fn, optimizer)
            val_loss = self._validate(model, dataset_path, loss_fn)
            task_store[self.task_id].update({
                "current_epoch": epoch + 1,
                "total_epochs": self.config["epochs"],
                "progress": (epoch + 1) / self.config["epochs"],
                "current_loss": val_loss,
            })
        self._save_model(model)
        task_store[self.task_id]["status"] = "completed"
```

#### SourceFactory（数据源工厂）

```python
class SourceFactory:
    @staticmethod
    def create(uri: str, credentials: SourceCredentials = None) -> DatasetSource:
        scheme = urlparse(uri).scheme.lower()
        if scheme in ('file', ''):    return LocalSource(uri)
        elif scheme == 'hdfs':       return HdfsSource(uri, credentials)
        elif scheme in ('s3', 'minio'): return S3Source(uri, credentials)
        elif scheme in ('http', 'https'): return HttpSource(uri, credentials)
        raise ValueError(f"不支持的数据源: {scheme}")
```

#### task_store（进度存储）

```python
# 内存存储，服务重启丢失（开发阶段可接受）
task_store: Dict[str, Dict] = {}

# 结构示例
{
    "task-uuid-123": {
        "status": "training",      # training/completed/failed/cancelled
        "progress": 0.5,           # 0.0 ~ 1.0
        "current_epoch": 15,
        "total_epochs": 30,
        "current_loss": 0.0087,
        "loss_history": [0.05, 0.03, ...],
        "model_path": "/data/models/task-123/autoencoder.pt"
    }
}
```

### 3.3 API 设计

| 方法 | 路径 | 请求 | 响应 |
|------|------|------|------|
| POST | `/api/train` | `{task_id, dataset_path, epochs, lr, batch_size}` | `{status: "started"}` |
| GET | `/api/train/{task_id}` | - | `{status, progress, current_epoch, total_epochs, current_loss}` |
| POST | `/api/train/{task_id}/cancel` | - | `{status: "cancelled"}` |
| POST | `/api/vectors/build` | `{dataset_id, encoder_path, collection_name, chroma_path}` | `{status: "building"}` |
| GET | `/api/vectors/build/{task_id}` | - | `{status, indexed_count, total_count}` |
| POST | `/api/search` | `{image, collection_name, encoder_path, chroma_path, top_k}` | `{results: [{id, similarity}]}` |
| POST | `/api/dataset/probe-source` | `{storage_source, uri, credentials}` | `{accessible, file_count, total_size_human}` |

---

## 4. Spring Boot 后端设计

### 4.1 包结构

```
lacus-admin/src/main/java/com/lacus/admin/
└── controller/lakeintelligence/
    ├── DatasetController.java
    ├── TrainController.java
    ├── VectorController.java
    ├── SearchController.java
    ├── ModelController.java
    └── PageController.java

lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/
├── DatasetBusiness.java
├── TrainBusiness.java
├── VectorIndexBusiness.java
├── SearchBusiness.java
├── ModelBusiness.java
├── feign/
│   ├── MlServiceFeign.java
│   ├── MlServiceFeignConfiguration.java
│   └── MlServiceFeignFallbackFactory.java
├── task/
│   ├── TaskHandler.java
│   ├── TaskHandlerFactory.java
│   └── SimilarityTaskHandler.java
├── command/
│   ├── CreateDatasetRequest.java
│   ├── TrainRequest.java
│   ├── SearchRequest.java
│   └── BuildVectorRequest.java
├── query/
│   ├── DatasetPageQuery.java
│   ├── TaskPageQuery.java
│   └── ModelPageQuery.java
└── dto/
    ├── DatasetDTO.java
    ├── TaskDTO.java
    ├── ModelInfoDTO.java
    ├── VectorIndexDTO.java
    ├── ProgressResponse.java
    └── SearchResponse.java

lacus-dao/src/main/java/com/lacus/dao/
├── entity/
│   ├── LakeDatasetEntity.java
│   ├── LakeTaskEntity.java
│   ├── LakeModelInfoEntity.java
│   └── LakeVectorIndexEntity.java
└── mapper/
    ├── LakeDatasetMapper.java
    ├── LakeTaskMapper.java
    ├── LakeModelInfoMapper.java
    └── LakeVectorIndexMapper.java

lacus-common/src/main/java/com/lacus/enums/
├── StorageSource.java
├── TaskType.java
├── TaskStatus.java
└── DatasetStatus.java
```

### 4.2 Feign 客户端设计

```java
@FeignClient(name = "mlServiceFeignClient",
        url = "${ml.service.url}",
        contextId = "mlServiceFeignClient",
        fallbackFactory = MlServiceFeignFallbackFactory.class,
        configuration = MlServiceFeignConfiguration.class)
public interface MlServiceFeign {

    @PostMapping("/api/train")
    Map<String, Object> startTrain(@RequestBody Map<String, Object> request);

    @GetMapping("/api/train/{task_id}")
    Map<String, Object> getTrainProgress(@PathVariable("task_id") String taskId);

    @PostMapping("/api/train/{task_id}/cancel")
    Map<String, Object> cancelTrain(@PathVariable("task_id") String taskId);

    @PostMapping("/api/vectors/build")
    Map<String, Object> buildVectors(@RequestBody Map<String, Object> request);

    @GetMapping("/api/vectors/build/{task_id}")
    Map<String, Object> getBuildProgress(@PathVariable("task_id") String taskId);

    @PostMapping("/api/search")
    Map<String, Object> search(@RequestPart("image") MultipartFile image,
                               @RequestParam Map<String, String> params);

    @PostMapping("/api/dataset/probe-source")
    Map<String, Object> probeSource(@RequestBody Map<String, Object> request);
}
```

### 4.3 训练状态轮询机制

```java
@Scheduled(fixedDelay = 2000) // 每 2 秒执行
public void pollTrainingTasks() {
    List<LakeTaskEntity> trainingTasks = taskMapper.selectByStatus("TRAINING");
    for (LakeTaskEntity task : trainingTasks) {
        Map<String, Object> progress = mlServiceFeign.getTrainProgress(task.getMlTaskId());
        String status = (String) progress.get("status");
        if ("completed".equals(status)) {
            task.setStatus("COMPLETED");
            taskMapper.updateById(task);
            createModelInfo(task, progress);
        } else if ("failed".equals(status)) {
            task.setStatus("FAILED");
            task.setErrorMsg((String) progress.get("error"));
            taskMapper.updateById(task);
        } else {
            // 更新进度
            task.setCurrentEpoch((Integer) progress.get("current_epoch"));
            task.setCurrentLoss(new BigDecimal(progress.get("current_loss").toString()));
            taskMapper.updateById(task);
        }
    }
}
```

---

## 5. 数据库设计

### 5.1 表命名约定

使用 `lake_` 前缀避免与现有表冲突。

### 5.2 DDL

```sql
-- 数据集表
CREATE TABLE lake_datasets (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT          NOT NULL COMMENT '用户 ID',
    name            VARCHAR(100)    NOT NULL COMMENT '数据集名称',
    description     VARCHAR(500)    COMMENT '描述',
    task_type       VARCHAR(50)     NOT NULL COMMENT 'IMAGE_SIMILARITY',
    storage_source  VARCHAR(20)     NOT NULL COMMENT 'LOCAL/HDFS/S3/MINIO/HTTP',
    uri             VARCHAR(1000)   NOT NULL COMMENT '资源 URI',
    local_path      VARCHAR(500)    COMMENT '本地存储路径',
    download_status VARCHAR(20)     DEFAULT 'NONE' COMMENT 'NONE/PENDING/DOWNLOADING/COMPLETED/FAILED',
    download_error  VARCHAR(1000)   COMMENT '下载错误信息',
    total_samples   INT             COMMENT '图片数量',
    file_size_bytes BIGINT          COMMENT '文件大小(字节)',
    status          VARCHAR(20)     NOT NULL DEFAULT 'PROCESSING'
                                    COMMENT 'PROCESSING/WAITING_DOWNLOAD/DOWNLOADING/READY/ERROR',
    error_msg       VARCHAR(1000)   COMMENT '错误信息',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_source (storage_source),
    INDEX idx_task_type (task_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 训练任务表
CREATE TABLE lake_tasks (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    dataset_id      BIGINT          NOT NULL,
    model_id        BIGINT          COMMENT '关联模型 ID',
    ml_task_id      VARCHAR(64)     COMMENT 'Python 端任务 UUID',
    task_type       VARCHAR(50)     NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING'
                                    COMMENT 'PENDING/TRAINING/COMPLETED/FAILED/CANCELLED',
    current_epoch   INT             NOT NULL DEFAULT 0,
    total_epochs    INT             NOT NULL,
    learning_rate   DECIMAL(10,6)   NOT NULL,
    batch_size      INT             NOT NULL DEFAULT 32,
    hyperparams     JSON            COMMENT '任务特定超参',
    current_loss    DECIMAL(10,6),
    loss_history    JSON            COMMENT '损失历史数组',
    error_msg       VARCHAR(1000),
    started_at      DATETIME,
    completed_at    DATETIME,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (dataset_id) REFERENCES lake_datasets(id),
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 模型信息表
CREATE TABLE lake_model_info (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    task_id         BIGINT          NOT NULL,
    dataset_id      BIGINT          NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    task_type       VARCHAR(50)     NOT NULL,
    model_dir       VARCHAR(500)    NOT NULL COMMENT '模型目录',
    model_framework VARCHAR(20)     NOT NULL DEFAULT 'PYTORCH',
    model_arch      VARCHAR(100)    NOT NULL COMMENT 'similarity_autoencoder',
    model_size_mb   DECIMAL(10,2),
    inference_config JSON           COMMENT '推理配置',
    final_metrics   JSON            COMMENT '最终训练指标',
    training_time_sec INT           COMMENT '训练时长(秒)',
    is_active       TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES lake_tasks(id),
    FOREIGN KEY (dataset_id) REFERENCES lake_datasets(id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 向量索引表
CREATE TABLE lake_vector_indexes (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    model_id        BIGINT          NOT NULL,
    collection_name VARCHAR(100)    NOT NULL UNIQUE,
    index_type      VARCHAR(20)     NOT NULL DEFAULT 'CHROMA',
    storage_path    VARCHAR(500)    NOT NULL,
    indexed_count   INT             NOT NULL DEFAULT 0,
    embedding_dim   INT             NOT NULL DEFAULT 512,
    status          VARCHAR(20)     NOT NULL DEFAULT 'BUILDING'
                                    COMMENT 'BUILDING/READY/ERROR',
    error_msg       VARCHAR(1000),
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (model_id) REFERENCES lake_model_info(id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 6. 前端设计

### 6.1 页面清单

| 页面 | 路径 | 功能 |
|------|------|------|
| 图片库上传页 | `/lake-intelligence/dataset/upload` | 数据源切换、文件上传、测试连接 |
| 图片库预览页 | `/lake-intelligence/dataset/{id}/preview` | 缩略图网格、基本信息、操作入口 |
| 训练配置页 | `/lake-intelligence/training/new?datasetId={id}` | 参数表单（epochs/lr/batch_size 滑块） |
| 训练进度页 | `/lake-intelligence/training/{id}` | 损失曲线（Chart.js）、进度条、取消按钮 |
| 向量构建页 | `/lake-intelligence/vector-build?modelId={id}` | 构建按钮、进度展示 |
| 相似检索页 | `/lake-intelligence/search?collection={name}` | 图片上传、Top-K 结果网格 |
| 模型管理页 | `/lake-intelligence/models` | 模型列表、下载/删除操作 |

### 6.2 前端技术

- **模板引擎**：Thymeleaf（与现有 lacus-admin 一致）
- **UI 框架**：Bootstrap 5（与现有一致）
- **图表**：Chart.js（损失曲线）
- **文件上传**：原生 HTML5 File API + 拖拽支持
- **进度轮询**：原生 JavaScript `setInterval` 每 2 秒轮询

---

## 7. 配置设计

### 7.1 application.yml 新增

```yaml
ml:
  service:
    url: http://localhost:8000
    timeout:
      connect: 5000      # 连接超时 5s
      read: 120000       # 读取超时 120s（训练进度查询）

storage:
  root: /data/lake-intelligence
  max-dataset-size: 50000    # 最大图片数
  max-upload-size: 500MB     # 最大上传 zip
  max-image-size: 10MB       # 最大单张图片

app:
  crypto:
    key: ${CRYPTO_KEY:change-me-in-production}  # AES 加密密钥

training:
  max-concurrent: 1          # 最大并发训练数
  timeout-hours: 2           # 训练超时(小时)
  poll-interval-ms: 2000     # 进度轮询间隔

vector:
  build-timeout-hours: 1     # 向量构建超时
  batch-size: 5000           # ChromaDB 批量写入大小
```

---

## 8. 安全设计

### 8.1 上传文件安全

| 攻击类型 | 防御措施 |
|----------|----------|
| Zip Bomb | 限制解压后总大小 ≤ 500MB |
| 路径穿越 | 校验解压路径，拒绝 `..` |
| 伪造图片 | PIL 打开失败则跳过 |
| 超大单图 | 限制单文件 ≤ 10MB |
| 文件数超限 | 限制 zip 内文件数 ≤ 50000 |

### 8.2 凭证安全

- 敏感字段（S3 Secret Key、HTTP 密码）使用 AES-GCM 加密后存入数据库
- 解密仅在调用 Python 前进行，内存中使用后立即置空

### 8.3 ML 服务安全

- Python 服务仅监听 `127.0.0.1:8000`，不接受外部请求
- 无认证（内部服务，信任 Feign 调用来源）

---

## 9. 测试策略

### 9.1 Python 端测试

| 测试 | 内容 | 验证方式 |
|------|------|----------|
| 模型形状 | `encode()` 输出 (N, 512) | pytest assert |
| 数据源工厂 | URI 路由正确 | pytest + mock |
| 训练循环 | 进度递增、取消生效 | pytest + mock |
| API 路由 | 请求/响应格式 | TestClient |
| 安全防护 | zip bomb/路径穿越检测 | pytest assert |

### 9.2 Java 端测试

| 测试 | 内容 | 验证方式 |
|------|------|----------|
| Business 逻辑 | 数据集创建、训练启动 | JUnit + Mockito |
| Feign 客户端 | 请求构造、Fallback | JUnit + Mockito |
| 任务工厂 | 路由正确 | JUnit |
| Controller | API 可访问、参数校验 | SpringBootTest |

### 9.3 端到端验证

1. 上传本地 zip 图片库 → 训练 → 构建向量库 → 以图搜图
2. HTTP URL 数据源创建数据集
3. 异常场景：超大 zip、错误凭证、未训练即检索

---

## 10. 部署与运维

### 10.1 开发环境启动

```bash
# 1. 启动 Python ML 服务
cd ml_service
pip install -r requirements.txt
uvicorn app:app --host 127.0.0.1 --port 8000

# 2. 启动 Spring Boot 应用（正常启动 lacus-admin）
mvn spring-boot:run
```

### 10.2 生产环境建议

- Python 服务使用 systemd 或 supervisor 管理进程
- 模型存储可迁移到 MinIO
- 进度存储可迁移到 Redis
- 训练并发可改为线程池 + GPU 调度

---

## 11. 开放问题

| 问题 | 当前处理 | 未来可优化 |
|------|----------|------------|
| 进度存储 | 内存（重启丢失） | 迁移到 Redis |
| 并发控制 | 单任务串行 | 线程池 + GPU 调度 |
| 模型存储 | 本地磁盘 | MinIO 对象存储 |
| 训练超时 | 2 小时无进度标记 FAILED | 可配置化 |
| 用户认证 | 复用现有 Spring Security | 无需改动 |

---

## 12. 开发优先级

### Phase 1 — MVP（核心流程）
1. 数据库表 + 枚举 + Entity + Mapper
2. Python 服务：SimilarityAutoEncoder + BaseTrainer + SimilarityTrainer
3. Python 服务：LOCAL 数据源 + 训练 API + 向量构建 API + 检索 API
4. Java 端：Feign 客户端 + Business 层 + Controller 层
5. 前端：5 个核心页面
6. 端到端验证

### Phase 2 — 多数据源
1. HDFS 数据源实现
2. S3/MinIO 数据源实现
3. HTTP 数据源实现
4. 数据源探测 API

### Phase 3 — 完善
1. 模型管理页面
2. 安全防护（zip bomb、路径穿越）
3. 异常处理完善
4. 模型热加载缓存
