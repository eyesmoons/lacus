package com.lacus.core.i18n;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.common.exception.error.ErrorCodeInterface;
import com.lacus.utils.i18n.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 检测 未添加到i18n文件(messages.properties)中的message
 */
@Component
@Slf4j
public class MessageI18nCheckerRunner implements ApplicationRunner {

    @Value("lacus.checkI18nKey")
    private String checkI18nKey;

    public static Object[] allErrorCodes = ArrayUtil.addAll(
        ErrorCode.Internal.values(),
        ErrorCode.External.values(),
        ErrorCode.Client.values(),
        ErrorCode.Business.values());

    @Override
    public void run(ApplicationArguments args) {
        if (Convert.toBool(checkI18nKey)) {
            checkEveryMessage();
        }
    }

    /**
     * 如果想支持i18n, 请把对应的错误码描述填到 /resources/i18n/messages.properties 文件中
     */
    public void checkEveryMessage() {
        for (Object errorCode : allErrorCodes) {
            ErrorCodeInterface errorInterface = (ErrorCodeInterface)errorCode;
            try {
                MessageUtils.message(errorInterface.i18nKey());
            } catch (Exception e) {
                log.warn(" in the file /resources/i18n/messages.properties, could not find i18n message for:"
                    + errorInterface.i18nKey());
            }
        }
    }
}
