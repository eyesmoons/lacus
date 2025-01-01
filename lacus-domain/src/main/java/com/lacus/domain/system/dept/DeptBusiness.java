package com.lacus.domain.system.dept;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.lacus.domain.common.dto.TreeSelectedDTO;
import com.lacus.domain.system.dept.command.AddDeptCommand;
import com.lacus.domain.system.dept.command.UpdateDeptCommand;
import com.lacus.domain.system.dept.dto.DeptDTO;
import com.lacus.domain.system.dept.model.DeptModel;
import com.lacus.domain.system.dept.model.DeptModelFactory;
import com.lacus.domain.system.dept.query.DeptQuery;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.dao.system.entity.SysDeptEntity;
import com.lacus.dao.system.entity.SysRoleEntity;
import com.lacus.service.system.ISysDeptService;
import com.lacus.service.system.ISysRoleService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 部门服务
 */
@Service
public class DeptBusiness {

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysRoleService roleService;


    public List<DeptDTO> getDeptList(DeptQuery query) {
        List<SysDeptEntity> list = deptService.list(query.toQueryWrapper());
        return list.stream().map(DeptDTO::new).collect(Collectors.toList());
    }

    public DeptDTO getDeptInfo(Long id) {
        SysDeptEntity byId = deptService.getById(id);
        return new DeptDTO(byId);
    }

    public List<Tree<Long>> getDeptTree() {
        List<SysDeptEntity> list = deptService.list();

        return TreeUtil.build(list, 0L, (dept, tree) -> {
            tree.setId(dept.getDeptId());
            tree.setParentId(dept.getParentId());
            tree.putExtra("label", dept.getDeptName());
        });
    }

    public TreeSelectedDTO getDeptTreeForRole(Long roleId) {
        List<Long> checkedKeys = new ArrayList<>();
        SysRoleEntity role = roleService.getById(roleId);
        if (role != null && StrUtil.isNotEmpty(role.getDeptIdSet())) {
            checkedKeys = StrUtil.split(role.getDeptIdSet(), ",")
                .stream().map(Long::new).collect(Collectors.toList());
        }

        TreeSelectedDTO selectedDTO = new TreeSelectedDTO();
        selectedDTO.setDepts(getDeptTree());
        selectedDTO.setCheckedKeys(checkedKeys);

        return selectedDTO;
    }


    public void addDept(AddDeptCommand addCommand, LoginUser loginUser) {
        DeptModel deptModel = DeptModelFactory.loadFromAddCommand(addCommand, new DeptModel());

        deptModel.checkDeptNameUnique(deptService);
        deptModel.generateAncestors(deptService);

        deptModel.insert();
    }

    public void updateDept(UpdateDeptCommand updateCommand, LoginUser loginUser) {
        DeptModel deptModel = DeptModelFactory.loadFromDb(updateCommand.getDeptId(), deptService);
        deptModel.loadUpdateCommand(updateCommand);

        deptModel.checkDeptNameUnique(deptService);
        deptModel.checkParentIdConflict();
        deptModel.checkStatusAllowChange(deptService);
        deptModel.generateAncestors(deptService);

        deptModel.updateById();
    }

    public void removeDept(Long deptId) {
        DeptModel deptModel = DeptModelFactory.loadFromDb(deptId, deptService);

        deptModel.checkHasChildDept(deptService);
        deptModel.checkDeptAssignedToUsers(deptService);

        deptService.removeById(deptId);
    }



}
