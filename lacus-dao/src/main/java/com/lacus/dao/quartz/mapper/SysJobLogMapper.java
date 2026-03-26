package com.lacus.dao.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.quartz.entity.SysJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务调度日志表 Mapper接口
 *
 * @author lacus
 */
@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    /**
     * 查询所有的任务日志
     *
     * @return 任务日志列表
     */
    List<SysJobLog> selectJobLogAll();

    /**
     * 清空任务日志
     *
     * @return 结果
     */
    int cleanJobLog();
}
