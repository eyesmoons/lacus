package com.lacus.domain.metadata.schema.model;

import com.lacus.domain.metadata.schema.dto.SchemaDbDTO;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class SchemaDbTreeNode {
    private List<SchemaDbDTO> schemaDbList;
    private Set<String> expandedKeys;
    private Set<String> checkedKeys;
}
