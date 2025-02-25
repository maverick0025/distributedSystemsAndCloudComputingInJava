

# Kafka Producer and Consumer

## Kafka-Producer
- Building a Kafka Producer using Kafka's Java API
- Configure the producer programatically
- Different ways to send messages (events) to the Kafka topic
    - specifying partition
    - not specifying partition ()
    - not specifying key at all (uses round robin)
- Check the comments in the code for more information
- How to run this application
    - untar the kafka_cmdline tar file in the other directory and do the following from that directory
        - Create 2 more server.properties naming them server-1.properties, server-2.properties in config/
            - Modify brokerid (increment by 1), logs storage location(just append -1, -2 for newly created), listeners(uncomment this and assign ports 9093, 9094 instead of 9092). After this step, we will have configs for 3 kafka broker servers ought to be running on ports 9092, 9093, 9094
        - execute `bin/zookeeper-server-start.sh config/zookeeper.properties` to start the zookeeper.
        - execute `bin/kafka-server-start.sh config/server.properties` to start first server.
        - execute `bin/kafka-server-start.sh config/server-1.properties` to start second server.
        - execute `bin/kafka-server-start.sh config/server-2.properties` to start the third server.
        - Now, create a new topic `event` by executing `bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 2 --partitions 3 --topic events`
        - To describe a topic, `bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic events`
        - If for some reason a topic needs to be deleted, use `bin/kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic <topic name>`
        - Now, run the application kafka-producer directly from intellij or generate a jar file and then run it.


---------
## Kafka-Consumer
- Generate the jar file from the consumer java project
- Intial setup and run
    -   `bin/zookeeper-server-start.sh config/zookeeper.properties`
    -   `bin/kafka-server-start.sh config/server.properties`
    -   `bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic events`
        - if topic is not present, create a new topic `bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 2 --partitions 3 --topic events`
    - Produce messages with producer `bin/kafka-console-producer.sh --broker-list localhost:9092 --topic events`
- Partition Load Balancing within a Consumer group
    - Balancing based on the number of consumers instances within a consumer group. Especially, tthe partitions from which the outgoing messages are there, they will be routed to a single consumer based on partition.  
    - Produce two messages `message 1`, `message 2`
    - Now, run the consumer jar file. Note that, a new consumer group will be registered with Kafka when this jar is run. So, we don't see messages 1, 2 because they are produced before this got registered. But the following messages do. With the current config, when we see a message being read by this consumer, key is null so it can be inferenced that messages produced into Kafka are following round robin order to go into partitions.
    - Now, terminate that consumer and produce a message `message 5`. Right now since there are no consumers, it will not be read but as soon as we run the jar once again, `message 5` will be read again. Note that, here we already registered our consumer group with Kafka.
    - If we have multiple consumers all in a same group, some messages might be read by some and some by others in the same group but never they read the same message. (Kafka rebalances the load)
- Publish/Subscribe with Kafka
    - If by doing `java -jar <consumer jar file> <group_name>`, each message produced will be read by a consumer from each group.
- Consumer Commit failure (when a consumer fails after the record/message is read from Kafka or during the processing of messages)
    - comment the `kafkaConsumer.commitAsync()` in the code
    - Rebuild and package the jar file
    - start a consumer in a group and then from publisher, publish a message. Now, in this flow, the consumer reads the message but because the commitAsync() is absent, the consumer fails to commit to Kafka and so if we launch a new consumer in the same or different group, it will also read this same message and the process continues indefinitely because the problem is with not committing to Kafka indicating that the message is successfully processed.


## Debug issues
- Improper event deletion
    - Tried to delete the kafka topic and then when I do describe, it's not present.
    - But then when I try to recreate the same topic, kafka says the topic is marked for deletion and it is not deleting it
    - Tried to rerun zookeeper, kafka server (stopped using zookeeper-server-stop.sh , kafka-server-stop.sh) but of vain.
    - from zookeeper-shell.sh, following needs to be deleted 
        - ls /config/topics/<topicname>
            - delete /config/topics/<topicname>
        - ls /admin/delete_topics/<topicname>
            - delete /admin/delete_topics/<topicname>
        - ls /brokers/topics/<topicname>/partitions/<list each partion>
            - deleteall /brokers/topics/<topicname>
    - Also, in server.properties, set `delete.topic.enable=true`
    - restart zookeeper and kafka servers and then try to create this topic. It will work 
    - Also make sure when deleting events, your consumers, producers are also offline else, even when you delete an event, there is a high possiblity that the server will just recreate the event.