package com.lacus.domain.system.post.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.domain.system.post.command.UpdatePostCommand;
import com.lacus.dao.system.entity.SysPostEntity;
import com.lacus.service.system.ISysPostService;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PostModel extends SysPostEntity {

    public PostModel(SysPostEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity, this);
        }
    }

    public void loadFromUpdateCommand(UpdatePostCommand command) {
        if (command != null) {
            BeanUtil.copyProperties(command, this, "postId");
        }
    }


    public void checkCanBeDelete(ISysPostService postService) {
        if (postService.isAssignedToUsers(this.getPostId())) {
            throw new ApiException(ErrorCode.Business.POST_ALREADY_ASSIGNED_TO_USER_CAN_NOT_BE_DELETED);
        }
    }

    public void checkPostNameUnique(ISysPostService postService) {
        if (postService.isPostNameDuplicated(getPostId(), getPostName())) {
            throw new ApiException(ErrorCode.Business.POST_NAME_IS_NOT_UNIQUE, getPostName());
        }
    }

    public void checkPostCodeUnique(ISysPostService postService) {
        if (postService.isPostCodeDuplicated(getPostId(), getPostCode())) {
            throw new ApiException(ErrorCode.Business.POST_CODE_IS_NOT_UNIQUE, getPostCode());
        }
    }

}
