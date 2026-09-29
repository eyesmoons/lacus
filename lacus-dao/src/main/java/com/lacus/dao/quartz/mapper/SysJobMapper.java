package com.lacus.dao.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.quartz.entity.SysJob;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 定时任务调度表 Mapper接口
 *
 * @author lacus
 */
@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {

    /**
     * 查询所有的定时任务
     *
     * @return 定时任务列表
     */
    List<SysJob> selectJobAll();

    /**
     * 根据任务组更新任务状态
     *
     * @return 结果
     */
    int updateJobStatusByJobGroup(SysJob job);

    List<SysJob> selectJobList(SysJob job);
}
