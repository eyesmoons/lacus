package com.lacus.service.system;

import com.lacus.dao.system.entity.SysPostEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 岗位信息表 服务类
 */
public interface ISysPostService extends IService<SysPostEntity> {

    /**
     * 校验岗位名称
     * @return 结果
     */
    boolean isPostNameDuplicated(Long postId, String postName);

    /**
     * 校验岗位编码
     * @return 结果
     */
    boolean isPostCodeDuplicated(Long postId, String postCode);


    /**
     * 检测职位是否分配给用户
     * @param postId 职位id
     * @return
     */
    boolean isAssignedToUsers(Long postId);

}
