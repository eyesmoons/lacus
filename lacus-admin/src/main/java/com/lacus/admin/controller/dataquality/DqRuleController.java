package com.lacus.admin.controller.dataquality;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.dataquality.entity.DqRuleVO;
import com.lacus.domain.dataquality.DqRuleBusiness;
import com.lacus.domain.dataquality.command.AddDqRuleCommand;
import com.lacus.domain.dataquality.command.UpdateDqRuleCommand;
import com.lacus.domain.dataquality.query.DqRuleQuery;
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

import javax.validation.Valid;

@Api(value = "数据质量规则管理", tags = {"数据质量规则管理"})
@RestController
@RequestMapping("/dq/rule")
public class DqRuleController {

    @Autowired
    private DqRuleBusiness dqRuleBusiness;

    @ApiOperation("查询规则列表")
    @GetMapping("/list")
    public ResponseDTO<?> listRules(DqRuleQuery query) {
        PageDTO page = dqRuleBusiness.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("新增规则")
    @PostMapping
    public ResponseDTO<DqRuleVO> addRule(@RequestBody @Valid AddDqRuleCommand command) {
        return ResponseDTO.ok(dqRuleBusiness.addRule(command));
    }

    @ApiOperation("修改规则")
    @PutMapping
    public ResponseDTO<Void> updateRule(@RequestBody @Valid UpdateDqRuleCommand command) {
        dqRuleBusiness.updateRule(command);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除规则")
    @DeleteMapping("/{id}")
    public ResponseDTO<Void> deleteRule(@PathVariable Long id) {
        dqRuleBusiness.deleteRule(id);
        return ResponseDTO.ok();
    }

    @ApiOperation("规则详情")
    @GetMapping("/{id}")
    public ResponseDTO<DqRuleVO> detail(@PathVariable Long id) {
        return ResponseDTO.ok(dqRuleBusiness.detail(id));
    }
}
