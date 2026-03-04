package com.lacus.domain.dig.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.entity.StTaskEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StTaskModel extends StTaskEntity {

    public StTaskModel(StJobEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }
}
