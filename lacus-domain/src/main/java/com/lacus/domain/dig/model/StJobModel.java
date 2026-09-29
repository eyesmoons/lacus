package com.lacus.domain.dig.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.service.dig.IStJobService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StJobModel extends StJobEntity {

    public StJobModel(StJobEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }

    public void checkJobNameUnique(IStJobService service) {
        if (service.isJobNameDuplicated(getJobId(), getJobName())) {
            throw new RuntimeException("任务名称已存在");
        }
    }
}
