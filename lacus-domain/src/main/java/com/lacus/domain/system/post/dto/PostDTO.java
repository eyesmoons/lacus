package com.lacus.domain.system.post.dto;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.annotation.ExcelColumn;
import com.lacus.dao.system.entity.SysPostEntity;
import com.lacus.enums.dictionary.StatusEnum;
import com.lacus.enums.interfaces.BasicEnumUtil;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDTO {

    public PostDTO(SysPostEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity, this);
            statusStr = BasicEnumUtil.getDescriptionByValue(StatusEnum.class, entity.getStatus());
        }
    }

    @ExcelColumn(name = "岗位ID")
    private Long postId;


    @ExcelColumn(name = "岗位编码")
    private String postCode;

    @ExcelColumn(name = "岗位名称")
    private String postName;


    @ExcelColumn(name = "岗位排序")
    private String postSort;

    @ExcelColumn(name = "备注")
    private String remark;

    private String status;

    @ExcelColumn(name = "状态")
    private String statusStr;

    private Date createTime;

}
