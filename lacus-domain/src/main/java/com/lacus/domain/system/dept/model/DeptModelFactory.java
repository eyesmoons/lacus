package com.lacus.domain.system.dept.model;

import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.domain.system.dept.command.AddDeptCommand;
import com.lacus.dao.system.entity.SysDeptEntity;
import com.lacus.service.system.ISysDeptService;

/**
 * 部门模型工厂
 */
public class DeptModelFactory {

    public static DeptModel loadFromDb(Long deptId, ISysDeptService deptService) {
        SysDeptEntity byId = deptService.getById(deptId);
        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, deptId, "部门");
        }
        return new DeptModel(byId);
    }

    public static DeptModel loadFromAddCommand(AddDeptCommand addCommand, DeptModel model) {
        model.setParentId(addCommand.getParentId());
        model.setAncestors(addCommand.getAncestors());
        model.setDeptName(addCommand.getDeptName());
        model.setOrderNum(addCommand.getOrderNum());
        model.setLeaderName(addCommand.getLeaderName());
        model.setPhone(addCommand.getPhone());
        model.setEmail(addCommand.getEmail());
        return model;
    }



}
