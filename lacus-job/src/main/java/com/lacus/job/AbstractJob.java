package com.lacus.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.job.flink.KafkaSourceConfig;
import com.lacus.job.model.SourceConf;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Properties;

@Slf4j
public abstract class AbstractJob implements IJob {

    private static final long serialVersionUID = -8435891490675922619L;


    // 任务名称
    protected String jobName;

    // 任务配置
    protected String jobConf;

    protected AbstractJob(String[] args) {
        if (ObjectUtils.isNotEmpty(args)) {
            jobName = args[0];
            jobConf = args[1];
            //job_param = JSONObject.parseObject(args[1]);
        }
    }

    @Override
    public void init() {
        log.info("接收到参数，任务名称：{}, 任务参数：{}", jobName, jobConf);
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
            handle();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }



    //默认从最新时间开始消费
    protected KafkaSource<ConsumerRecord<String, String>> buildKafkaSource(String bootstrapServers, String groupId, List<String> topics, Properties conf) {
        return KafkaSourceConfig.builder()
                .bootstrapServers(bootstrapServers)
                .groupId(groupId)
                .topics(topics)
                .conf(conf)
                .offsetsInitializer(null)
                .valueSerialize(null)
                .build();
    }

    /**
     * 发送数据到kafka
     *
     * @param bootStrapServers bootStrapServers
     * @param topic            topic
     */
    protected FlinkKafkaProducer<String> kafkaSink(String bootStrapServers, String topic) {
        log.info("开发往kafka，topic：{}", topic);
        // 定义kafka producer
        return new FlinkKafkaProducer<>(topic, new KafkaSerializationSchema<String>() {
            @Override
            public ProducerRecord<byte[], byte[]> serialize(String data, Long aLong) {
                return new ProducerRecord<>(topic, data.getBytes());
            }
        }, buildKafkaProducerProps(bootStrapServers), FlinkKafkaProducer.Semantic.EXACTLY_ONCE);
    }

    /**
     * 构建kafka生产者参数
     */
    private Properties buildKafkaProducerProps(String bootStrapServers) {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.setProperty(ProducerConfig.RETRIES_CONFIG, "1");
        props.setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, "300000");
        props.setProperty(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "60485760");
        return props;
    }
}