# distributedSystemsAndCloudComputingInJava
Speaks about distributed systems. Zookeeper etc...

## 1. Basics of Zookeeper (Cluster Coordination Service and Distributed Algos)


`./zkServer.sh start` starts zookeeper server
`./zkServer.sh stop` stops the server

`./zkCli.sh` starts zookeeper client

`help`

`ls /` path root znode

`create /parent "some parent data"` 

`create /parent/child "some child data"`

`ls /parent`

`get /parent`

`rmr /parent` removes both parent and all childs it had 

`java -jar target/leader.election-1.0-SNAPSHOT-jar-with-dependencies.jar`
