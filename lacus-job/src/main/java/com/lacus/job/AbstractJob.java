package com.lacus.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.job.flink.KafkaSourceConfig;
import com.lacus.job.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;
import java.util.Properties;

@Slf4j
public abstract class AbstractJob implements IJob {

    private static final long serialVersionUID = -8435891490675922619L;

    protected KafkaSource<ConsumerRecord<String, String>> kafkaSource;
    protected List<String> topics;
    protected String bootStrapServer;
    protected String groupId;
    protected OffsetsInitializer offsetsInitializer;
    protected String autoOffset = "earliest";
    protected String kafkaTimeStamp;
    protected String dataFormat;
    protected String sinkType;
    protected String engine;
    protected JSONObject baseConf;


    protected Properties conf = new Properties();


    protected static transient JSONObject param_json = new JSONObject();

    protected AbstractJob(String[] args) {
        if (ObjectUtils.isNotEmpty(args)) {
            param_json = JSONObject.parseObject(args[0]);
        }
    }

    @Override
    public void init() {
        this.baseConf = getParamValue("baseConf", null);
        JSONObject source = JSONObject.parseObject(JSON.toJSONString(getParamValue("source", null)));
        this.bootStrapServer = source.getString("bootstrap.server");
        this.topics = JSONArray.parseArray(source.getString("topics"), String.class);
        this.groupId = source.getString("group.id");
        String offsetReset = source.getString("auto.offset.reset");
        this.kafkaTimeStamp = source.getString("kafka.timestamp");
        if (StringUtils.checkValNotNull(offsetReset)) {
            this.autoOffset = offsetReset;
        }
        JSONObject sink = JSONObject.parseObject(JSON.toJSONString(getParamValue("sink", null)));
        this.dataFormat = sink.getString("dataFormat");
        this.sinkType = sink.getString("sink.type");
        this.engine = sink.getString("engine");
        log.info("接收到参数:" + param_json);
    }

    @Override
    public void close() {
        log.info("job finished");
    }

    @Override
    public void run() {
        try {
            init();
            afterInit();
            buildKafkaSource();
            handle();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getParamValue(String name, T defaultValue) {
        return paramContainKey(name) ? (T) param_json.get(name) : defaultValue;
    }

    private boolean paramContainKey(String name) {
        return param_json.containsKey(name);
    }


    private void buildKafkaSource() {
        this.kafkaSource = KafkaSourceConfig.builder()
                .bootstrapServer(bootStrapServer)
                .groupId(groupId)
                .topics(topics)
                .conf(conf)
                .offsetsInitializer(offsetsInitializer)
                .valueSerialize(null)
                .build();
    }
}