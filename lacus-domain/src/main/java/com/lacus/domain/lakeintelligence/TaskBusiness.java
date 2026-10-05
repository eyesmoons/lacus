package com.lacus.domain.lakeintelligence;

import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.lakeintelligence.entity.LakeTaskEntity;
import com.lacus.domain.lakeintelligence.dto.TaskDTO;
import com.lacus.domain.lakeintelligence.query.TaskPageQuery;
import com.lacus.service.lakeintelligence.ILakeTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TaskBusiness {

    @Autowired
    private ILakeTaskService lakeTaskService;

    public PageDTO pageList(TaskPageQuery query) {
        return new PageDTO(lakeTaskService.page(query.toPage(), query.toQueryWrapper()));
    }

    public TaskDTO detail(Long taskId) {
        LakeTaskEntity entity = lakeTaskService.getById(taskId);
        if (entity == null) {
            throw new CustomException("任务[" + taskId + "]不存在");
        }
        return toDTO(entity);
    }

    public Object getProgress(Long taskId) {
        LakeTaskEntity entity = lakeTaskService.getById(taskId);
        if (entity == null) {
            throw new CustomException("任务[" + taskId + "]不存在");
        }
        Map<String, Object> progress = new HashMap<>();
        progress.put("task_id", taskId);
        progress.put("status", entity.getStatus());
        progress.put("progress", entity.getTrainingProgress());
        progress.put("message", entity.getErrorMessage());
        return progress;
    }

    private TaskDTO toDTO(LakeTaskEntity entity) {
        TaskDTO dto = new TaskDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
