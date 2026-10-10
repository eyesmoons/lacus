package com.lacus.admin.controller.lakeintelligence;

import com.lacus.common.exception.CustomException;
import com.lacus.domain.lakeintelligence.DatasetBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * 数据集图片访问接口
 *
 * <p>路径格式：/lake-intelligence/image/{datasetId}/{数据集内相对路径}</p>
 */
@Api(value = "数据集图片", tags = {"湖智-图片"})
@RestController
@RequestMapping("/lake-intelligence/image")
public class ImageController {

    private static final String PREFIX = "/lake-intelligence/image/";

    @Autowired
    private DatasetBusiness datasetBusiness;

    @ApiOperation("获取数据集图片")
    @GetMapping("/**")
    public ResponseEntity<Resource> getImage(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int idx = uri.indexOf(PREFIX);
        String rest = uri.substring(idx + PREFIX.length());
        int slash = rest.indexOf('/');
        if (slash <= 0) {
            throw new CustomException("图片路径格式错误");
        }
        Long datasetId;
        try {
            datasetId = Long.parseLong(rest.substring(0, slash));
        } catch (NumberFormatException e) {
            throw new CustomException("图片路径格式错误");
        }
        String relativePath;
        try {
            // getRequestURI() 返回 URL 编码路径，需解码以支持中文等文件名
            relativePath = java.net.URLDecoder.decode(rest.substring(slash + 1), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new CustomException("图片路径解码失败");
        }
        File file = datasetBusiness.resolveImage(datasetId, relativePath);
        return ResponseEntity.ok()
                .contentType(mediaTypeOf(file.getName()))
                .body(new FileSystemResource(file));
    }

    private MediaType mediaTypeOf(String name) {
        String lower = name.toLowerCase();
        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        if (lower.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        if (lower.endsWith(".bmp")) {
            return MediaType.parseMediaType("image/bmp");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
