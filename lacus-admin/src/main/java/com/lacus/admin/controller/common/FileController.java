package com.lacus.admin.controller.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.lacus.admin.response.UploadDTO;
import com.lacus.common.constant.Constants.UploadSubDir;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.common.exception.error.ErrorCode.Business;
import com.lacus.utils.ServletHolderUtil;
import com.lacus.utils.file.FileUploadUtils;
import com.lacus.utils.jackson.JacksonUtil;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用请求处理
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {


    /**
     * 通用下载请求
     * download接口  其实不是很有必要
     * @param fileName 文件名称
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> fileDownload(String fileName, HttpServletResponse response) {
        try {
            if (!FileUploadUtils.isAllowDownload(fileName)) {
                // 返回类型是ResponseEntity 不能捕获异常， 需要手动将错误填到 ResponseEntity
                ResponseDTO<Object> fail = ResponseDTO.fail(
                    new ApiException(Business.FILE_NOT_ALLOWED_TO_DOWNLOAD, fileName));
                return new ResponseEntity<>(JacksonUtil.to(fail).getBytes(), null, HttpStatus.OK);
            }

            String filePath = FileUploadUtils.getFileAbsolutePath(UploadSubDir.DOWNLOAD_PATH, fileName);

            HttpHeaders downloadHeader = FileUploadUtils.getDownloadHeader(fileName);

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            return new ResponseEntity<>(FileUtil.readBytes(filePath), downloadHeader, HttpStatus.OK);
        } catch (Exception e) {
            log.error("下载文件失败", e);
            return null;
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public ResponseDTO<UploadDTO> uploadFile(MultipartFile file) {
        if (file == null) {
            throw new ApiException(ErrorCode.Business.UPLOAD_FILE_IS_EMPTY);
        }

        // 上传并返回新文件名称
        String fileName = FileUploadUtils.upload(UploadSubDir.UPLOAD_PATH, file);

        String url = ServletHolderUtil.getContextUrl() + fileName;

        UploadDTO uploadDTO = UploadDTO.builder()
            // 全路径
            .url(url)
            // 相对路径
            .fileName(fileName)
            // 新生成的文件名
            .newFileName(FileNameUtil.getName(fileName))
            // 原始的文件名
            .originalFilename(file.getOriginalFilename()).build();

        return ResponseDTO.ok(uploadDTO);
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public ResponseDTO<List<UploadDTO>> uploadFiles(List<MultipartFile> files) {
        if (CollUtil.isEmpty(files)) {
            throw new ApiException(ErrorCode.Business.UPLOAD_FILE_IS_EMPTY);
        }

        List<UploadDTO> uploads = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(UploadSubDir.UPLOAD_PATH, file);
                String url = ServletHolderUtil.getContextUrl() + fileName;
                UploadDTO uploadDTO = UploadDTO.builder()
                    .url(url)
                    .fileName(fileName)
                    .newFileName(FileNameUtil.getName(fileName))
                    .originalFilename(file.getOriginalFilename()).build();

                uploads.add(uploadDTO);

            }
        }
        return ResponseDTO.ok(uploads);
    }

}
