package com.lacus.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecryptUtils {

    private static final byte[] SECRET_KEY = "asUa0763hab88&2y".getBytes();

    private static final SymmetricAlgorithm ALGORITHM = SymmetricAlgorithm.AES;

    public static String decryptPwd(String password) {
        try {
            Assert.notEmpty(password, "密码不能为空");
            SymmetricCrypto aes = new SymmetricCrypto(ALGORITHM, SECRET_KEY);
            byte[] decrypt = aes.decrypt(Base64.decode(password));
            return new String(decrypt);
        } catch (Exception e) {
            log.info("解密失败：", e);
            return password;
        }
    }
}
