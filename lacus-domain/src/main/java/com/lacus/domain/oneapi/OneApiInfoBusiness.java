package com.lacus.domain.oneapi;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.constant.Constants;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.oneapi.command.ApiAddCommand;
import com.lacus.domain.oneapi.command.ApiUpdateCommand;
import com.lacus.domain.oneapi.dto.ApiConfigDTO;
import com.lacus.domain.oneapi.dto.ApiInfoDTO;
import com.lacus.domain.oneapi.dto.ApiParamsDTO;
import com.lacus.domain.oneapi.dto.ApiParseDTO;
import com.lacus.domain.oneapi.dto.ApiTestResp;
import com.lacus.domain.oneapi.dto.RequestParamsDTO;
import com.lacus.domain.oneapi.dto.ReturnParamsDTO;
import com.lacus.domain.oneapi.feign.OneApiFeignClient;
import com.lacus.domain.oneapi.model.OneApiInfoModel;
import com.lacus.domain.oneapi.model.OneApiInfoModelFactory;
import com.lacus.domain.oneapi.parse.MySQLParseProcessor;
import com.lacus.domain.oneapi.query.OneApiInfoQuery;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.service.oneapi.IOneApiInfoService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class OneApiInfoBusiness {

    @Autowired
    private IOneApiInfoService oneApiInfoService;

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    @Autowired
    private OneApiFeignClient oneApiFeignClient;

    private static final Pattern SQL_COMMENT_REGEX = Pattern.compile("(/\\*+?[\\w\\W]+?\\*/)");

    private static final Integer RESPONSE_MAX_SIZE = 5;

    public PageDTO pageList(OneApiInfoQuery query) {
        Page<OneApiInfoEntity> page = oneApiInfoService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public OneApiInfoModel addApi(ApiAddCommand addCommand) {
        checkApi(addCommand, true);
        OneApiInfoModel model = OneApiInfoModelFactory.loadFromAddCommand(addCommand, new OneApiInfoModel());
        extraResponse(addCommand);
        boolean insert = model.insert();
        if (insert) {
            oneApiFeignClient.flushCache(model.getApiId(), model.getStatus());
        }
        return model;
    }

    public void updateApi(ApiUpdateCommand updateCommand) {
        checkApi(updateCommand, false);
        OneApiInfoModel model = OneApiInfoModelFactory.loadFromDb(updateCommand.getApiId(), oneApiInfoService);
        extraResponse(updateCommand);
        boolean update = model.updateById();
        if (update) {
            oneApiFeignClient.flushCache(model.getApiId(), model.getStatus());
        }
    }

    public void deleteApi(BulkOperationCommand<Long> command) {
        boolean delete = oneApiInfoService.removeBatchByIds(command.getIds());
        if (delete) {
            for (Long id : command.getIds()) {
                oneApiFeignClient.flushCache(id, 0);
            }
        }
    }

    public OneApiInfoEntity getApiInfo(Long apiId) {
        OneApiInfoEntity byId = oneApiInfoService.getById(apiId);
        if (Objects.isNull(byId)) {
            throw new CustomException(String.format("数据[%s]不存在", apiId));
        }
        MetaDatasourceEntity metaDatasource = metaDataSourceService.getById(byId.getDatasourceId());
        if (Objects.isNull(metaDatasource)) {
            throw new CustomException(String.format("数据源[%s]不存在", byId.getDatasourceId()));
        }
        byId.setDatasourceName(metaDatasource.getDatasourceName());
        return byId;
    }

    public void checkApi(ApiAddCommand addCommand, boolean add) {
        String apiUrl = addCommand.getApiUrl();
        //校验接口地址是否存在
        if (add) {
            OneApiInfoEntity oneApiInfo = oneApiInfoService.queryApiByUrl(apiUrl);
            if (Objects.nonNull(oneApiInfo)) {
                throw new CustomException(String.format("接口[%s]地址已存在", apiUrl));
            }
        }
        if (addCommand.getLimitCount() > 1000) {
            throw new CustomException(String.format("接口[%s]最大返回条数超过限制[%s]", apiUrl, addCommand.getLimitCount()));
        }
        //校验sql脚本
        String apiConfig = addCommand.getApiConfig();
        ApiConfigDTO apiConfigDTO = JSONObject.parseObject(apiConfig, ApiConfigDTO.class);
        this.checkSqlScript(apiConfigDTO);
    }

    private void checkSqlScript(ApiConfigDTO apiConfig) {
        String sql = apiConfig.getSql().trim().toUpperCase();
        if (sql.startsWith("/*")) {
            sql = this.hasSqlComment(sql);
        }
        if (!sql.startsWith("SELECT")) {
            throw new CustomException("只支持SELECT语句！");
        }
    }

    private String hasSqlComment(String sqlScript) {
        Matcher matcher = SQL_COMMENT_REGEX.matcher(sqlScript);
        if (matcher.find()) {
            String offCommentSql = matcher.group(0);
            sqlScript = sqlScript.replace(offCommentSql, "");
        }
        return sqlScript;
    }

    private void extraResponse(ApiAddCommand addCommand) {
        String apiResponse = addCommand.getApiResponse();
        JSONObject jsonObject = JSONObject.parseObject(apiResponse);
        JSONArray list = jsonObject.getJSONArray("list");
        if (list.size() > RESPONSE_MAX_SIZE) {
            List<Object> subList = list.subList(0, RESPONSE_MAX_SIZE);
            jsonObject.put("list", subList);
        }
        addCommand.setApiResponse(JSON.toJSONString(jsonObject, JSONWriter.Feature.PrettyFormat));
    }

    public ApiParamsDTO parse(ApiParseDTO parseDTO) {
        String apiUrl = parseDTO.getApiUrl();
        String sqlScript = parseDTO.getSqlScript();
        OneApiInfoEntity oldApiInfo = oneApiInfoService.queryApiByUrl(apiUrl);
        ApiConfigDTO oldApiConfig = null;
        List<RequestParamsDTO> requestParams = Lists.newArrayList();
        if (Objects.nonNull(oldApiInfo)) {
            oldApiConfig = JSONObject.parseObject(oldApiInfo.getApiConfig(), ApiConfigDTO.class);
        }
        try {
            ApiParamsDTO apiParamsDTO = new ApiParamsDTO();
            List<ReturnParamsDTO> returnParams = Lists.newArrayList();
            MySQLParseProcessor mySQLDynamicParseAdapter = new MySQLParseProcessor();
            Map<String, Set<String>> resultMap = mySQLDynamicParseAdapter.doParse(sqlScript);
            Set<String> reqSet = resultMap.get("req");
            if (CollectionUtil.isNotEmpty(reqSet)) {
                Map<String, RequestParamsDTO> oldReqMap = null;
                if (Objects.nonNull(oldApiConfig)) {
                    List<RequestParamsDTO> oldRequestParams = oldApiConfig.getApiParams().getRequestParams();
                    oldReqMap = oldRequestParams.stream().collect(Collectors.toMap(RequestParamsDTO::getColumnName, req -> req));
                    if (Objects.equals(1, oldApiConfig.getPageFlag())) {
                        requestParams.add(oldReqMap.get(Constants.PAGE_NUM));
                        requestParams.add(oldReqMap.get(Constants.PAGE_SIZE));
                    }
                }
                for (String req : reqSet) {
                    if (CollectionUtil.isNotEmpty(oldReqMap) && oldReqMap.containsKey(req)) {
                        requestParams.add(oldReqMap.get(req));
                        continue;
                    }
                    RequestParamsDTO requestParamsVO = new RequestParamsDTO();
                    requestParamsVO.setColumnName(req);
                    requestParamsVO.setIsMust(0);
                    requestParams.add(requestParamsVO);
                }
            }

            apiParamsDTO.setRequestParams(requestParams);
            Set<String> returnSet = resultMap.get("return");
            if (CollectionUtil.isNotEmpty(returnSet)) {
                Map<String, ReturnParamsDTO> oldReturnMap = null;
                if (Objects.nonNull(oldApiConfig)) {
                    List<ReturnParamsDTO> oldRequestParams = oldApiConfig.getApiParams().getReturnParams();
                    oldReturnMap = oldRequestParams.stream().collect(Collectors.toMap(ReturnParamsDTO::getColumnName, req -> req));
                }
                for (String ret : returnSet) {
                    if (CollectionUtil.isNotEmpty(oldReturnMap) && oldReturnMap.containsKey(ret)) {
                        returnParams.add(oldReturnMap.get(ret));
                        continue;
                    }
                    ReturnParamsDTO returnParamsVO = new ReturnParamsDTO();
                    returnParamsVO.setColumnName(ret);
                    returnParams.add(returnParamsVO);
                }
            }
            apiParamsDTO.setReturnParams(returnParams);
            return apiParamsDTO;
        } catch (Exception e) {
            throw new CustomException("动态SQL解析异常，请检查SQL语句", e);
        }
    }

    public ResponseDTO<ApiTestResp> testApi(ApiInfoDTO apiDTO) {
        ResponseDTO<ApiTestResp> result = oneApiFeignClient.testApi(apiDTO);
        if (result.getData().getCode() != 200) {
            return result;
        }
        Object data = result.getData();
        ApiTestResp response = JSONObject.parseObject(JSONObject.toJSONString(data), ApiTestResp.class);
        String formatResult = JSON.toJSONString(response.getData(), JSONWriter.Feature.PrettyFormat);
        response.setData(formatResult);
        result.setData(response);
        return result;
    }

    public Boolean updateStatus(Long id, Integer status) {
        OneApiInfoEntity entity = new OneApiInfoEntity();
        entity.setApiId(id);
        entity.setStatus(status);
        boolean update = oneApiInfoService.updateById(entity);
        if (update) {
            oneApiFeignClient.flushCache(id, status);
        }
        return update;
    }

    public ResponseDTO<ApiTestResp> onlineTest(ApiInfoDTO apiInfoDTO) {
        String apiUrl = apiInfoDTO.getApiUrl();
        OneApiInfoEntity apiInfo = oneApiInfoService.queryApiByUrl(apiUrl);

        ApiConfigDTO apiConfig = JSONObject.parseObject(apiInfoDTO.getApiConfig(), ApiConfigDTO.class);
        List<RequestParamsDTO> requestParams = apiConfig.getApiParams().getRequestParams();
        ApiConfigDTO initApiConfig = JSONObject.parseObject(apiInfo.getApiConfig(), ApiConfigDTO.class);

        ApiParamsDTO apiParams = initApiConfig.getApiParams();
        apiParams.setRequestParams(requestParams);
        initApiConfig.setApiParams(apiParams);
        apiInfo.setApiConfig(JSONObject.toJSONString(initApiConfig));
        BeanUtils.copyProperties(apiInfo, apiInfoDTO);
        return this.testApi(apiInfoDTO);
    }
}
