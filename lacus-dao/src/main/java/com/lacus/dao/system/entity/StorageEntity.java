package com.lacus.dao.system.entity;

import com.lacus.enums.ResourceType;
import lombok.Data;

import java.util.Date;

@Data
public class StorageEntity {

    /**
     * exist only if it is stored in t_ds_relation_resources_task.
     */
    private Long id;
    /**
     * fullname is in a format of basepath + tenantCode + res/udf + filename
     */
    private String fullName;
    /**
     * filename is in a format of possible parent folders + alias
     */
    private String fileName;
    /**
     * the name of the file
     */
    private String alias;
    /**
     * parent folder time
     */
    private String pfullName;
    private boolean isDirectory;
    private int userId;
    private String userName;
    private ResourceType type;
    private long size;
    private Date createTime;
    private Date updateTime;
}
