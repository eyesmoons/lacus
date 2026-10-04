package com.lacus.admin.controller.lakeintelligence;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.lakeintelligence.SearchBusiness;
import com.lacus.domain.lakeintelligence.command.SearchRequest;
import com.lacus.domain.lakeintelligence.dto.SearchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 相似检索接口
 *
 * @author shengyu
 * @date 2024/10/26 17:29
 */
@Api(value = "相似检索", tags = {"湖智-检索"})
@RestController
@RequestMapping("/api/lake-intelligence/search")
public class SearchController {

    @Autowired
    private SearchBusiness searchBusiness;

    @ApiOperation("相似检索（支持图片上传）")
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("@permission.has('lakeintelligence:search:query')")
    public ResponseDTO<SearchResponse> search(@RequestPart(value = "image", required = false) MultipartFile image,
                                               @RequestParam(value = "image_id", required = false) String imageId,
                                               @RequestParam(value = "top_k", required = false) Integer topK,
                                               @RequestParam(value = "collection_name", required = false) String collectionName) {
        SearchRequest request = new SearchRequest();
        request.setImageId(imageId);
        request.setTopK(topK);
        request.setCollectionName(collectionName);
        return ResponseDTO.ok(searchBusiness.search(image, request));
    }
}
