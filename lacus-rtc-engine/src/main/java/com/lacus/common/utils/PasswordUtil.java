package com.lacus.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @created by shengyu on 2023/9/21 17:42
 */
@Slf4j
public class PasswordUtil {
    public static String decryptPwd(String password) {
        try {
            if (ObjectUtils.isEmpty(password)) {
                log.warn("密码不能为空");
            }
            SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, "asUa0763hab88&2y".getBytes());
            byte[] decrypt = aes.decrypt(Base64.decode(password));
            return new String(decrypt);
        } catch (Exception e) {
            log.info("decrypt error!", e);
            return password;
        }
    }
}