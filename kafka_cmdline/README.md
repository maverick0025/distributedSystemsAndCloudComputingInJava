
# All about Kafa server, it's producers, brokers, partitions, replications, consumer


## Kafka Cluster Architecture
###### First case is, one cmd line publisher, one cmd line consumer, one Zookeeper cluster, one Kafka Cluster. We shall have only one bootstrap server for this kafka accessible to both publisher and consumer.

- Download Kafka from https://kafka.apache.org/quickstart
- bin dir - all scripts to run
- config dir - all config files and its dependencies' configs
- lib dir - all kafka dependencies
----------
### Run Zookeeper
- `bin/zookeeper-server-start.sh config/zookeeper.properties` to run zookeeper
- config/server.properties -> configuration file for a single kafka server in this config. We can find many default properties for Kafka broker
    - `broker.id` has to be unique for every kafka broker
    -  This server listens on port 9092 by default
    - `logs.dir` is where kafka persists all it's data
    -  `log.retention.hours` how long should kafka retain the messages it got from publishers irrespective if consumers consumed that message.
------------
### Running, Configuring and Testing a Kafka cluster
- `bin/kafka-server-start.sh config/server.properties` to start the kafka broker using kafka server script pointing it to server.properties config file. This broker now connects and registers with the Zookeeper from above. Now, we have a fully functional single broker Kafka cluster. 
- Before publishing any messages, we need to create a topic. Open a separate terminal window and do the following.
    - `bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic chat`
    - Currently, we have only one server and so we can't have rep factor more than 1 logically.
    - All the messages will be in one ordered queue because we set number of partitions to be 1.
    - topic parameter to name the current topic `chat`.
- In the same window, use this script to see the topic we created just now `bin/kafka-topics.sh --list --bootstrap-server localhost:9092`
- Now, from a new window, publish some messages to the `chat` topic using `bin/kafka-console-producer.sh --broker-list localhost:9092 --topic chat` to test our cluster. We will point it to a subset of our Kafka servers but right now, we have only one server at this point. 
    - Now, publish two messages (just 2 texts)
    - Righnow, we don't have any consumers
- Start a consumer by using Kafka console consumer script pointing it to our cluster telling it to consume, pointing it to the cluster telling it to consume messages from the `chat` topic with a `from-beginning` argument to fetch all the messages that published and have been retented. `bin/kafka-console-consumer.sh --botstrap-server localhost:9092 --topic chat --from-beginning`
    - Now, since the consumer is up and running, publish a 3rd messages and see it consumed instantly.
    - Now, if this consumer is closed and restarted, it will read all the messages from beginning in the same order because we have only one broker and only one partition (single queue).
----------
## Run a multi broker Kafka cluster
- Configure and launch a few more Kafka servers. These are completely transparent to teh publisher and consumer.
- Now, we need to configure server.properties file and do a few changes
    - Duplicate server.properties and rename the new file to `server-1.properties`.
        - increment the broker id by 1
        - another port to listen on (9093). line 34 should be uncommented and make it like `listeners=PLAINTEXT://:9093`. This is because we are running our entire cluster on a single machine. But in production phase of SDLC, we don't need to do it because different brokers would run on separate computers.
        - point this server to different location so this broker server can persist all its data `logs.dirs=/tmp/kafka-logs-1`. This change is also due to the fact that we are running our entire cluster on this single computer.
    - make an another copy `server-2.properties`
        - increment broker id by 1. so, now it will be 2
        - `listeners=PLAINTEXT://:9094`
        - `logs.dirs=/tmp/kafka-logs-2`
- Run the broker servers in separate terminal splits
    - if this hasn't been run previously then run it, `bin/kafka-server-start.sh config/server.properties`and create a topic `chat` also in a new terminal. `bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic chat` 
    - `bin/kafka-server-start.sh config/server-1.properties`
    - `bin/kafka-server-start.sh config/server-2.properties`
- Now, on a new terminal, create a new topic with replication factor 3, 3 partitions and call it `purchases` 
    - `bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic purchases`
    - Although we have 3 Kafka servers, we only need to specify a subset of them as bootstrap servers and those bootstrap servers will propagate the topic creation through zookeeper to the rest of the cluster.
    - Check if the topic is successfully created. `bin/kafka-topics.sh --list --bootstrap-server localhost:9092`
        - we must see topics `chat` and `purchases`
    - Describing a topic `purchases`
        - `bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic purchases`
        - It details a more insightful information of how this new topic is managed by Kafka.
            - For example, it should show, Partition 0 is replicated by broker ids 0,1,2 and broker id 0 is the leader for partition 0. ISR means in-sync replicas (all our kafka broker ids which means, all the replicas are up to date with the partition leader and are healthy as far as Zookeeper is concerned)
    - Describe `chat` topic
        - `bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic chat`
- Launch a producer and publish few messages to `purchases` topic
    - `bin/kafka-console-producer.sh --broker-list localhost:9092 --topic purchases`
        - purchase 1
        - purchase 2
        - purchase 3
        - purchase 4
- Lauch one consumer script and to read messages from `purchases` topic from the beginning of time.
    - `bin/kafka-consoole-consumer.sh --bootstrap-server localhost:9092 --topic purchases --from-beginning`
    - this time, compared to the single partition case, messages are read in a different order from they are published. This likely because, the publisher used different keys for all the messages and are sent to respective `purchases` topic partitions. Partition is an ordered queue but there is no global order between the partitions.

---------
## Test Kafka fault tolerance (failover and replication)
- Check how well our system works if something bad were to happen to any of the broker servers
- From the three servers above, abruptly shutdown one of them `ctrl+c` (lets assume we shut broker id 1)
    - In the 2nd term window where we publish and consume messsages, we can see that our publisher lost connection with that broker id 1. Despite this loss of connection, our cluster should be fully functional.
    - Describe `purchases` topic
        - `bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic purchases`
        - We can see partition 2's whose leader must have been broker 1 is now leaded by broker id 2. Note that, a single broker can be a leader for multiple partitions as well. Take a look at ISRs. With ISRs, it can be observed that broker id 1 can't be reached by Zookeeper.
        - So now, all the partitions are covered by the two remaining Kafka servers
- Publish a few more messages and see if they are consumed.
- Now, if we close all publishers and consumers and then again start consumers with read from beginning, all the messages irresptive of the loss of a server 1 can be read. This is because, that the partition that is leaded by server 1 is replicated to other server's. This is possible only because of the replication factor. No data has been lost.





