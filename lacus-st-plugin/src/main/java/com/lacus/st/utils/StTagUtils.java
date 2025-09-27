package com.lacus.st.utils;

import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ST组件标签工具类
 * 用于处理组件的标签排序和解析
 * 
 * @author lacus
 */
public class StTagUtils {
    
    /**
     * 标签信息DTO
     */
    @Data
    public static class TagInfo {
        private String name;
        private String displayName;
        private int order;
        private String description;
        
        public TagInfo(String name, String displayName, int order, String description) {
            this.name = name;
            this.displayName = displayName;
            this.order = order;
            this.description = description;
        }
    }
    
    /**
     * 从组件类中解析标签信息并按order排序
     * 
     * @param componentClass 组件类
     * @return 排序后的标签信息列表
     */
    public static List<TagInfo> parseAndSortTags(Class<?> componentClass) {
        StTag stTag = componentClass.getAnnotation(StTag.class);
        if (stTag == null) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(stTag.value())
                .map(tagDef -> new TagInfo(
                        tagDef.name(),
                        tagDef.displayName().isEmpty() ? tagDef.name() : tagDef.displayName(),
                        tagDef.order(),
                        tagDef.description()
                ))
                .sorted(Comparator.comparingInt(TagInfo::getOrder))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取指定标签的排序值
     * 
     * @param componentClass 组件类
     * @param tagName 标签名称
     * @return 标签的排序值，如果不存在返回Integer.MAX_VALUE
     */
    public static int getTagOrder(Class<?> componentClass, String tagName) {
        return parseAndSortTags(componentClass).stream()
                .filter(tag -> tag.getName().equals(tagName))
                .mapToInt(TagInfo::getOrder)
                .findFirst()
                .orElse(Integer.MAX_VALUE);
    }
    
    /**
     * 检查组件是否定义了指定标签
     * 
     * @param componentClass 组件类
     * @param tagName 标签名称
     * @return 是否包含该标签
     */
    public static boolean hasTag(Class<?> componentClass, String tagName) {
        return parseAndSortTags(componentClass).stream()
                .anyMatch(tag -> tag.getName().equals(tagName));
    }
}