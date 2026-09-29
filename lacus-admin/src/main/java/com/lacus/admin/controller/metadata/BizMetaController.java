package com.lacus.admin.controller.metadata;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.dao.metadata.entity.BusinessMetadataEntity;
import com.lacus.service.metadata.IBusinessMetadataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "业务元数据", tags = {"业务元数据"})
@RestController
@RequestMapping("/metadata/bizmeta")
public class BizMetaController {

    @Autowired
    private IBusinessMetadataService businessMetadataService;

    @ApiOperation("查询对象的全部业务属性")
    @GetMapping("/{bizType}/{bizId}")
    public ResponseDTO<List<BusinessMetadataEntity>> getBizMeta(@PathVariable("bizType") String bizType,
                                                                @PathVariable("bizId") String bizId) {
        return ResponseDTO.ok(businessMetadataService.listByBiz(bizType, bizId));
    }

    @ApiOperation("批量保存业务属性(值为空串则删除)")
    @PreAuthorize("@permission.has('metadata:bizmeta:edit')")
    @PostMapping("/batch")
    public ResponseDTO<Void> saveBizMetaBatch(@RequestBody BatchSaveCommand command) {
        businessMetadataService.saveBatch(command.getBizType(), command.getBizId(), command.getItems());
        return ResponseDTO.ok();
    }

    /**
     * 批量保存命令, 与前端 businessMetaApi.saveBizMetaBatch 契约一致
     */
    public static class BatchSaveCommand {
        private String bizType;
        private String bizId;
        private List<BusinessMetadataEntity> items;

        public String getBizType() {
            return bizType;
        }

        public void setBizType(String bizType) {
            this.bizType = bizType;
        }

        public String getBizId() {
            return bizId;
        }

        public void setBizId(String bizId) {
            this.bizId = bizId;
        }

        public List<BusinessMetadataEntity> getItems() {
            return items;
        }

        public void setItems(List<BusinessMetadataEntity> items) {
            this.items = items;
        }
    }
}