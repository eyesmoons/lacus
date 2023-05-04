package com.lacus.domain.metadata.column;

import com.lacus.dao.metadata.entity.MetaColumnEntity;
import com.lacus.service.metadata.IMetaColumnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class ColumnService {

    @Autowired
    private IMetaColumnService metaColumnService;

    public List<MetaColumnEntity> getColumnsBytTableId(Long tableId) {
        return metaColumnService.getColumnsBytTableId(tableId);
    }
}
