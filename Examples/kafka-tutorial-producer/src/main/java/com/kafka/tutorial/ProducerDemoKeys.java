package com.kafka.tutorial;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemoKeys {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Logger LOGGER = LoggerFactory.getLogger(ProducerDemoKeys.class);

        String bootStrapServer = "127.0.0.1:9092";

        //Create Producer Properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 10; i++) {
            String topic = "first_topic";
            String value = "Hello World" + i;
            String key = "id_" + i;

            //Create producer record
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

            //Send data - asynchronously
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //Execute every time a record is successfully sent or an exception is thrown

                    if (e == null) {
                        LOGGER.info("\n Received new metadata. \n" +
                                "Topic : " + recordMetadata.topic() + "\n" +
                                "Partition : " + recordMetadata.partition() + "\n" +
                                "Offset : " + recordMetadata.offset() + "\n" +
                                "Timestamp : " + recordMetadata.timestamp());
                    } else {
                        LOGGER.error("Error while producing", e);
                    }
                }
            }).get();
        }
        producer.flush();
        producer.close();
    }
}
