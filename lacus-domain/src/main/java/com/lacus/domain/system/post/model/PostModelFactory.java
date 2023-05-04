package com.lacus.domain.system.post.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode.Business;
import com.lacus.domain.system.post.command.AddPostCommand;
import com.lacus.dao.system.entity.SysPostEntity;
import com.lacus.service.system.ISysPostService;

public class PostModelFactory {

    public static PostModel loadFromDb(Long postId, ISysPostService postService) {
        SysPostEntity byId = postService.getById(postId);
        if (byId == null) {
            throw new ApiException(Business.OBJECT_NOT_FOUND, postId, "职位");
        }
        return new PostModel(byId);
    }

    public static PostModel loadFromAddCommand(AddPostCommand addCommand, PostModel model) {
        if (addCommand != null && model != null) {
            BeanUtil.copyProperties(addCommand, model);
        }
        return model;
    }


}
