# distributedSystemsAndCloudComputingInJava
Speaks about distributed systems. Zookeeper etc...



### zookeeper-api-introduction
 Connecting to zookeeper, leader election, leader relection strategy, service registry
### autohealer-exercie 
 Set of two java applications. One is responsible for leader election and handling what happens when a node goes rouge/dies. Other application is flaky worker which essentially is brings in the rougness to the workers by killing itself(multiple flaky workers on their own) at a random time. Check the readme in it to get more insight 
### httpserver, httpclient 
 http server and client functionality
### BasicBookSearchAlgo 
 searching the query string and ranking each book based on the relevance to the searched query strings




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
