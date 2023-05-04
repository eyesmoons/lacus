package com.lacus.admin.controller.system;

import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.core.annotations.AccessLog;
import com.lacus.core.cache.guava.GuavaCacheService;
import com.lacus.core.cache.map.MapCache;
import com.lacus.dao.system.enums.dictionary.BusinessTypeEnum;
import com.lacus.dao.system.result.DictionaryData;
import com.lacus.domain.system.config.ConfigApplicationService;
import com.lacus.domain.system.config.command.ConfigUpdateCommand;
import com.lacus.domain.system.config.dto.ConfigDTO;
import com.lacus.domain.system.config.query.ConfigQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 参数配置 信息操作处理
 */
@RestController
@RequestMapping("/system/config")
@Validated
public class SysConfigController extends BaseController {

    @Autowired
    private ConfigApplicationService configApplicationService;

    @Autowired
    private GuavaCacheService guavaCacheService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@permission.has('system:config:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(ConfigQuery query) {
        PageDTO page = configApplicationService.getConfigList(query);
        return ResponseDTO.ok(page);
    }

    /**
     * 根据字典类型查询字典数据信息
     * 换成用Enum
     */
    @GetMapping(value = "/dict/{dictType}")
    public ResponseDTO<List<DictionaryData>> dictType(@PathVariable String dictType) {
        List<DictionaryData> dictionaryData = MapCache.dictionaryCache().get(dictType);
        return ResponseDTO.ok(dictionaryData);
    }


    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@permission.has('system:config:query')")
    @GetMapping(value = "/{configId}")
    public ResponseDTO<ConfigDTO> getInfo(@NotNull @Positive @PathVariable Long configId) {
        ConfigDTO config = configApplicationService.getConfigInfo(configId);
        return ResponseDTO.ok(config);
    }


    /**
     * 修改参数配置
     */
    @PreAuthorize("@permission.has('system:config:edit')")
    @AccessLog(title = "参数管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@RequestBody ConfigUpdateCommand config) {
        configApplicationService.updateConfig(config);
        return ResponseDTO.ok();
    }

    /**
     * 刷新参数缓存
     */
    @PreAuthorize("@permission.has('system:config:remove')")
    @AccessLog(title = "参数管理", businessType = BusinessTypeEnum.CLEAN)
    @DeleteMapping("/refreshCache")
    public ResponseDTO<?> refreshCache() {
        guavaCacheService.configCache.invalidateAll();
        return ResponseDTO.ok();
    }
}
