

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