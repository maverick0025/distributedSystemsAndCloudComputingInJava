
## Install MongoDB and run
- `brew tap mongodb/brew`
- `brew install mongodb-community@8.0`
- `brew services`
- `brew services start mongodb-community`
- `cat /opt/homebrew/etc/mongodb.conf`
- `ps aux | grep -v grep | grep mongodb` to check if mongodb is running. 
- `mongod --version`
- `mongosh` to connect to the mongodb terminal.
-------------------

## Commands - 1 

- `help` lists all available commands for us to try.
-  `show dbs`
- `use <db_name>` is that db is not there, mongodb will create it. Here, lets use the db_name as `online-school`.
- show collections.
- `db.students.insertOne({"name":"Michael", "age" : 25, favorite_colors:["blue", "yellow"]});` with this first entry, collection `student` will also be created.
- `db.students.find();`
-  `db.students.insertOne({"_id":"5bd" , "name":"Jackie", "age" : 35, favorite_colors:["green"]});` better not to use custom _id and let mongodb choose it for us.
- `db.students.insertMany([{"name":"John", "age" : 41, favorite_colors:["black"]}, {"name":"Mary", "age" : 5, favorite_colors:["white", "blue", "green"]}]);` to insert more entries.
- `db.students.find().pretty()` to view data better.
- `db.students.find({ name: "Michael"});` fetch all students with name Michael.
- `db.students.find({age:{$gt: 30}});` fetch all students with age greater than 30.
- `db.students.find( { $or : [ {favorite_colors:"blue"} , {favorite_colors: "black"}] } );` query inside an array field.
- `db.students.find( { $or : [ {favorite_colors:"blue"} , {favorite_colors: "black"}] } ).limit(1);` show only 1 result.
- `db.students.updateOne({name: "Jackie"}, {$set: {age:32}})`; update a field in an entry in students' collection.
-  `db.students.find({name: 'Jackie});` check if the age has updated.
-  `db.students.find();`
- `db.students.deleteMany({age:{$lt:18}});` delete all students whose age is less than 18.
-  `db.students.find();` check if collection has updated.

## mongodb-java-client application 
### Scaling MongoDB using Data Replication
- A Java client application that cn read and write to our cluster through the MongoDB Java Driver library.
- Reference url: https://www.mongodb.com/docs/manual/core/replica-set-write-concern/


- `mkdir -p /opt/homebrew/var/mongodb/rs0-0`
- `mkdir -p /opt/homebrew/var/mongodb/rs0-1`
- `mkdir -p /opt/homebrew/var/mongodb/rs0-2`
- `mongod --replSet rs0 --port 27017 --bind_ip 127.0.0.1 --dbpath /opt/homebrew/var/mongodb/rs0-0 --oplogSize 128`
- `mongod --replSet rs0 --port 27018 --bind_ip 127.0.0.1 --dbpath /opt/homebrew/var/mongodb/rs0-1 --oplogSize 128`
- `mongod --replSet rs0 --port 27019 --bind_ip 127.0.0.1 --dbpath /opt/homebrew/var/mongodb/rs0-2 --oplogSize 128`
- `mongosh --port 27017`
    ```json
        rs.initiate({
        _id: "rs0",
        members: [
            { _id: 0, host: "127.0.0.1:27017" },
            { _id: 1, host: "127.0.0.1:27018" },
            { _id: 2, host: "127.0.0.1:27019" }
        ] 
        })

    ```

- `mongosh --port 27017` connecting to this port to see if its primary and it is. Also check 27018, 27019

- build a java client application to test this above built mongodb cluster

- `java -jar mongodb-java-client-1.0-SNAPSHOT-jar-with-dependencies.jar physics Michael 25 90.5`
    - output says "Invalid course physics" - this is because we might not have physics course in db.
    - `mongosh --port 27017`
    - `show dbs`
    - `use online-school` creates a new db
    -  `db.createCollection("physics")` creates a new collection in online-school
    - `show collections`

 ![Testing Java Mongodb client](image.png)

### Failure Injection and testing (contd. from above)

- kill the primary replication node ctrc+c the node running on port 27017 (note that this is our primary node)
- To check if the secondaries at 27018, 27019 have done the leader election and chose a leader, do `mongosh --port 27018` and `mongosh --port 27019`
    - node running on 27108 now acts as primary and 27019 acts as secondary
- now, if we add a new student entry, the cluster will still add it since the write concern is the primary node and we do have a primary node at this point.


## Sharded Distributed MongoDB Cluster (insert-many-users, insert-many-movies)

- This cluster would have two shards.

### 
- Need to launch our config server cluster. So, we need three directories, one for each config server replication set member.
    - `mkdir -p /opt/homebrew/var/mongodb/config-srv-0`
    - `mkdir -p /opt/homebrew/var/mongodb/config-srv-1`
    - `mkdir -p /opt/homebrew/var/mongodb/config-srv-2`

- Now, lets launch the config servers mongod instances. Set the replication set to config-rs. Bind to local host. Use Three terminals. This step is completely identical to the above running a regular MongoDB replication set except the role of those mongod instances was set to config server.
    - Run the first config server mongod instance `mongod --configsvr --replSet config-rs --dbpath /opt/homebrew/var/mongodb/config-srv-0 --bind_ip 127.0.0.1 --port 27020`
    - Run the second config server mongod instance `mongod --configsvr --replSet config-rs --dbpath /opt/homebrew/var/mongodb/config-srv-1 --bind_ip 127.0.0.1 --port 27021`
    - Run the third config server mongod instance `mongod --configsvr --replSet config-rs --dbpath /opt/homebrew/var/mongodb/config-srv-2 --bind_ip 127.0.0.1 --port 27022`

- Group the above created config servers together into a single replicated cluster
    - lets connect to one of them `mongosh --port 27020`.
        ```json
        rs.initiate({
        _id: "config-rs",
        configsvr: true,
        members: [
            { _id: 0, host: "127.0.0.1:27020" },
            { _id: 1, host: "127.0.0.1:27021" },
            { _id: 2, host: "127.0.0.1:27022" }
        ] 
        })
        ```
- Launching actual MongoDB shards. we are going to run two standalone shards
    - create directories for shards
        - `mkdir -p /opt/homebrew/var/mongodb/shard-0`
        - `mkdir -p /opt/homebrew/var/mongodb/shard-1`
    - Run above shards. Run the mongod instance but with shard server parameter. Use 2 terminals
        - `mongod --shardsvr --port 27017 --bind_id 127.0.0.1 --dbpath /opt/homebrew/var/mongodb/shard-0 --oplogSize 128`
        - `mongod --shardsvr --port 27018 --bind_id 127.0.0.1 --dbpath /opt/homebrew/var/mongodb/shard-1 --oplogSize 128`
    - Now on a new terminal, we need our mongo s router to put everything together. Ask it to listen on port 27023
        - `mongos --configdb config-rs/127.0.0.1:27020,127.0.0.1:27021,127.0.0.1:27022 --bind_id 127.0.0.1 --port 27023`
    - Now, we have everything put together. Check the cluster architecture diagram below
        -  ![Mongodb whole cluster idea](IMG_0098.jpeg)
    - Now, need to tell mongos about these two shards
        - `mongosh --port 27023`
        - add first shard `sh.addShard("127.0.0.1:27017)`
        - add second shard `sh.addShard("127.0.0.1:27018)`
        - `show dbs`
        - `use config` switch to config database
        -  Now, we'll experiment with the balancing feature. So, change the chunk size from original 128 MB to 1MB. It's not needed in production but here we do it because to see the result my putting in a few documents.
            - `db.settings.save( {_id:"chunksize", value: 1} )`
- Video on Demand online streaming service Use case to practice above sharding.
