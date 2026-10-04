"""相似度检索 API 路由

提供图像相似度搜索接口，支持图片上传和 image_id 两种查询方式。
"""
from typing import List, Optional

import chromadb
import numpy as np
import torch
import torchvision.transforms as T
from fastapi import APIRouter, File, Form, HTTPException, UploadFile
from PIL import Image
from pydantic import BaseModel

from core.model_cache import ModelCache
from config import default_config

router = APIRouter()


class SearchResult(BaseModel):
    """搜索结果项"""
    id: str
    distance: float


class SearchResponse(BaseModel):
    """搜索响应"""
    results: List[SearchResult]


def _extract_embedding_from_image(upload: UploadFile) -> list:
    """从上传的图片文件中提取 embedding 向量"""
    data = upload.file.read()
    img = Image.open(io.BytesIO(data)).convert("RGB")
    transform = T.Compose([
        T.Resize((default_config.img_h, default_config.img_w)),
        T.ToTensor(),
    ])
    tensor = transform(img).unsqueeze(0)

    cache = ModelCache()
    model = cache.get_model("similarity_autoencoder")
    model.eval()
    with torch.no_grad():
        embedding = model.encode(tensor)
    return embedding.numpy().tolist()[0]


def _extract_embedding_from_image(upload: UploadFile) -> list:
    """从上传的图片文件中提取 embedding 向量"""
    data = upload.file.read()
    img = Image.open(io.BytesIO(data)).convert("RGB")
    transform = T.Compose([
        T.Resize((default_config.img_h, default_config.img_w)),
        T.ToTensor(),
    ])
    tensor = transform(img).unsqueeze(0)

    cache = ModelCache()
    model = cache.get_model("similarity_autoencoder")
    model.eval()
    with torch.no_grad():
        embedding = model.encode(tensor)
    return embedding.numpy().tolist()[0]


def _perform_search(
    query_embedding: Optional[list],
    image: Optional[UploadFile],
    image_id: Optional[str],
    top_k: int,
    collection_name: str,
) -> dict:
    """执行相似度搜索

    支持三种查询方式：
    - 直接传入 query_embedding
    - 上传图片文件，自动提取 embedding
    - 传入 image_id，从 ChromaDB 查询已有 embedding
    """
    # 解析查询向量
    if query_embedding is not None:
        embedding = query_embedding
    elif image is not None:
        embedding = _extract_embedding_from_image(image)
    elif image_id:
        client = chromadb.PersistentClient(path=default_config.chroma_backend_path)
        collection = client.get_collection(name=collection_name)
        result = collection.get(ids=[image_id], include=["embeddings"])
        if not result["embeddings"]:
            raise HTTPException(status_code=404, detail=f"图像 {image_id} 不存在")
        embedding = result["embeddings"][0]
    else:
        raise HTTPException(status_code=400, detail="请提供图片或 image_id")

    # 搜索相似向量
    client = chromadb.PersistentClient(path=default_config.chroma_backend_path)
    collection = client.get_collection(name=collection_name)

    search_result = collection.query(
        query_embeddings=[embedding],
        n_results=top_k,
    )

    results = []
    for id, distance in zip(search_result["ids"][0], search_result["distances"][0]):
        results.append({"id": id, "distance": distance})

    return {"results": results}


@router.post("/api/search", response_model=SearchResponse)
async def search_similar(
    top_k: int = Form(5),
    collection_name: str = Form("image_collection"),
    image_id: Optional[str] = Form(None),
    image: Optional[UploadFile] = File(None),
):
    """搜索相似图像（支持图片上传或 image_id）"""
    import io

    result = _perform_search(None, image, image_id, top_k, collection_name)
    return SearchResponse(results=[SearchResult(**r) for r in result["results"]])
