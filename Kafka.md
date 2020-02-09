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


# Core Concepts of Kafka

![GitHub Logo](/images/1.Brokers.png)

![GitHub Logo](/images/2.Brokers-Topics.png)

![GitHub Logo](/images/3.Broker-Partitions-Replications.png)

![GitHub Logo](/images/4.Topics-Partitions-Offsets.png)

![GitHub Logo](/images/5.Producers.png)

![GitHub Logo](/images/6.Producers-Acknowledgment.png)

![GitHub Logo](/images/7.Producers-MessageKeys.png)

![GitHub Logo](/images/8.Consumers.png)

![GitHub Logo](/images/9.Consumers-Delivery-Semantics.png)

![GitHub Logo](/images/10.Consumers-Groups.png)

![GitHub Logo](/images/11.Consumers-Offsets.png)

![GitHub Logo](/images/12.Kafka-Concepts.png)






