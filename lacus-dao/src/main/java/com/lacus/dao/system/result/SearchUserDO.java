package com.lacus.dao.system.result;

import com.lacus.dao.system.entity.SysUserEntity;
import lombok.Data;

/**
 * 如果Entity的字段和复杂查询不匹配时   自定义类来接收
 */
@Data
public class SearchUserDO extends SysUserEntity {

    private String deptName;
    private String deptLeader;

}
