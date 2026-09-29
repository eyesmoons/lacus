package com.lacus.admin.controller.metadata;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.dao.metadata.entity.LineageEdgeEntity;
import com.lacus.dao.metadata.entity.LineageNodeEntity;
import com.lacus.service.metadata.ILineageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Api(value = "数据血缘", tags = {"数据血缘"})
@RestController
@RequestMapping("/metadata/lineage")
public class LineageController {

    @Autowired
    private ILineageService lineageService;

    @ApiOperation("血缘图查询")
    @GetMapping("/graph")
    public ResponseDTO<Map<String, Object>> getLineageGraph(@RequestParam("tableId") Long tableId,
                                                            @RequestParam(value = "direction", defaultValue = "full") String direction,
                                                            @RequestParam(value = "depth", defaultValue = "5") Integer depth) {
        LineageNodeEntity center = lineageService.getNodeByTableId(tableId);
        List<LineageNodeEntity> nodes = new ArrayList<>();
        List<LineageEdgeEntity> edges = new ArrayList<>();
        if (center != null) {
            collectGraph(center.getNodeId(), direction, depth, nodes, edges);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("nodes", nodes);
        result.put("edges", edges);
        return ResponseDTO.ok(result);
    }

    @ApiOperation("登记血缘关系")
    @PreAuthorize("@permission.has('metadata:lineage:edit')")
    @PostMapping("/edge")
    public ResponseDTO<Void> addLineageEdge(@RequestBody AddEdgeCommand command) {
        lineageService.addEdge(command.getSourceTableId(), command.getTargetTableId(),
                command.getDepType(), command.getRemark());
        return ResponseDTO.ok();
    }

    @ApiOperation("删除血缘关系（仅 MANUAL 来源）")
    @PreAuthorize("@permission.has('metadata:lineage:edit')")
    @DeleteMapping("/edge/{edgeId}")
    public ResponseDTO<Void> deleteLineageEdge(@PathVariable("edgeId") Long edgeId) {
        LineageEdgeEntity edge = lineageService.getById(edgeId);
        if (edge != null && "MANUAL".equals(edge.getSourceFlag())) {
            lineageService.deleteEdge(edgeId);
        }
        return ResponseDTO.ok();
    }

    @ApiOperation("查询单个血缘节点信息")
    @GetMapping("/node/{tableId}")
    public ResponseDTO<LineageNodeEntity> getLineageNode(@PathVariable("tableId") Long tableId) {
        return ResponseDTO.ok(lineageService.getNodeByTableId(tableId));
    }

    @ApiOperation("编辑血缘关系（仅 MANUAL 来源可编辑，可选改源/目标表）")
    @PreAuthorize("@permission.has('metadata:lineage:edit')")
    @PutMapping("/edge/{edgeId}")
    public ResponseDTO<Void> updateLineageEdge(@PathVariable("edgeId") Long edgeId,
                                               @RequestBody UpdateEdgeCommand command) {
        lineageService.updateEdge(edgeId, command.getDepType(), command.getRemark(),
                command.getSourceTableId(), command.getTargetTableId());
        return ResponseDTO.ok();
    }

    public static class UpdateEdgeCommand {
        private String depType;
        private String remark;
        private Long sourceTableId;
        private Long targetTableId;

        public String getDepType() {
            return depType;
        }

        public void setDepType(String depType) {
            this.depType = depType;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Long getSourceTableId() {
            return sourceTableId;
        }

        public void setSourceTableId(Long sourceTableId) {
            this.sourceTableId = sourceTableId;
        }

        public Long getTargetTableId() {
            return targetTableId;
        }

        public void setTargetTableId(Long targetTableId) {
            this.targetTableId = targetTableId;
        }
    }

    private void collectGraph(Long centerNodeId, String direction, Integer depth,
                              List<LineageNodeEntity> nodes, List<LineageEdgeEntity> edges) {
        int maxDepth = depth == null || depth < 0 ? 5 : depth;
        Set<Long> visitedNodes = new HashSet<>();
        Set<Long> visitedEdges = new HashSet<>();
        Set<String> nodeKey = new HashSet<>();
        Set<String> edgeKey = new HashSet<>();

        List<LineageEdgeEntity> allEdges = lineageService.listEdges();
        List<LineageNodeEntity> allNodes = lineageService.listNodes(
                allEdges.stream()
                        .flatMap(e -> java.util.stream.Stream.of(e.getSourceNodeId(), e.getTargetNodeId()))
                        .distinct().collect(Collectors.toList()));
        if (allNodes.isEmpty()) {
            allNodes = lineageService.listNodes(java.util.Collections.singletonList(centerNodeId));
        }

        Map<Long, LineageNodeEntity> byId = allNodes.stream()
                .collect(Collectors.toMap(LineageNodeEntity::getNodeId, n -> n));

        // BFS 按 direction 展开
        List<Long> layer = new ArrayList<>();
        if (byId.containsKey(centerNodeId)) {
            layer.add(centerNodeId);
            visitedNodes.add(centerNodeId);
            collectNode(centerNodeId, byId, nodeKey, nodes);
        }
        for (int hop = 0; hop < maxDepth && !layer.isEmpty(); hop++) {
            List<Long> next = new ArrayList<>();
            for (Long nodeId : layer) {
                for (LineageEdgeEntity edge : allEdges) {
                    Long neighbor = null;
                    boolean match = false;
                    if ("upstream".equalsIgnoreCase(direction)) {
                        match = edge.getTargetNodeId().equals(nodeId);   // 上游: 边指向当前节点
                        neighbor = edge.getSourceNodeId();
                    } else if ("downstream".equalsIgnoreCase(direction)) {
                        match = edge.getSourceNodeId().equals(nodeId);   // 下游: 边由当前节点指出
                        neighbor = edge.getTargetNodeId();
                    } else {
                        // full: 双向
                        if (edge.getTargetNodeId().equals(nodeId)) {
                            match = true;
                            neighbor = edge.getSourceNodeId();
                        } else if (edge.getSourceNodeId().equals(nodeId)) {
                            match = true;
                            neighbor = edge.getTargetNodeId();
                        }
                    }
                    if (match && !visitedEdges.contains(edge.getEdgeId())) {
                        visitedEdges.add(edge.getEdgeId());
                        collectEdge(edge, edgeKey, edges);
                    }
                    if (match && neighbor != null && !visitedNodes.contains(neighbor)) {
                        visitedNodes.add(neighbor);
                        collectNode(neighbor, byId, nodeKey, nodes);
                        next.add(neighbor);
                    }
                }
            }
            layer = next;
        }
    }

    private void collectNode(Long nodeId, Map<Long, LineageNodeEntity> byId,
                             Set<String> seen, List<LineageNodeEntity> out) {
        LineageNodeEntity node = byId.get(nodeId);
        if (node == null) {
            return;
        }
        String key = String.valueOf(node.getNodeId());
        if (!seen.contains(key)) {
            seen.add(key);
            out.add(node);
        }
    }

    private void collectEdge(LineageEdgeEntity edge, Set<String> seen, List<LineageEdgeEntity> out) {
        String key = edge.getEdgeId() + ":" + edge.getSourceNodeId() + "->" + edge.getTargetNodeId();
        if (!seen.contains(key)) {
            seen.add(key);
            out.add(edge);
        }
    }

    public static class AddEdgeCommand {
        private Long sourceTableId;
        private Long targetTableId;
        private String depType;
        private String remark;

        public Long getSourceTableId() {
            return sourceTableId;
        }

        public void setSourceTableId(Long sourceTableId) {
            this.sourceTableId = sourceTableId;
        }

        public Long getTargetTableId() {
            return targetTableId;
        }

        public void setTargetTableId(Long targetTableId) {
            this.targetTableId = targetTableId;
        }

        public String getDepType() {
            return depType;
        }

        public void setDepType(String depType) {
            this.depType = depType;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}