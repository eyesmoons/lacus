package com.lacus.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.metadata.entity.LineageEdgeEntity;
import com.lacus.dao.metadata.entity.LineageNodeEntity;

import java.util.List;

public interface ILineageService extends IService<LineageEdgeEntity> {

    LineageNodeEntity getNodeByTableId(Long tableId);

    void ensureNode(LineageNodeEntity node);

    List<LineageNodeEntity> listNodes(List<Long> nodeIds);

    List<LineageEdgeEntity> listEdges();

    void addEdge(Long sourceTableId, Long targetTableId, String depType, String remark);

    void deleteEdge(Long edgeId);

    void deleteEdgesByTableId(Long tableId);

    void updateEdge(Long edgeId, String depType, String remark, Long sourceTableId, Long targetTableId);
}