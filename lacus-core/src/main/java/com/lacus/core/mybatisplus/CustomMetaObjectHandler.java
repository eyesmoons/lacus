package com.lacus.core.mybatisplus;

import com.lacus.core.security.AuthenticationUtils;
import com.lacus.core.web.domain.login.LoginUser;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * Mybatis Plus允许在插入或者更新的时候
 * 自定义设定值
 */
@Component
@Slf4j
public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", new Date(), metaObject);

        this.strictInsertFill(metaObject, "creatorId", this::getUserIdSafely, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        String newUpdaterId = getUserIdSafely();

        this.strictUpdateFill(metaObject, "updaterId", String.class, newUpdaterId);
    }

    public String getUserIdSafely() {
        String userId = null;
        try {
            LoginUser loginUser = AuthenticationUtils.getLoginUser();
            // 将用户 ID 转换为字符串
            userId = String.valueOf(loginUser.getUserId());
        } catch (Exception e) {
            log.info("can not find user in current thread, using system user id (0).");
            // 当无法获取当前用户时（如定时任务执行），使用系统用户 ID "0"
            userId = "0";
        }
        return userId;
    }



}
