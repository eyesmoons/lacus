package com.lacus.domain.oneapi.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OneApiInfoModel extends OneApiInfoEntity {
    public OneApiInfoModel(OneApiInfoEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }
}
