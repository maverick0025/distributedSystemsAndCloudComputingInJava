
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

## Scaling MongoDB using Data Replication

- Reference url: https://www.mongodb.com/docs/manual/core/replica-set-write-concern/
- 