package com.lacus.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DatasourceTypeEnumTest {

    @Test
    public void shouldOfferBigDataTypeValues() {
        assertEquals(Integer.valueOf(4), DatasourceTypeEnum.DISTRIBUTED_FILE.getValue());
        assertEquals(Integer.valueOf(5), DatasourceTypeEnum.MESSAGE_QUEUE.getValue());
        assertEquals(Integer.valueOf(6), DatasourceTypeEnum.DATA_WAREHOUSE.getValue());
    }

    @Test
    public void shouldMapByDisplayName() {
        assertEquals("分布式文件系统", DatasourceTypeEnum.DISTRIBUTED_FILE.getDescription());
        assertEquals("消息队列", DatasourceTypeEnum.MESSAGE_QUEUE.getDescription());
        assertEquals("数据仓库", DatasourceTypeEnum.DATA_WAREHOUSE.getDescription());
    }

    @Test
    public void shouldLookupByTypeName() {
        assertEquals(DatasourceTypeEnum.MESSAGE_QUEUE, DatasourceTypeEnum.getByType("message_queue"));
        assertEquals(DatasourceTypeEnum.DATA_WAREHOUSE, DatasourceTypeEnum.getByType("data_warehouse"));
        assertNull(DatasourceTypeEnum.getByType("UNKNOWN"));
    }
}