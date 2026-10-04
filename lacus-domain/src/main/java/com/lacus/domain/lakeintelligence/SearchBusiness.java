package com.lacus.domain.lakeintelligence;

import com.lacus.common.exception.CustomException;
import com.lacus.domain.lakeintelligence.command.SearchRequest;
import com.lacus.domain.lakeintelligence.dto.SearchResponse;
import com.lacus.domain.lakeintelligence.feign.MlServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 相似检索业务逻辑
 */
@Slf4j
@Service
public class SearchBusiness {

    @Autowired
    private MlServiceFeign mlServiceFeign;

    /**
     * 相似检索（以图搜图）
     */
    public SearchResponse search(MultipartFile image, SearchRequest request) {
        Map<String, String> params = new HashMap<>();
        if (request.getImageId() != null && !request.getImageId().isEmpty()) {
            params.put("image_id", request.getImageId());
        }
        if (request.getTopK() != null) {
            params.put("top_k", String.valueOf(request.getTopK()));
        }
        if (request.getCollectionName() != null && !request.getCollectionName().isEmpty()) {
            params.put("collection_name", request.getCollectionName());
        }

        Map<String, Object> response;
        try {
            response = mlServiceFeign.search(image, params);
        } catch (Exception e) {
            throw new CustomException("相似检索失败：" + e.getMessage());
        }

        if (response == null || (response.get("code") != null && Integer.valueOf(-1).equals(response.get("code")))) {
            throw new CustomException("相似检索失败：" + (response != null ? response.get("message") : "无响应"));
        }

        // 解析响应
        SearchResponse searchResponse = new SearchResponse();
        List<SearchResponse.SearchResultItem> items = new ArrayList<>();
        Object resultsObj = response.get("results");
        if (resultsObj instanceof List) {
            for (Object item : (List<?>) resultsObj) {
                if (item instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) item;
                    SearchResponse.SearchResultItem resultItem = new SearchResponse.SearchResultItem();
                    resultItem.setId(map.get("id") != null ? map.get("id").toString() : null);
                    resultItem.setDistance(map.get("distance") instanceof Number ? ((Number) map.get("distance")).doubleValue() : null);
                    items.add(resultItem);
                }
            }
        }
        searchResponse.setResults(items);
        return searchResponse;
    }
}
