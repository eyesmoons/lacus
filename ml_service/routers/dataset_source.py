"""数据源探测 API 路由

提供数据源连通性和文件数量探测接口。
"""
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional

from core.source_factory import SourceFactory

router = APIRouter()


class ProbeSourceRequest(BaseModel):
    """探测请求体"""
    uri: str


class ProbeSourceResponse(BaseModel):
    """探测响应"""
    uri: str
    count: int = 0
    accessible: bool = True
    error: Optional[str] = None


@router.post("/api/dataset/probe-source", response_model=ProbeSourceResponse)
async def probe_source(request: ProbeSourceRequest):
    """探测数据源，返回可访问状态和文件数量"""
    try:
        source = SourceFactory.create(request.uri)
        count = source.probe()
        return ProbeSourceResponse(
            uri=request.uri,
            count=count,
            accessible=True,
        )
    except Exception as e:
        return ProbeSourceResponse(
            uri=request.uri,
            count=0,
            accessible=False,
            error=str(e),
        )
