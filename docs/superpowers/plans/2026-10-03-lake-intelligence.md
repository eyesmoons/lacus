---
status: final
archived-with: 2026-10-04-lake-intelligence
---
# 湖智模块（Lake Intelligence）实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 lacus 多模块 Maven 项目中新增"湖智"图像相似度检索模块，实现上传图片库→训练自编码器→构建向量库→以图搜图的完整流程。

**Architecture:** Spring Boot 后端（lacus-admin 控制器 + lacus-domain 业务逻辑 + lacus-dao 数据访问）通过 Feign 调用独立的 Python ML 服务（FastAPI + PyTorch + ChromaDB）。遵循现有 admin/domain/dao 分层架构模式。

**Tech Stack:** Java 8, Spring Boot 2.7.15, MyBatis-Plus, Spring Cloud OpenFeign, Thymeleaf, Bootstrap 5, Chart.js, Python 3.9+, FastAPI, PyTorch 2.0+, ChromaDB 0.4+

**Spec:** docs/superpowers/specs/2026-10-03-lake-intelligence-design.md

## Global Constraints

- 产物语言：zh-CN（所有注释、文档、commit message 使用中文）
- 数据库表前缀：`lake_`（避免与现有表冲突）
- 包路径前缀：`com.lacus.admin.controller.lakeintelligence`、`com.lacus.domain.lakeintelligence`
- Python 服务仅监听 `127.0.0.1:8000`
- 训练并发：单任务串行
- 进度同步：内存存储 + 2秒轮询
- 文件上传限制：zip 最大 500MB，单张图片最大 10MB，zip 内文件数最大 50000
- 训练超时：2小时无进度自动标记 FAILED
- 下载超时：30分钟无进度自动标记 FAILED
- 向量构建超时：1小时无进度自动标记 ERROR

---

## Task 1.1: 创建数据库表结构

**Files:**
- Create: `sql/lake_intelligence_ddl.sql`

**Verify:** `grep -c "CREATE TABLE" sql/lake_intelligence_ddl.sql` → 4

---

## Task 1.2: 新增枚举类

**Files:**
- Create: `lacus-common/src/main/java/com/lacus/enums/StorageSource.java`
- Create: `lacus-common/src/main/java/com/lacus/enums/TaskType.java`
- Create: `lacus-common/src/main/java/com/lacus/enums/TaskStatus.java`
- Create: `lacus-common/src/main/java/com/lacus/enums/DatasetStatus.java`

**Verify:** `cd lacus-common && mvn compile -q`

---

## Task 1.3: 新增 Entity 类

**Files:**
- Create: `lacus-dao/src/main/java/com/lacus/dao/entity/LakeDatasetEntity.java`
- Create: `lacus-dao/src/main/java/com/lacus/dao/entity/LakeTaskEntity.java`
- Create: `lacus-dao/src/main/java/com/lacus/dao/entity/LakeModelInfoEntity.java`
- Create: `lacus-dao/src/main/java/com/lacus/dao/entity/LakeVectorIndexEntity.java`

**Verify:** `cd lacus-dao && mvn compile -q`

---

## Task 1.4: 新增 Mapper 接口

**Files:**
- Create: `lacus-dao/src/main/java/com/lacus/dao/mapper/LakeDatasetMapper.java`
- Create: `lacus-dao/src/main/java/com/lacus/dao/mapper/LakeTaskMapper.java`
- Create: `lacus-dao/src/main/java/com/lacus/dao/mapper/LakeModelInfoMapper.java`
- Create: `lacus-dao/src/main/java/com/lacus/dao/mapper/LakeVectorIndexMapper.java`

**Verify:** `cd lacus-dao && mvn compile -q`

---

## Task 2.1: 创建 Python 项目结构

**Files:**
- Create: `ml_service/app.py`, `ml_service/config.py`, `ml_service/requirements.txt`
- Create: `ml_service/routers/`, `ml_service/core/`, `ml_service/tasks/`, `ml_service/models/`, `ml_service/sources/`, `ml_service/utils/`

**Verify:** `ls ml_service/routers/ ml_service/core/ ml_service/tasks/ ml_service/models/ ml_service/sources/ ml_service/utils/`

---

## Task 2.2: 实现 SimilarityAutoEncoder 模型

**Files:**
- Create: `ml_service/models/similarity_autoencoder.py`
- Create: `ml_service/tests/test_model.py`

**Verify:** `cd ml_service && python -m pytest tests/test_model.py -v` → PASS

---

## Task 2.3: 实现 BaseTrainer 训练基类

**Files:**
- Create: `ml_service/core/trainer_base.py`

**Verify:** `cd ml_service && python -c "from core.trainer_base import BaseTrainer; print('OK')"`

---

## Task 2.4: 实现 SimilarityTrainer

**Files:**
- Create: `ml_service/tasks/similarity_trainer.py`

**Verify:** `cd ml_service && python -c "from tasks.similarity_trainer import SimilarityTrainer; print('OK')"`

---

## Task 2.5: 实现 TaskRegistry

**Files:**
- Create: `ml_service/core/task_registry.py`

**Verify:** `cd ml_service && python -c "from core.task_registry import create_trainer; print('OK')"`

---

## Task 2.6: 实现数据源抽象和工厂

**Files:**
- Create: `ml_service/core/dataset_source.py`
- Create: `ml_service/core/source_factory.py`

**Verify:** `cd ml_service && python -c "from core.source_factory import SourceFactory; print('OK')"`

---

## Task 2.7: 实现 LOCAL 数据源

**Files:**
- Create: `ml_service/sources/local_source.py`

**Verify:** `cd ml_service && python -m pytest tests/test_local_source.py -v` → PASS

---

## Task 2.8: 实现 HDFS 数据源

**Files:**
- Create: `ml_service/sources/hdfs_source.py`

**Verify:** `cd ml_service && python -c "from sources.hdfs_source import HdfsSource; print('OK')"`

---

## Task 2.9: 实现 S3/MinIO 数据源

**Files:**
- Create: `ml_service/sources/s3_source.py`

**Verify:** `cd ml_service && python -c "from sources.s3_source import S3Source; print('OK')"`

---

## Task 2.10: 实现 HTTP 数据源

**Files:**
- Create: `ml_service/sources/http_source.py`

**Verify:** `cd ml_service && python -m pytest tests/test_http_source.py -v` → PASS

---

## Task 2.11: 实现训练 API 路由

**Files:**
- Create: `ml_service/routers/train.py`

**Verify:** `cd ml_service && python -m pytest tests/test_train_router.py -v` → PASS

---

## Task 2.12: 实现向量和检索 API 路由

**Files:**
- Create: `ml_service/routers/vectors.py`
- Create: `ml_service/routers/search.py`

**Verify:** `cd ml_service && python -m pytest tests/test_vectors_search_routers.py -v` → PASS

---

## Task 2.13: 实现数据源探测 API

**Files:**
- Create: `ml_service/routers/dataset_source.py`

**Verify:** `cd ml_service && python -m pytest tests/test_dataset_source_router.py -v` → PASS

---

## Task 2.14: 实现向量构建服务

**Files:**
- Create: `ml_service/utils/vector_builder.py`

**Verify:** `cd ml_service && python -c "from utils.vector_builder import do_build_vectors; print('OK')"`

---

## Task 2.15: 实现模型缓存

**Files:**
- Create: `ml_service/core/model_cache.py`

**Verify:** `cd ml_service && python -m pytest tests/test_model_cache.py -v` → PASS

---

## Task 3.1: 创建 Domain 层包结构

**Files:**
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/{command,query,feign,dto,task}/`

**Verify:** `ls lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/`

---

## Task 3.2: 实现 Feign 客户端

**Files:**
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/feign/MlServiceFeign.java`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/feign/MlServiceFeignConfiguration.java`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/feign/MlServiceFeignFallbackFactory.java`

**Verify:** `cd lacus-domain && mvn compile -q`

---

## Task 3.3: 实现 Command/Query/DTO 类

**Files:**
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/command/`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/query/`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/dto/`

**Verify:** `cd lacus-domain && mvn compile -q`

---

## Task 3.4: 实现 Business 层

**Files:**
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/DatasetBusiness.java`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/TrainBusiness.java`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/VectorIndexBusiness.java`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/SearchBusiness.java`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/ModelBusiness.java`

**Verify:** `cd lacus-domain && mvn compile -q`

---

## Task 3.5: 实现 TaskHandler 扩展机制

**Files:**
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/task/TaskHandler.java`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/task/TaskHandlerFactory.java`
- Create: `lacus-domain/src/main/java/com/lacus/domain/lakeintelligence/task/SimilarityTaskHandler.java`

**Verify:** `cd lacus-domain && mvn compile -q`

---

## Task 4.1: 实现 Controller 层

**Files:**
- Create: `lacus-admin/src/main/java/com/lacus/admin/controller/lakeintelligence/DatasetController.java`
- Create: `lacus-admin/src/main/java/com/lacus/admin/controller/lakeintelligence/TrainController.java`
- Create: `lacus-admin/src/main/java/com/lacus/admin/controller/lakeintelligence/VectorController.java`
- Create: `lacus-admin/src/main/java/com/lacus/admin/controller/lakeintelligence/SearchController.java`
- Create: `lacus-admin/src/main/java/com/lacus/admin/controller/lakeintelligence/ModelController.java`
- Create: `lacus-admin/src/main/java/com/lacus/admin/controller/lakeintelligence/PageController.java`

**Verify:** `cd lacus-admin && mvn compile -q`

---

## Task 5.1: 实现前端页面和 JS

**Files:**
- Create: `lacus-admin/src/main/resources/templates/pages/lake-intelligence/` (7 HTML files)
- Create: `lacus-admin/src/main/resources/static/js/lake-intelligence/` (JS files)

**Verify:** `ls lacus-admin/src/main/resources/templates/pages/lake-intelligence/`

---

## Task 6.1: 更新配置

**Files:**
- Modify: `lacus-admin/src/main/resources/application.yml`

**Verify:** `grep "ml.service.url" lacus-admin/src/main/resources/application.yml`

---

## Task 6.2: 实现工具类

**Files:**
- Create: `FileStorageConfig.java`, `CredentialCrypto.java`, `AsyncConfig.java`
- Modify: `GlobalExceptionHandler.java`
- Create: `SecurityUtils.java`

**Verify:** `mvn compile -q`

---

## Task 7.1: 端到端验证

**Verify:**
- `mvn clean compile -o -pl lacus-admin -am` → BUILD SUCCESS
- `cd ml_service && python -m pytest tests/ -q` → 91 passed
