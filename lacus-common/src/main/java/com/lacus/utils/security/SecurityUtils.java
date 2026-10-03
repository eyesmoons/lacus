package com.lacus.utils.security;

import com.lacus.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 安全防护工具类（zip bomb 检测、路径穿越检测）
 */
@Slf4j
public class SecurityUtils {

    /**
     * 安全解压 zip 文件
     *
     * @param zipPath   zip 文件路径
     * @param dest      解压目标目录
     * @param maxFiles  最大文件数
     * @param maxSize   解压后总大小上限（字节）
     * @throws IOException 当解压失败或检测到恶意文件时抛出
     */
    public static void safeExtractZip(String zipPath, String dest, int maxFiles, long maxSize) throws IOException {
        File destDir = new File(dest);
        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new IOException("无法创建目标目录: " + dest);
        }

        Path destCanonicalPath = destDir.getCanonicalFile().toPath();

        try (ZipFile zipFile = new ZipFile(zipPath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            int fileCount = 0;
            long totalSize = 0;

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                fileCount++;

                // 检查文件数限制
                if (fileCount > maxFiles) {
                    throw new CustomException("zip 文件内文件数量超出限制，最大允许 " + maxFiles + " 个文件（疑似 zip bomb）");
                }

                // 路径穿越检测
                File extractedFile = new File(destDir, entry.getName());
                String extractedCanonical = extractedFile.getCanonicalPath();
                if (!extractedCanonical.startsWith(destCanonicalPath + File.separator)
                        && !extractedCanonical.equals(destCanonicalPath.toString())) {
                    throw new CustomException("检测到路径穿越攻击，非法条目: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    continue;
                }

                // 解压单个文件，并实时计数实际写入的字节数
                // 注意：entry.getSize() 可能返回 -1（UNKNOWN），不可用于安全校验
                Path extractedPath = extractedFile.toPath();
                Path parent = extractedPath.getParent();
                if (parent != null && !parent.toFile().exists()) {
                    parent.toFile().mkdirs();
                }

                long entryBytes = 0;
                try (InputStream is = zipFile.getInputStream(entry);
                     java.io.OutputStream os = java.nio.file.Files.newOutputStream(extractedPath,
                             java.nio.file.StandardOpenOption.CREATE,
                             java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
                             java.nio.file.StandardOpenOption.WRITE)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        os.write(buffer, 0, len);
                        entryBytes += len;
                        totalSize += len;
                        if (totalSize > maxSize) {
                            throw new CustomException("zip 解压后总大小超出限制，最大允许 " + maxSize + " 字节（疑似 zip bomb）");
                        }
                    }
                }

                log.debug("解压文件: {}, 当前总文件数: {}, 当前总大小: {}", entry.getName(), fileCount, totalSize);
            }

            log.info("zip 安全解压完成, 文件数: {}, 总大小: {}", fileCount, totalSize);
        }
    }

    /**
     * 检查文件名是否包含路径穿越字符
     *
     * @param fileName 待检查的文件名
     * @return true 表示安全，false 表示存在风险
     */
    public static boolean isSafeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        return !fileName.contains("..") && !fileName.contains("/") && !fileName.contains("\\");
    }

    /**
     * 验证解压后的文件路径是否在目标目录内
     *
     * @param destDir     目标目录
     * @param extractedPath 解压后的文件路径
     * @return true 表示安全
     * @throws IOException 当无法获取规范路径时
     */
    public static boolean isWithinDestination(String destDir, String extractedPath) throws IOException {
        File dest = new File(destDir);
        File extracted = new File(extractedPath);
        String destCanonical = dest.getCanonicalPath();
        String extractedCanonical = extracted.getCanonicalPath();
        return extractedCanonical.startsWith(destCanonical + File.separator)
                || extractedCanonical.equals(destCanonical);
    }
}
