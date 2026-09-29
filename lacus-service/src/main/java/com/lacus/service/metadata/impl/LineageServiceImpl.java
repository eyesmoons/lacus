package com.lacus.service.metadata.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.metadata.entity.LineageEdgeEntity;
import com.lacus.dao.metadata.entity.LineageNodeEntity;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.entity.MetaDbEntity;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import com.lacus.dao.metadata.mapper.LineageEdgeMapper;
import com.lacus.dao.metadata.mapper.LineageNodeMapper;
import com.lacus.service.metadata.ILineageService;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.service.metadata.IMetaDbService;
import com.lacus.service.metadata.IMetaTableService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LineageServiceImpl extends ServiceImpl<LineageEdgeMapper, LineageEdgeEntity>
        implements ILineageService {

    @Autowired
    private LineageNodeMapper lineageNodeMapper;

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    @Autowired
    private IMetaDbService metaDbService;

    @Autowired
    private IMetaTableService metaTableService;

    @Override
    public LineageNodeEntity getNodeByTableId(Long tableId) {
        return lineageNodeMapper.selectOne(new LambdaQueryWrapper<LineageNodeEntity>()
                .eq(LineageNodeEntity::getTableId, tableId));
    }

    @Override
    public void ensureNode(LineageNodeEntity node) {
        LineageNodeEntity existing = getNodeByTableId(node.getTableId());
        if (existing == null) {
            lineageNodeMapper.insert(node);
        }
    }

    @Override
    public List<LineageNodeEntity> listNodes(List<Long> nodeIds) {
        if (nodeIds == null || nodeIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<LineageNodeEntity> nodes = lineageNodeMapper.selectBatchIds(nodeIds);
        for (LineageNodeEntity node : nodes) {
            enrichNode(node);
        }
        return nodes;
    }

    @Override
    public List<LineageEdgeEntity> listEdges() {
        return list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEdge(Long sourceTableId, Long targetTableId, String depType, String remark) {
        LineageNodeEntity source = ensureOrCreateNode(sourceTableId);
        LineageNodeEntity target = ensureOrCreateNode(targetTableId);
        LineageEdgeEntity edge = new LineageEdgeEntity();
        edge.setSourceNodeId(source.getNodeId());
        edge.setTargetNodeId(target.getNodeId());
        edge.setDepType(StringUtils.defaultIfBlank(depType, "DIRECT"));
        edge.setSourceFlag("MANUAL");
        edge.setRemark(remark);
        save(edge);
    }

    @Override
    public void deleteEdge(Long edgeId) {
        removeById(edgeId);
    }

    /**
     * 删除某张表参与的全部血缘边(作为 source 或 target)。
     * 删除表时级联调用,避免孤儿边造成脏数据。
     */
    @Override
    public void deleteEdgesByTableId(Long tableId) {
        LineageNodeEntity node = getNodeByTableId(tableId);
        if (node == null) {
            return;
        }
        Long nodeId = node.getNodeId();
        LambdaQueryWrapper<LineageEdgeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LineageEdgeEntity::getSourceNodeId, nodeId)
                .or()
                .eq(LineageEdgeEntity::getTargetNodeId, nodeId);
        remove(wrapper);
        // 同时清理该表对应的节点
        lineageNodeMapper.deleteById(nodeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEdge(Long edgeId, String depType, String remark, Long sourceTableId, Long targetTableId) {
        LineageEdgeEntity edge = getById(edgeId);
        if (edge == null) {
            throw new CustomException("血缘关系不存在");
        }
        if (!"MANUAL".equals(edge.getSourceFlag())) {
            throw new CustomException("仅手动登记的血缘可编辑");
        }
        Long sourceNodeId = edge.getSourceNodeId();
        Long targetNodeId = edge.getTargetNodeId();
        if (sourceTableId != null) {
            sourceNodeId = ensureOrCreateNode(sourceTableId).getNodeId();
        }
        if (targetTableId != null) {
            targetNodeId = ensureOrCreateNode(targetTableId).getNodeId();
        }
        edge.setSourceNodeId(sourceNodeId);
        edge.setTargetNodeId(targetNodeId);
        if (StringUtils.isNotEmpty(depType)) {
            edge.setDepType(depType);
        }
        if (remark != null) {
            edge.setRemark(remark);
        }
        updateById(edge);
    }

    private LineageNodeEntity ensureOrCreateNode(Long tableId) {
        LineageNodeEntity node = getNodeByTableId(tableId);
        if (node != null) {
            return node;
        }
        MetaTableEntity metaTable = metaTableService.getById(tableId);
        LineageNodeEntity created = new LineageNodeEntity();
        created.setTableId(tableId);
        created.setNodeType("TABLE");
        if (metaTable != null) {
            created.setNodeName(metaTable.getTableName());
            if (metaTable.getDbId() != null) {
                MetaDbEntity metaDb = metaDbService.getById(metaTable.getDbId());
                if (metaDb != null) {
                    created.setDbName(metaDb.getDbName());
                    created.setDatasourceId(metaDb.getDatasourceId());
                }
            }
        } else {
            created.setNodeName(String.valueOf(tableId));
        }
        lineageNodeMapper.insert(created);
        return created;
    }

    private void enrichNode(LineageNodeEntity node) {
        if (node.getDatasourceId() == null || StringUtils.isNotEmpty(node.getDatasourceName())) {
            return;
        }
        MetaDatasourceEntity dataSource = metaDataSourceService.getById(node.getDatasourceId());
        if (dataSource != null) {
            node.setDatasourceName(dataSource.getDatasourceName());
        }
    }
}