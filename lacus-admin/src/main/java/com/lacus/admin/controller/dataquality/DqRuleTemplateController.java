package com.lacus.admin.controller.dataquality;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.dao.dataquality.entity.DqRuleTemplateEntity;
import com.lacus.domain.dataquality.DqRuleTemplateBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "数据质量规则模板", tags = {"数据质量规则模板管理"})
@RestController
@RequestMapping("/dq/template")
public class DqRuleTemplateController {

    @Autowired
    private DqRuleTemplateBusiness dqRuleTemplateBusiness;

    @ApiOperation("查询启用的模板列表（前端选择器使用）")
    @GetMapping("/list")
    public ResponseDTO<List<DqRuleTemplateEntity>> listEnabled() {
        return ResponseDTO.ok(dqRuleTemplateBusiness.listEnabled());
    }

    @ApiOperation("查询全部模板（管理用）")
    @GetMapping("/listAll")
    public ResponseDTO<List<DqRuleTemplateEntity>> listAll() {
        return ResponseDTO.ok(dqRuleTemplateBusiness.listAll());
    }

    @ApiOperation("新增模板")
    @PostMapping
    public ResponseDTO<DqRuleTemplateEntity> addTemplate(@RequestBody DqRuleTemplateEntity entity) {
        return ResponseDTO.ok(dqRuleTemplateBusiness.addTemplate(entity));
    }

    @ApiOperation("修改模板")
    @PutMapping
    public ResponseDTO<Void> updateTemplate(@RequestBody DqRuleTemplateEntity entity) {
        dqRuleTemplateBusiness.updateTemplate(entity);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除模板")
    @DeleteMapping("/{id}")
    public ResponseDTO<Void> deleteTemplate(@PathVariable Long id) {
        dqRuleTemplateBusiness.deleteTemplate(id);
        return ResponseDTO.ok();
    }
}
