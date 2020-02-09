package com.kafka.tutorial;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {
    public static void main(String[] args) {

        String bootStrapServer = "127.0.0.1:9092";

        //Create Producer Properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        //Create producer record
        ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", "Hello World ");

        //Send data - asynchronously
        producer.send(record);
        producer.flush();
        producer.close();
    }
}
