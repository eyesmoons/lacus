"""分类推理 API 路由

提供图像分类推理接口，接收图片文件并返回预测类别与置信度。
"""
import io
from typing import List

import torch
import torchvision.transforms as T
from fastapi import APIRouter, File, Form, HTTPException, UploadFile
from PIL import Image

from config import default_config
from core.model_cache import ModelCache

router = APIRouter()

# 分类类别名称表（可按需扩展或改为外部配置）
DEFAULT_CLASS_NAMES: List[str] = ["猫", "狗", "鸟", "鱼", "兔"]


def _load_image_as_tensor(upload: UploadFile) -> torch.Tensor:
    """读取上传图片并预处理为模型输入张量"""
    data = upload.file.read()
    img = Image.open(io.BytesIO(data)).convert("RGB")
    transform = T.Compose([
        T.Resize((default_config.img_h, default_config.img_w)),
        T.ToTensor(),
    ])
    return transform(img).unsqueeze(0)


def _class_names_for(n_classes: int) -> List[str]:
    """根据类别数生成类别名称列表"""
    if n_classes <= len(DEFAULT_CLASS_NAMES):
        return DEFAULT_CLASS_NAMES[:n_classes]
    # 超出预定义类别时，用 "class_0", "class_1", ... 填充
    names = list(DEFAULT_CLASS_NAMES)
    names.extend(f"class_{i}" for i in range(len(names), n_classes))
    return names


@router.post("/api/classify")
async def classify_image(
    image: UploadFile = File(..., description="待分类的图片文件"),
    model_id: str = Form("classifier", description="模型标识符"),
):
    """图像分类推理

    接收图片文件，加载指定分类模型，返回预测类别、置信度与 class_id。
    """
    if not image or not image.filename:
        raise HTTPException(status_code=422, detail="请提供图片文件")

    # 1. 加载模型
    cache = ModelCache()
    model = cache.get_model(model_id)
    model.eval()

    # 2. 预处理图片
    tensor = _load_image_as_tensor(image)

    # 3. 推理
    with torch.no_grad():
        logits = model(tensor)

    if logits.dim() != 2 or logits.shape[0] != 1:
        raise HTTPException(status_code=500, detail="模型输出维度异常")

    logits = logits.squeeze(0)
    probabilities = torch.softmax(logits, dim=0)
    confidence, class_id = torch.max(probabilities, dim=0)
    class_id_int = int(class_id.item())
    confidence_float = float(confidence.item())

    # 4. 映射类别名称
    n_classes = logits.shape[0]
    names = _class_names_for(n_classes)
    class_name = names[class_id_int] if class_id_int < len(names) else f"class_{class_id_int}"

    return {
        "class_name": class_name,
        "confidence": confidence_float,
        "class_id": class_id_int,
    }
