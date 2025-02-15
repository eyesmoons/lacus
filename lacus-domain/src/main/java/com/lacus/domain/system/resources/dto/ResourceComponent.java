package com.lacus.domain.system.resources.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lacus.enums.ResourceType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * resource component
 */
@Data
@NoArgsConstructor
@JsonPropertyOrder({"id", "pid", "name", "fullName", "description", "isDirctory", "children", "type"})
public abstract class ResourceComponent {

    public ResourceComponent(Long id, String pid, String name, String fullName, boolean isDirctory) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.fullName = fullName;
        this.isDirctory = isDirctory;
        int directoryFlag = isDirctory ? 1 : 0;
        this.idValue = String.format("%s_%s", id, directoryFlag);
    }

    /**
     * id
     */
    protected Long id;
    /**
     * parent id
     */
    protected String pid;
    /**
     * name
     */
    protected String name;
    /**
     * current directory
     */
    protected String currentDir;
    /**
     * full name
     */
    protected String fullName;
    /**
     * description
     */
    protected String description;
    /**
     * is directory
     */
    protected boolean isDirctory;
    /**
     * id value
     */
    protected String idValue;
    /**
     * resoruce type
     */
    protected ResourceType type;
    /**
     * children
     */
    protected List<ResourceComponent> children = new ArrayList<>();

    /**
     * add resource component
     *
     * @param resourceComponent resource component
     */
    public void add(ResourceComponent resourceComponent) {
        children.add(resourceComponent);
    }

    public void setIdValue(Long id, boolean isDirctory) {
        int directoryFlag = isDirctory ? 1 : 0;
        this.idValue = String.format("%s_%s", id, directoryFlag);
    }
}
