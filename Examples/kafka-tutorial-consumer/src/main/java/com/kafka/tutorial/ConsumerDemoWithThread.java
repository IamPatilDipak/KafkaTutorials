package com.kafka.tutorial;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoWithThread {
    public static void main(String[] args) {
        Logger LOGGER = LoggerFactory.getLogger(ConsumerDemoWithThread.class);
        String bootStrapServer = "127.0.0.1:9092";
        String groupId = "my-second1-app";
        String topic = "first_topic";

        //Latch for dealing multiple thread
        CountDownLatch latch = new CountDownLatch(1);

        //Consumer Runnable
        Runnable consumerThread = new ConsumerThread(latch, bootStrapServer, groupId, topic);
        Thread myThread = new Thread(consumerThread);
        myThread.start();

        //Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Caught shutdown hook");
            ((ConsumerThread) consumerThread).shutdown();

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOGGER.info("Application has exited!");
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.error("Application got interrupted", e);
        } finally {
            LOGGER.info("Application is closing");
        }
    }

    public static class ConsumerThread implements Runnable {

        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;

        private String bootStrapServer;
        private String groupId;
        private String topic;
        private Logger LOGGER = LoggerFactory.getLogger(ConsumerThread.class);

        public ConsumerThread(CountDownLatch latch,
                              String bootStrapServer,
                              String groupId,
                              String topic) {
            this.latch = latch;
            this.bootStrapServer = bootStrapServer;
            this.groupId = groupId;
            this.topic = topic;

            //Create Consumer Config
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

            //Create Consumer
            consumer = new KafkaConsumer<>(properties);

            //Subscribe Consumer to Topic
            consumer.subscribe(Collections.singleton(topic));
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                    for (ConsumerRecord<String, String> record : records) {
                        LOGGER.info("Key : " + record.key());
                        LOGGER.info("Topic : " + record.topic());
                        LOGGER.info("Partition : " + record.partition());
                    }
                }
            } catch (WakeupException ex) {
                LOGGER.info("Received shutdown signal!");
            } finally {
                consumer.close();
            }
        }

        public void shutdown() {
            //the wakeup() method is special method to interrupt consumer.poll()
            //it will throw the exception WakeupException
            consumer.wakeup();
        }
    }
}
