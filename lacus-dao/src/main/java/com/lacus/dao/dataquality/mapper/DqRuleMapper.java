package com.lacus.dao.dataquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.dao.dataquality.entity.DqRuleEntity;
import com.lacus.dao.dataquality.entity.DqRuleVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface DqRuleMapper extends BaseMapper<DqRuleEntity> {

    /**
     * 分页查询规则，关联 dq_rule_template 和 meta_datasource 补充展示字段
     */
    @Select("<script>" +
            "SELECT r.*, t.template_name, t.template_code, t.template_icon, t.template_color, d.datasource_name " +
            "FROM dq_rule r " +
            "LEFT JOIN dq_rule_template t ON r.template_id = t.id " +
            "LEFT JOIN meta_datasource d ON r.datasource_id = d.datasource_id " +
            "WHERE r.deleted = 0 " +
            "<if test='ruleName != null and ruleName != \"\"'> AND r.rule_name LIKE CONCAT('%',#{ruleName},'%') </if>" +
            "<if test='templateId != null'> AND r.template_id = #{templateId} </if>" +
            "<if test='enabled != null'> AND r.enabled = #{enabled} </if>" +
            "ORDER BY r.create_time DESC" +
            "</script>")
    IPage<DqRuleVO> selectPageWithJoin(
            Page<DqRuleVO> page,
            @Param("ruleName") String ruleName,
            @Param("templateId") Long templateId,
            @Param("enabled") Integer enabled);

    /**
     * 根据 ID 查询规则，关联展示字段
     */
    @Select("SELECT r.*, t.template_name, t.template_code, t.template_icon, t.template_color, d.datasource_name " +
            "FROM dq_rule r " +
            "LEFT JOIN dq_rule_template t ON r.template_id = t.id " +
            "LEFT JOIN meta_datasource d ON r.datasource_id = d.datasource_id " +
            "WHERE r.deleted = 0 AND r.id = #{id}")
    DqRuleVO selectVOById(@Param("id") Long id);
}
