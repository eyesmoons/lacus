package com.lacus.domain.system.resources.dto.visitor;

import com.lacus.domain.system.resources.dto.ResourceComponent;

/**
 * Visitor
 */
public interface Visitor {

    /**
     * visit
     * @return resource component
     */
    ResourceComponent visit(String rootPath);
}
