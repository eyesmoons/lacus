package com.lacus.common.core.base;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import java.beans.PropertyEditorSupport;
import java.util.Date;

import com.lacus.common.constant.Constants;
import com.lacus.common.exception.CustomException;
import com.lacus.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Slf4j
public class BaseController {

    /**
     *
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtil.parseDate(text));
            }
        });
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StrUtil.format("redirect:{}", url);
    }


    public void checkPageParams(int pageNo, int pageSize) throws CustomException {
        if (pageNo <= 0) {
            throw new CustomException(Status.REQUEST_PARAMS_NOT_VALID_ERROR.getMsg(), pageNo);
        }
        if (pageSize <= 0) {
            throw new CustomException(Status.REQUEST_PARAMS_NOT_VALID_ERROR.getMsg(), pageSize);
        }
    }
}
