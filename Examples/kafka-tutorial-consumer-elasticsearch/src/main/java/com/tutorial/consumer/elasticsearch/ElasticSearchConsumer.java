package com.tutorial.consumer.elasticsearch;

import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

public class ElasticSearchConsumer {

    public static RestHighLevelClient createClient() {

        //////////////////////////
        /////////// IF YOU USE LOCAL ELASTICSEARCH
        //////////////////////////

        //  String hostname = "localhost";
        //  RestClientBuilder builder = RestClient.builder(new HttpHost(hostname,9200,"http"));


        //////////////////////////
        /////////// IF YOU USE BONSAI / HOSTED ELASTICSEARCH
        //////////////////////////

        //https://d5uucn7bvj:7ycc5fduud@kafaka-2434710820.eu-central-1.bonsaisearch.net:443
        //7ycc5fduud
        //https://ropo8abfno:v5itzysa8r@kafaka-2434710820.eu-central-1.bonsaisearch.net:443
        // replace with your own credentials
        String hostname = "kafaka-2434710820.eu-central-1.bonsaisearch.net:443"; // localhost or bonsai url
        String username = "ropo8abfno"; // needed only for bonsai
        String password = "v5itzysa8r"; // needed only for bonsai

        // credentials provider help supply username and password
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(hostname, 443, "https"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

    public static KafkaConsumer<String, String> createConsumer(String topic) {

        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "kafka-demo-elasticsearch";

        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // disable auto commit of offsets
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100"); // disable auto commit of offsets

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));

        return consumer;

    }

    private static JsonParser jsonParser = new JsonParser();

    private static String extractIdFromTweet(String tweetJson) {
        // gson library
        return jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("id_str")
                .getAsString();
    }

    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class.getName());
        RestHighLevelClient client = createClient();

        KafkaConsumer<String, String> consumer = createConsumer("twitter_tweets");

        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

            Integer recordCount = records.count();
            logger.info("Received " + recordCount + " records");

            BulkRequest bulkRequest = new BulkRequest();

            for (ConsumerRecord<String, String> record : records) {

                // 2 strategies
                // kafka generic ID
                // String id = record.topic() + "_" + record.partition() + "_" + record.offset();

                // twitter feed specific id
                try {
                    String id = Integer.toString(new Random().nextInt());//extractIdFromTweet(record.value());

                    // where we insert data into ElasticSearch

                    /**
                     * Uncomment this code if you are using elastic search version < 7.0
                     IndexRequest indexRequest = new IndexRequest(
                     "twitter",
                     "tweets",
                     id // this is to make our consumer idempotent
                     ).source(record.value(), XContentType.JSON);
                     */

                    /** Added for ElasticSearch >= v7.0
                     * The API to add data into ElasticSearch has slightly changes
                     * and therefore we must use this new method. 
                     * the idea is still the same
                     * read more here: https://www.elastic.co/guide/en/elasticsearch/reference/current/removal-of-types.html
                     * uncomment the code above if you use ElasticSearch 6.x or less
                     */

//                    IndexRequest indexRequest = new IndexRequest("twitter","tweets")
//                       .source(record.value(), XContentType.JSON)
//                       .id(id); // this is to make our consumer idempotent

                    IndexRequest indexRequest = new IndexRequest(
                            "twitter",
                            "tweets",
                            id // this is to make our consumer idempotent
                    ).source(record.value(), XContentType.JSON);

                    //bulkRequest.add(indexRequest); // we add to our bulk request (takes no time)

                    IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                    logger.info(response.getId());

                } catch (NullPointerException e) {
                    logger.warn("skipping bad data: " + record.value());
                }

            }

            if (recordCount > 0) {
                BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                logger.info("Committing offsets...");
                consumer.commitSync();
                logger.info("Offsets have been committed");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // close the client gracefully
        // client.close();

    }
}
