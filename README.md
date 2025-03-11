# Distributed Systems on-hands

## zookeeper-api-introduction
 Connecting to zookeeper, leader election, leader relection strategy, service registry

## autohealer-exercie 
 Set of two java applications. One is responsible for leader election and handling what happens when a node goes rouge/dies. Other application is flaky worker which essentially is brings in the rougness to the workers by killing itself(multiple flaky workers on their own) at a random time. Check the readme in it to get more insight 

## httpServerAndClient
- httpServer
	* basic http server setup, two endpoints, test mode, debug mode.
- http client
	* basic http client functionality

### BasicBookSearchAlgo 
 searching the query string and ranking each book based on the relevance to the searched query strings

### distributed-search-worker-node:
  - LeaderElection, Service Registry, HTTP, Data Serialization, TF-IDF
  - Any instance of this application dynamically in run time can assume the role of the cluster coordinator or the search worker. It performs it's role in the distributed algorithm and communicate with the corresponding nodes in the distributed system using HTTP and data serialization.

## Distributed Document Search frontend, backend application:
 - Take an algorithm like TF-IDF(a simple document search algorithm) and transform it into a distributed system that performs the algorithm in parallel.
 - This Distributed System can operate on a large data set that potentially can exceed teh capacity of a single computer.
 - using Zookeeper, I made the cluster dynamic, fault-tolerant and highly scalable. 
 - This DS components (search cluster, frontend server) are loosely coupled. This loose coupling is a key feature for a distributed system.
 - Using serialization method like Protocol Buffers instead of HTTP, in this application, there is not limit to any particular technology (especially frontend can be of any language that supports protobufs) 
 - User facing frontend server interacts with the coordinator(leader) and then leader then distributes the workload to all available workers.
 
 - ### Run
	* start zookeeper server `./zkServer.sh start`
	* open zookeeper client `./zkCli.sh`
		- create /coordinators_service_registry "For the leader"
		- create /workers_service_registry "For worker nodes"
		- create /election "For leader election and relection"
	* run frontend jar file `java -jar target/front.end-1.0-SNAPSHOT-jar-with-dependencies.jar` and server be active on  localhost:9090
	* run backend jars (as many workers as needed + 1 leader (auto elect)) `java -jar target/distributed.search-1.0-SNAPSHOT-jar-with-dependencies.jar 808x`. Replace x with the port for each node to use.
 
## HA Proxy

- In haproxy/dockerfile set the path of haproxycorrectly if not set properly.
- From the docker-compose.yml directory, do `docker-compose up --build` to start the containers.
- check `localhost`, `localhost:9001`, `localhost:9002`, `localhost:9003`, ``localhost:83`- for stats
- Reload `localhost` to check if load balancing logic is working.	
- To stop the containers, do `ctrl+c` and then `docker compose down`
	
## kafka_cmdline

- Run Kafka broker servers, publishers, consumers
- check README.md in /kafka_cmdline/

# kafka_pc
- check readme in it.

## ProtoBuf file installation and compilation process

 - Install the protoc x86-64 version from https://github.com/protocolbuffers/protobuf/releases/tag/v29.3 to some directory probably it's best to place it somewhere universally.
- After unpacking it, we can see protoc compiler binary in bin directory and we have to compile it to the location where we want our proto file to be at (Beware of the path again. I have changed the directories couple of times).
- `/Users/ashok/Documents/Else/Projects_leer/UdemyDistributedSystemsAndCloudComputing/distributedSystemsAndCloudComputingInJava/protoc-29.3-osx-x86_64/bin/protoc --java_out=./src/main/java/ ./src/main/java/model/proto/search_cluster_protos.proto`
- Also, make sure to update the pom.xml with protobuf-java latest stable version

## Infra as Code deployment in GCP

### Cloud deployment in practice
Manually run an application on a compute enginer instance and instance templates for automating that vm creation for most of the part
- generate a jar file locally.
- Upload this jar file in gcp storage application bucket.
- Either generate an instance template or a new VM instance manually everytime.
- Commands in shell for up and running
	- `which java` initially it's not installed
	- `sudo apt-get update`
	- `sudo apt-get -y --force-yes install openjdk-11-jdk`
	- copy the jar file from storage application bucket to the current VM 
		- `gsutil cp gs://application-bucket/*.jar .`
	- Launch our application listening on a port (ex: 80)
		- `sudo java -jar <jar name> 80 &`
- When creating an instance template, we don't need to specify the region because we can have our compute engines anywhere we want when we create VM button from the Instance template options.

### Instance groups, autoscaling, auto-healing
- Launch an entire cluster distributed across multiple zones
- Autoscaling policies to allow our cluster to grow and shrink depending on the load.
- Autohealing health checks for automatic recovery from failures and keeps our system stable and available to our users at all times.
##### Instance groups and autoscaling
- Manage groups of computer instances that can scale up and down if they become unhealthy or completely automatic.
- Even though we have Instance templates, 
	- creation of each VM was still manual
	- No way to create a number of instances at once
	- Each VM is independent and unmonitored
	- We have to manually restart VMs upon failure.
- To solve above problems, we can create instance groups.
	- We can manage a large cluster of compute instances
	- Monitor all compute instances' health
	- Add more instance during peak traffic
	- Heal the cluster if there are any failures

##### Auto-healing
- In the Instance groups, edit it and there will be an option for health check.
- In side the health check, we need to give an end point like a `/status` endpoint from our application jar which it sends requests periodically to a Check Interval time we set in this Health Check.
	- Need to set Check Interval, timeout, Healthy threshld (consecutive successes), un healthy threshold (no. of consecutive failures)
- Test this
	- Get the shell of one of the instances
	- `ps axu | grep java`
	- `sudo kill -9 <pid>`
	- Now the application on this instance no longer runs.
	- After the healthchecks, autohealer understands that this application is not running and because `/status` can't be reached. So it shuts down this VM instance and starts a new VM (now this would have a new ip address). Check it in monitoring window also. 

