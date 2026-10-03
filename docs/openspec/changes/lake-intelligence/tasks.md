# 实施任务清单

## 1. 数据库与基础设施

- [x] 1.1 编写 4 张新表的 CREATE SQL 脚本（datasets, tasks, model_info, vector_indexes），验证 SQL 语法正确
- [x] 1.2 在 `lacus-common` 中新增枚举类：StorageSource、TaskType、TaskStatus、DatasetStatus，验证编译通过
- [x] 1.3 在 `lacus-dao` 中新增 Entity 类：DatasetEntity、TaskEntity、ModelInfoEntity、VectorIndexEntity（使用 @TableName 注解），验证编译通过
- [x] 1.4 在 `lacus-dao` 中新增 Mapper 接口：DatasetMapper、TaskMapper、ModelInfoMapper、VectorIndexMapper，验证编译通过

## 2. Python ML 服务

- [ ] 2.1 创建 `ml_service/` 目录结构（app.py, config.py, requirements.txt, routers/, core/, tasks/, models/, sources/, utils/），验证目录结构完整
- [ ] 2.2 实现合并的 `SimilarityAutoEncoder` 模型（`models/similarity_autoencoder.py`），验证 `model.encode()` 输出形状为 (N, 512)
- [ ] 2.3 实现训练基类 `BaseTrainer`（`core/trainer_base.py`），包含通用训练循环和进度回调，验证类可实例化
- [ ] 2.4 实现 `SimilarityTrainer`（`tasks/similarity_trainer.py`），继承 BaseTrainer，验证可创建实例
- [ ] 2.5 实现任务注册工厂 `TaskRegistry`（`core/task_registry.py`），验证可通过任务类型创建对应训练器
- [ ] 2.6 实现数据源抽象基类 `DatasetSource` 和工厂 `SourceFactory`（`core/dataset_source.py`, `core/source_factory.py`），验证工厂可路由到对应实现
- [ ] 2.7 实现 LOCAL 数据源（`sources/local_source.py`），验证 probe() 返回正确文件数
- [ ] 2.8 实现 HDFS 数据源（`sources/hdfs_source.py`），验证 probe() 可连接 HDFS
- [ ] 2.9 实现 S3/MinIO 数据源（`sources/s3_source.py`），验证 probe() 可列出 bucket 文件
- [ ] 2.10 实现 HTTP 数据源（`sources/http_source.py`），验证 probe() 返回文件大小
- [ ] 2.11 实现 FastAPI 路由：`/api/train`（启动训练）、`/api/train/{task_id}`（查询进度）、`/api/train/{task_id}/cancel`（取消训练），验证接口可访问
- [ ] 2.12 实现 FastAPI 路由：`/api/vectors/build`（构建向量库）、`/api/search`（相似检索），验证接口可访问
- [ ] 2.13 实现 FastAPI 路由：`/api/dataset/probe-source`（数据源探测），验证返回正确探测结果
- [ ] 2.14 实现向量构建服务（参考 `similarity_embeddings.py`），验证可批量提取 embedding 并写入 ChromaDB
- [ ] 2.15 实现模型缓存 `ModelCache`（`core/model_cache.py`），验证相同模型路径只加载一次

## 3. Spring Boot 后端 — Domain 层

- [ ] 3.1 在 `lacus-domain` 中新增 `lakeintelligence` 包结构（command/, query/, feign/, dto/, model/），验证目录创建
- [ ] 3.2 实现 Feign 客户端 `MlServiceFeign`（调用 Python ML 服务），遵循 SchedulerFeign 模式（url 从配置读取 + fallback + configuration），验证编译通过
- [ ] 3.3 实现 Feign 配置 `MlServiceFeignConfiguration`（设置超时和请求头），验证编译通过
- [ ] 3.4 实现 Feign Fallback `MlServiceFeignFallbackFactory`，验证服务不可用时返回友好错误
- [ ] 3.5 实现 Command 类：CreateDatasetRequest、TrainRequest、SearchRequest、BuildVectorRequest，验证编译通过
- [ ] 3.6 实现 Query 类：DatasetPageQuery、TaskPageQuery、ModelPageQuery，验证编译通过
- [ ] 3.7 实现 DTO 类：DatasetDTO、TaskDTO、ModelInfoDTO、VectorIndexDTO、ProgressResponse、SearchResponse，验证编译通过
- [ ] 3.8 实现 `DatasetBusiness`：创建数据集、数据源探测、数据集删除，验证方法可调用
- [ ] 3.9 实现 `TrainBusiness`：启动训练、查询进度、取消训练，验证方法可调用
- [ ] 3.10 实现 `VectorIndexBusiness`：构建向量库、查询构建进度，验证方法可调用
- [ ] 3.11 实现 `SearchBusiness`：相似检索，验证方法可调用
- [ ] 3.12 实现 `ModelBusiness`：模型列表、模型详情、模型下载、模型删除，验证方法可调用
- [ ] 3.13 实现 `TaskHandler` 接口和 `TaskHandlerFactory`，验证 Spring 自动注入可发现处理器
- [ ] 3.14 实现 `SimilarityTaskHandler`，验证返回正确的配置 Schema

## 4. Spring Boot 后端 — Admin 层

- [ ] 4.1 在 `lacus-admin` 中新增 `controller/lakeintelligence/` 包，验证目录创建
- [ ] 4.2 实现 `DatasetController`：POST /api/lake-intelligence/datasets（创建数据集）、POST /api/lake-intelligence/datasets/probe-source（探测数据源）、GET /api/lake-intelligence/datasets/{id}/preview（预览数据集）、DELETE /api/lake-intelligence/datasets/{id}（删除数据集），验证接口可访问
- [ ] 4.3 实现 `TrainController`：POST /api/lake-intelligence/tasks（启动训练）、GET /api/lake-intelligence/tasks/{id}/progress（查询进度）、POST /api/lake-intelligence/tasks/{id}/cancel（取消训练），验证接口可访问
- [ ] 4.4 实现 `VectorController`：POST /api/lake-intelligence/vectors/build（构建向量库）、GET /api/lake-intelligence/vectors/{id}/progress（查询进度），验证接口可访问
- [ ] 4.5 实现 `SearchController`：POST /api/lake-intelligence/search（相似检索），验证接口可访问
- [ ] 4.6 实现 `ModelController`：GET /api/lake-intelligence/models（模型列表）、GET /api/lake-intelligence/models/{id}/download（下载模型）、DELETE /api/lake-intelligence/models/{id}（删除模型），验证接口可访问
- [ ] 4.7 实现 `PageController`：页面路由（dataset/upload, dataset/{id}/preview, training/new, training/{id}, vector-build, search, models），验证页面可访问

## 5. 前端页面

- [ ] 5.1 实现 `dataset-upload.html`（图片库上传页）：数据源切换逻辑、文件上传、测试连接、创建数据集，验证页面渲染正确
- [ ] 5.2 实现 `dataset-preview.html`（图片库预览页）：缩略图网格、基本信息展示、操作按钮，验证页面渲染正确
- [ ] 5.3 实现 `training-new.html`（训练配置页）：参数表单（epochs, lr, batch_size 滑块）、提交训练，验证页面渲染正确
- [ ] 5.4 实现 `training-progress.html`（训练进度页）：损失曲线（Chart.js）、进度条、取消按钮，验证页面渲染正确
- [ ] 5.5 实现 `vector-build.html`（向量库构建页）：构建按钮、进度展示，验证页面渲染正确
- [ ] 5.6 实现 `search.html`（相似检索页）：图片上传、Top-K 结果网格、相似度标签，验证页面渲染正确
- [ ] 5.7 实现 `model-manager.html`（模型管理页）：模型列表、下载/删除操作，验证页面渲染正确
- [ ] 5.8 实现前端 JS：dataset-upload.js（数据源切换）、training.js（进度轮询）、search.js（结果展示），验证 JS 加载无报错

## 6. 配置与集成

- [ ] 6.1 更新 `application.yml`：新增 `ml.service.url`、`storage.root`、`app.crypto.key` 配置项，验证配置加载正确
- [ ] 6.2 实现 `FileStorageConfig`：文件存储路径配置，验证文件可写入
- [ ] 6.3 实现 `CredentialCrypto`：AES-GCM 加解密工具类，验证加密/解密结果一致
- [ ] 6.4 实现 `AsyncConfig`：异步线程池配置（并发训练任务限制），验证线程池创建
- [ ] 6.5 实现 `GlobalExceptionHandler` 新增异常处理：SourceUnreachableException、DownloadFailedException、QuotaExceededException，验证异常返回正确错误码
- [ ] 6.6 实现安全防护工具类 `SecurityUtils`（zip bomb 检测、路径穿越检测），验证可检测恶意文件

## 7. 端到端验证

- [ ] 7.1 完整流程验证：上传本地 zip 图片库 → 训练 → 构建向量库 → 以图搜图，验证全流程可走通
- [ ] 7.2 多数据源验证：分别测试 LOCAL、HTTP URL 数据源创建数据集，验证数据源工厂路由正确
- [ ] 7.3 异常场景验证：上传超大 zip、错误凭证、未训练即检索，验证错误处理正确
- [ ] 7.4 任务扩展验证：新增一个模拟的 `MockTaskHandler`，验证工厂自动发现并路由
