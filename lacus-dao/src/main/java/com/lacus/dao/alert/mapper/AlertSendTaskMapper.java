package com.lacus.dao.alert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lacus.dao.alert.entity.AlertSendTaskEntity;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AlertSendTaskMapper extends BaseMapper<AlertSendTaskEntity> {

    List<AlertSendTaskEntity> selectReadyTasksForLock(@Param("limit") int limit);
}
