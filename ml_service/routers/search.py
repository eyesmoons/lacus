"""相似度检索 API 路由

提供图像相似度搜索接口。
"""
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List

router = APIRouter()


class SearchRequest(BaseModel):
    """搜索请求体"""
    image_id: str
    top_k: int = 5
    collection_name: str = "image_collection"


class SearchResult(BaseModel):
    """搜索结果项"""
    id: str
    distance: float


class SearchResponse(BaseModel):
    """搜索响应"""
    results: List[SearchResult]


def _perform_search(image_id: str, top_k: int, collection_name: str) -> dict:
    """执行相似度搜索"""
    import chromadb
    import numpy as np

    client = chromadb.PersistentClient(path="./chroma_backend")
    collection = client.get_collection(name=collection_name)

    # 获取查询图像的向量
    result = collection.get(ids=[image_id], include=["embeddings"])
    if not result["embeddings"]:
        raise HTTPException(status_code=404, detail=f"图像 {image_id} 不存在")

    query_embedding = result["embeddings"][0]

    # 搜索相似向量
    search_result = collection.query(
        query_embeddings=[query_embedding],
        n_results=top_k + 1,  # 多取一个排除自身
    )

    results = []
    for id, distance in zip(search_result["ids"][0], search_result["distances"][0]):
        if id != image_id:
            results.append({"id": id, "distance": distance})
        if len(results) >= top_k:
            break

    return {"results": results}


@router.post("/api/search", response_model=SearchResponse)
async def search_similar(request: SearchRequest):
    """搜索相似图像"""
    result = _perform_search(request.image_id, request.top_k, request.collection_name)
    return SearchResponse(results=[SearchResult(**r) for r in result["results"]])
