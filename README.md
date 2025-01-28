# distributedSystemsAndCloudComputingInJava
Speaks about distributed systems. Zookeeper etc...

## 1. Basics of Zookeeper (Cluster Coordination Service and Distributed Algos)

- Start zookeeper server
	`./zkServer.sh start`

- Start zookeeper client
	`./zkCli.sh`

`help`

`ls /` path root znode

`create /parent "some parent data"` 

`create /parent/child "some child data"`

`ls /parent`

`get /parent`

`rmr /parent` removes both parent and all childs it had 


