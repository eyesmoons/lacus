package com.lacus.st.example;

import com.lacus.st.utils.StTagUtils;
import com.lacus.st.sink.Db2Sink;
import com.lacus.st.source.MysqlSource;
import com.lacus.st.transform.SqlTransform;

import java.util.List;

/**
 * ST标签系统使用示例
 * 展示如何使用新的标签排序系统
 * 
 * @author lacus
 */
public class StTagExample {
    
    public static void main(String[] args) {
        // 演示Sink组件的标签排序
        System.out.println("=== DB2 Sink 标签信息 ===");
        List<StTagUtils.TagInfo> db2Tags = StTagUtils.parseAndSortTags(Db2Sink.class);
        db2Tags.forEach(tag -> 
            System.out.printf("标签: %s, 显示名: %s, 排序: %d, 描述: %s%n", 
                tag.getName(), tag.getDisplayName(), tag.getOrder(), tag.getDescription())
        );
        
        // 演示Source组件的标签排序
        System.out.println("\n=== MySQL Source 标签信息 ===");
        List<StTagUtils.TagInfo> mysqlTags = StTagUtils.parseAndSortTags(MysqlSource.class);
        mysqlTags.forEach(tag -> 
            System.out.printf("标签: %s, 显示名: %s, 排序: %d, 描述: %s%n", 
                tag.getName(), tag.getDisplayName(), tag.getOrder(), tag.getDescription())
        );
        
        // 演示Transform组件的标签排序
        System.out.println("\n=== SQL Transform 标签信息 ===");
        List<StTagUtils.TagInfo> sqlTags = StTagUtils.parseAndSortTags(SqlTransform.class);
        sqlTags.forEach(tag -> 
            System.out.printf("标签: %s, 显示名: %s, 排序: %d, 描述: %s%n", 
                tag.getName(), tag.getDisplayName(), tag.getOrder(), tag.getDescription())
        );
        
        // 演示工具方法
        System.out.println("\n=== 工具方法演示 ===");
        System.out.println("DB2Sink 连接配置标签排序: " + StTagUtils.getTagOrder(Db2Sink.class, "连接配置"));
        System.out.println("DB2Sink 是否包含事务配置标签: " + StTagUtils.hasTag(Db2Sink.class, "事务配置"));
        System.out.println("MysqlSource 是否包含分区配置标签: " + StTagUtils.hasTag(MysqlSource.class, "分区配置"));
    }
}