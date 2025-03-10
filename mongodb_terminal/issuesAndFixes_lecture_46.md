Almost all of these versions might be because of the latest mongodb version compatibility issue

## Issue 1

#### Problem: 

Unable to create shard using the following commands as mentioned in the video
-`mongod --shardsvr --port 27017 --bind_ip 127.0.0.1 --dbpath /opt/homebrew/var/mongodb/shard-0 --oplogSize 128`
-`mongod --shardsvr --port 27018 --bind_ip 127.0.0.1 --dbpath /opt/homebrew/var/mongodb/shard-1 --oplogSize 128` 

#### Error : 
- BadValue: Cannot start a shardsvr as a standalone server. Please use the option --replSet to start the node as a replica set.

#### Fix: 
- `mongod --shardsvr --replSet shard-rs --port 27017 --bind_ip 127.0.0.1 --dbpath /opt/homebrew/var/mongodb/shard-0/ --oplogSize 128`
- `mongod --shardsvr --replSet shard-rs --port 27018 --bind_ip 127.0.0.1 --dbpath /opt/homebrew/var/mongodb/shard-1/ --oplogSize 128`
- connect to `mongosh --port 27017` and intiate replication set
    ```json
    rs.initiate(
        {
        _id: "shard-rs",
        members: [
            { _id: 0, host: "127.0.0.1:27017" },
            { _id: 1, host: "127.0.0.1:27018" }
            ] 
        }
    )
    ```

----------------

## Issue 2
#### Problem: 
Unable to add shards. Continuation to Issue 1
-`sh.addShard("127.0.0.1:27017")`
-`sh.addShard("127.0.0.1:27018")`

#### Error: 
- MongoServerError[OperationFailed]: host is part of set shard-rs; use replica set url format <setname>/<server1>,<server2>, ...

#### Fix: 
`sh.addShard("shard-rs/127.0.0.1:27017,127.0.0.1:27018")`

----------------

## Issue 3
#### Problem: 
saving chunk size method changed in latest mongodb version
- `db.settings.save( {_id:"chunksize", value: 1} )`

#### Error:
- [direct: mongos] config> db.settings.save( {_id:"chunksize", value: 1} ) TypeError: db.settings.save is not a function

#### Fix:
- `db.settings.updateOne({_id:"chunksize"}, {$set: {_id: "chunksize", value: 1}}, {upsert: true})`

----------------


## Issue 4
#### Problem:
- At 7:42 in the lecture, the db.movies.find command with regex is not supported with the latest mongodb version.
- `db.movies.find({name:"{$regex:^P}"})`

#### Error:
- Error in video it said `db.movies.find({name:"{$regex:^P}"})`

#### Fix:
- `db.movies.find({name:{ $regex: "^P" }})` 

---------------

## Issue 5
In both Java applications,
       
- I had to add the following dependency to resolve the data format error. 
            
```
<dependency>
<groupId>javax.xml.bind</groupId>
<artifactId>jaxb-api</artifactId>
<version>2.3.1</version>
</dependency>
```    
 - mongodb driver version should be upgraded to 3.9.0. It not, I'm getting an unsupported OP_QUERY error. If I'm upgrading it to the latest version, there are few more errors (mongo driver methods not working) in the code. So, I went to 3.x version.


 -------------------

 After setting the repl set for shards, whenever I do `sh.status(true)` as you mentioned, I'm not able to see the min-key, max-key mappings or shards or anything like that. Below is the output that I got when I do sh.status(true). Note that I ran the users application twice so there are 2000 users and 1002 movies. Also, I ran a regex to see if there is data, and yes the data is successfully populated.

```json
 [direct: mongos] videodb> sh.status(true)
shardingVersion
{ _id: 1, clusterId: ObjectId('67cee5a2ba96ea6ec2e7b8cc') }
---
shards
[
  {
    _id: 'shard-rs',
    host: 'shard-rs/127.0.0.1:27017,127.0.0.1:27018',
    state: 1,
    topologyTime: Timestamp({ t: 1741614544, i: 10 }),
    replSetConfigVersion: Long('-1')
  }
]
---
active mongoses
[
  {
    _id: 'Mac.lan:27023',
    created: ISODate('2025-03-10T13:39:23.785Z'),
    mongoVersion: '8.0.5',
    ping: ISODate('2025-03-10T14:34:31.049Z'),
    up: Long('3307'),
    waiting: true
  }
]
---
autosplit
{ 'Currently enabled': 'yes' }
---
balancer
{
  'Currently enabled': 'yes',
  'Currently running': 'no',
  'Failed balancer rounds in last 5 attempts': 0,
  'Migration Results for the last 24 hours': 'No recent migrations'
}
---
shardedDataDistribution
[
  {
    ns: 'videodb.users',
    shards: [
      {
        shardName: 'shard-rs',
        numOrphanedDocs: 0,
        numOwnedDocuments: 20002,
        ownedSizeBytes: 25662566,
        orphanedSizeBytes: 0
      }
    ]
  },
  {
    ns: 'config.system.sessions',
    shards: [
      {
        shardName: 'shard-rs',
        numOrphanedDocs: 0,
        numOwnedDocuments: 11,
        ownedSizeBytes: 1089,
        orphanedSizeBytes: 0
      }
    ]
  },
  {
    ns: 'videodb.movies',
    shards: [
      {
        shardName: 'shard-rs',
        numOrphanedDocs: 0,
        numOwnedDocuments: 10002,
        ownedSizeBytes: 6281256,
        orphanedSizeBytes: 0
      }
    ]
  }
]
---
databases
[
  {
    database: { _id: 'config', primary: 'config', partitioned: true },
    collections: {
      'config.system.sessions': {
        shardKey: { _id: 1 },
        unique: false,
        balancing: true,
        chunkMetadata: [ { shard: 'shard-rs', nChunks: 1 } ],
        chunks: [
          { min: { _id: MinKey() }, max: { _id: MaxKey() }, 'on shard': 'shard-rs', 'last modified': Timestamp({ t: 1, i: 0 }) }
        ],
        tags: []
      }
    }
  },
  {
    database: {
      _id: 'videodb',
      primary: 'shard-rs',
      version: {
        uuid: UUID('0b9bc150-ea72-4867-827e-d029e58194eb'),
        timestamp: Timestamp({ t: 1741615236, i: 2 }),
        lastMod: 1
      }
    },
    collections: {
      'videodb.movies': {
        shardKey: { name: 1 },
        unique: false,
        balancing: true,
        chunkMetadata: [ { shard: 'shard-rs', nChunks: 1 } ],
        chunks: [
          { min: { name: MinKey() }, max: { name: MaxKey() }, 'on shard': 'shard-rs', 'last modified': Timestamp({ t: 1, i: 0 }) }
        ],
        tags: []
      },
      'videodb.users': {
        shardKey: { _id: 'hashed' },
        unique: false,
        balancing: true,
        chunkMetadata: [ { shard: 'shard-rs', nChunks: 1 } ],
        chunks: [
          { min: { _id: MinKey() }, max: { _id: MaxKey() }, 'on shard': 'shard-rs', 'last modified': Timestamp({ t: 1, i: 0 }) }
        ],
        tags: []
      }
    }
  }
]
```

- Is there a way to see the individual shards and number of chunks in each just like in the video you mentioned?
