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
### distributed-search-worker-node:
 LeaderElection, Service Registry, HTTP, Data Serialization, TF-IDF
                                    - Any instance of this application dynamically in run time can assume the role of the cluster coordinator or the search worker. It performs it's role in the distributed algorithm and communicate with the corresponding nodes in the distributed system using HTTP and data serialization.

## Distributed Document Search frontend, backend application:
 - Take an algorithm like TF-IDF(a simple document search algorithm) and transform it into a distributed system that performs the algorithm in parallel.
 - This Distributed System can operate on a large data set that potentially can exceed teh capacity of a single computer.
 - using Zookeeper, I made the cluster dynamic, fault-tolerant and highly scalable. 
 - This DS components (search cluster, frontend server) are loosely coupled. This loose coupling is a key feature for a distributed system.
 - Using serialization method like Protocol Buffers instead of HTTP, in this application, there is not limit to any particular technology (especially frontend can be of any language that supports protobufs) 
 - User facing frontend server interacts with the coordinator(leader) and then leader then distributes the workload to all available workers.
 
 - ### Run
	* start zookeeper server
	* create /coordinators_service_registry "For the leader"
	* create /workers_service_registry "For worker nodes"
	* create /election "For leader election and relection"
	* run frontend jar file `java -jar target/front.end-1.0-SNAPSHOT-jar-with-dependencies.jar` and server be active on  localhost:9090
	* run backend jars (as many workers as needed + 1 leader (auto elect)) `java -jar target/distributed.search-1.0-SNAPSHOT-jar-with-dependencies.jar 808x`. Replace x with the port for each node to use.
 

## ProtoBuf file installation and compilation process
Install the protoc x86-64 version from https://github.com/protocolbuffers/protobuf/releases/tag/v29.3 to some directory probably it's best to place it somewhere universally
After unpacking it, we can see protoc compiler binary in bin directory and we have to compile it to the location where we want our proto file to be at (Beware of the path again. I have changed the directories couple of times)
` /Users/ashok/Documents/Else/Projects_leer/UdemyDistributedSystemsAndCloudComputing/distributedSystemsAndCloudComputingInJava/protoc-29.3-osx-x86_64/bin/protoc --java_out=./src/main/java/ ./src/main/java/model/proto/search_cluster_protos.proto`

Also, make sure to update the pom.xml with protobuf-java latest stable version
