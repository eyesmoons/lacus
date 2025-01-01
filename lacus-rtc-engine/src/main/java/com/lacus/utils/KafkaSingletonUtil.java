package com.lacus.utils;

import com.lacus.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
public class KafkaSingletonUtil {

    private  KafkaProducer<String, String> producer;

    private KafkaSingletonUtil() {}

    private static class LazyHolder {
        private static final KafkaSingletonUtil INSTANCE = new KafkaSingletonUtil();
    }

    public static KafkaSingletonUtil getInstance(){
        return LazyHolder.INSTANCE;
    }

    public void init(Properties properties){
        if (producer == null) {
            producer = new KafkaProducer<>((properties));
        }
    }

    /**
     * 同步发送消息到kafka
     */
    public  void sendMessageSync(String topicName, String jsonMessage) {
        try{
            Future<RecordMetadata> send = producer.send(new ProducerRecord<>(topicName, jsonMessage));
            RecordMetadata recordMetadata = send.get();
            log.info("同步发送Kafka成功：topicName:{},分区:{},偏移量:{}",topicName,recordMetadata.partition(),recordMetadata.offset());
        }catch (Exception ex){
            log.error("同步发送Kafka失败：topicName:{},data:{}",topicName,jsonMessage,ex);
            throw  new CustomException("发送Kafka失败");
        }
    }


    /**
     * 异步发送消息到kafka
     */
    public  void sendMessageAsync(String topicName, String jsonMessage) {
        try{
            producer.send(new ProducerRecord<>(topicName, jsonMessage), (recordMetadata, e) -> {
                if(e!=null){
                    log.error("异步发送Kafka失败：topicName:{},data:{}",topicName,jsonMessage,e);
                }else{
                    log.info("异步发送Kafka成功：topicName:{},分区:{},偏移量:{}",topicName,recordMetadata.partition(),recordMetadata.offset());
                }
            });
        }catch (Exception ex){
            log.error("异步发送Kafka错误：topicName:{},data:{}",topicName,jsonMessage,ex);
        }
    }

    public void close(){
        if(producer != null){
            producer.close();
        }
    }

    public static void main(String[] args) throws  Exception {
        String s = "demo";
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");;
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaUtil.sendMessageSync("demo",s,properties);
    }
}
