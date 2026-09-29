package com.lacus.domain.system.resources.dto.visitor;

import com.lacus.dao.system.entity.StorageEntity;
import com.lacus.domain.system.resources.dto.Directory;
import com.lacus.domain.system.resources.dto.FileLeaf;
import com.lacus.domain.system.resources.dto.ResourceComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * resource tree visitor
 */
public class ResourceTreeVisitor implements Visitor {

    /**
     * resource list
     */
    private List<StorageEntity> resourceList;

    public ResourceTreeVisitor() {
    }

    /**
     * constructor
     *
     * @param resourceList resource list
     */
    public ResourceTreeVisitor(List<StorageEntity> resourceList) {
        this.resourceList = resourceList;
    }

    /**
     * visit
     *
     * @return resoruce component
     */
    @Override
    public ResourceComponent visit(String rootPath) {
        ResourceComponent rootDirectory = new Directory();
        for (StorageEntity resource : resourceList) {
            // judge whether is root node
            if (rootNode(resource, rootPath)) {
                // if it is a root node.
                ResourceComponent tempResourceComponent = getResourceComponent(resource);
                rootDirectory.add(tempResourceComponent);
                tempResourceComponent.setChildren(setChildren(tempResourceComponent.getFullName(), resourceList));
            }
        }
        return rootDirectory;
    }

    /**
     * set children
     *
     * @param fullName unique path
     * @param list     resource list
     * @return resource component list
     */
    public static List<ResourceComponent> setChildren(String fullName, List<StorageEntity> list) {
        // id is the unique value,
        List<ResourceComponent> childList = new ArrayList<>();
        for (StorageEntity resource : list) {
            if (Objects.equals(fullName, resource.getPfullName())) {
                ResourceComponent tempResourceComponent = getResourceComponent(resource);
                childList.add(tempResourceComponent);
            }
        }
        for (ResourceComponent resourceComponent : childList) {
            resourceComponent.setChildren(setChildren(resourceComponent.getFullName(), list));
        }
        if (childList.size() == 0) {
            return new ArrayList<>();
        }
        return childList;
    }

    /**
     * Determine whether it is the root node
     *
     * @param resource resource
     * @return true if it is the root node
     */
    public boolean rootNode(StorageEntity resource, String rootPath) {

        boolean isRootNode = true;
        if (!Objects.equals(resource.getPfullName(), rootPath)) {
            for (StorageEntity parent : resourceList) {
                if (Objects.equals(resource.getPfullName(), parent.getFullName())) {
                    isRootNode = false;
                    break;
                }
            }
        }

        return isRootNode;
    }

    /**
     * get resource component by resource
     *
     * @param resource resource
     * @return resource component
     */
    private static ResourceComponent getResourceComponent(StorageEntity resource) {
        ResourceComponent tempResourceComponent;
        if (resource.isDirectory()) {
            tempResourceComponent = new Directory();
        } else {
            tempResourceComponent = new FileLeaf();
        }

        tempResourceComponent.setName(resource.getAlias());
        tempResourceComponent.setFullName(resource.getFullName());
        tempResourceComponent.setId(resource.getId());
        tempResourceComponent.setPid(resource.getPfullName());
        tempResourceComponent.setIdValue(resource.getId(), resource.isDirectory());
        tempResourceComponent.setType(resource.getType());
        return tempResourceComponent;
    }
}
