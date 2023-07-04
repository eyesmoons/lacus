package com.lacus.job.constants;


public class Constant {


    //-----------------------CDC Flag----------------- //
    public static final String BEFORE = "before";

    public static final String AFTER = "after";

    public static final String OP = "op";

    public static final String SOURCE = "source";

    public static final String DB = "db";

    public static final String TABLE = "tableName";

    //----------------------LOAD DATA COMMON FILED ------------------------//
    public static final String IS_DELETE_FILED = "_is_delete";

    public static final String UPDATE_STAMP_FILED = "update_stamp";


    //----------------------DELETE FLAG ------------------------//

    public static final int DELETE_FALSE = 0;

    public static final int DELETE_TRUE = 1;


    //------------------------sink conf -------------------------//


    public static final String SINK_SOURCE = "source";

    public static final String SINK_SOURCE_BOOTSTRAP_SERVERS = "bootstrapServers";

    public static final String SINK_SOURCE_GROUP_ID = "groupId";

    public static final String SINK_SOURCE_TOPICS = "topics";


    public static final String SINK_FLINK = "flinkConf";

    public static final String SINK_FLINK_MAX_BATCH_INTERVAL = "maxBatchInterval";

    public static final String SINK_FLINK_MAX_BATCH_ROWS = "maxBatchRows";

    public static final String SINK_FLINK_MAX_BATCH_SIZE = "maxBatchSize";


    public static final String SINK_ENGINE = "sink";

    public static final String SINK_ENGINE_TYPE = "sinkType";

    public static final String SINK_ENGINE_CONF = "engine";

    public static final String SINK_DORIS_FORMAT = "format";

    public static final String SINK_DORIS_COLUMNS = "columns";

    public static final String SINK_DORIS_JSON_PATHS = "jsonpaths";

    public static final String MAX_FILTER_RATIO = "max_filter_ratio";

    public static final String STRIP_OUTER_ARRAY = "strip_outer_array";

}
