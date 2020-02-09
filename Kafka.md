# Apache Kafka - Use Cases 

- Messaging System
- Activity Tracking
- Gather metrics from many different locations
- Application logs gathering
- Stream Processing (with Kafka Streams API or Spark)
- Dec-Coupling of system dependencies
- Integration with Spark, Flink, Hadoop, and many other Big Data technologies

**For Example** -

* **Netflix** uses kafka to apply recommendations in real time while you are watching TV shows.

* **Uber** uses kafka to gather user, taxi and trip data in real time to compute and forecast demand and compute surge pricing in real time.

* **LinkedIn** uses kafka to prevent spam, collect user interactions to make better connection recommendations in real time. 




# Command Line Interface (CLI)

#### Start Zookeeper
* zookeeper-server-start.bat config\zookeeper.properties*

#### Start Kafka
* kafka-server-start.bat config\server.properties*

#### Create Topic
* kafka-topics --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1*

#### List Out Topics
* kafka-topics --zookeeper localhost:2181 --list

#### Topic Details
* kafka-topics --zookeeper localhost:2181 --topic first_topic --describe

#### Delete Topic
* kafka-topics --zookeeper 127.0.0.1:2181 --topic first_topic --delete

#### Producers
* kafka-console-producer --broker-list localhost:9092 --topic first_topic
* kafka-console-producer --broker-list localhost:9092 --topic first_topic --producer-property acks=all

#### Consumers
* kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic
* kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --from-beginning

#### Groups
* kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group my-first-application


# Brokers

![GitHub Logo](/images/1.Brokers.png)