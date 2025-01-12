# RabbitMQ

## Introduction

### Messaging system

- application-to-application communication
- handle messages
- integration between systems

- without messaging system
  - many integration points (one service can be source for many applications and target for other services)
  - different implementations (data protocol - database link, API, file) or data format (binary, JSON, CSV, XML)
  - maintain connection
- with messaging system
  - decoupling
  - use the same format
  - less integration points
- one use-case suitable for messaging system
  - every received payment data - create accounting journal, send notification to customer, inform logistic to send item
  - every possible fraud data - temporarily suspend a user account, send an email to auditor, hold transaction
- avoid resource locking for a time-consuming process

### RabbitMQ

- open-source, but available as enterprise as well
- queueing data in real time
- reliable and provide high availability
- available on-premise or cloud
- written in Erlang

- RabbitMQ vs others
  - provides web interface for management and monitoring
  - built-in user access control
  - built-in REST API
  - multiple programming languages for client

### RabbitMQ in Docker

```
docker run -d --restart always \ #restart the container when the OS is restarted or the container crashes
--name rabbitmq --hostname docker-rabbitmq \
-p 5672:5672 \ # RabbitMQ port
-p 15672:15672 \ # RabbitMQ web UI port
-v <local-path>:/var/lib/rabbitmq/mnesia \ # volume to persist data
rabbitmq:3.12-management # RabbitMQ + management console

```

E.g.:

```
docker run -d --restart always --name rabbitmq --hostname docker-rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -v ./data:/var/lib/rabbitmq/mnesia rabbitmq:3.12-management
```

```
Add a new/fresh user, say user test and password test:

rabbitmqctl add_user test test
Give administrative access to the new user:

rabbitmqctl set_user_tags test administrator
Set permission to newly created user:

rabbitmqctl set_permissions -p / test ".*" ".*" ".*"
```

## RabbitMQ basic concepts

- publisher/producer - an entitety that sends the message to RMQ
- exchange and queue
- subscriber/consumer - an entity subscribed to a queue consuming messages from it

### Queue & exchange

- queue: a buffer that stores messsages
- exchange: routing messages to queue
- routing key: a key that the exchange uses to decide how to route the message to queue(s)
- binding: a link between exchange and queue(s)

#### Message durability

- guarantee that a message will not be lost in server restart/failure
- the default RMQ setting is transient (not durable) -> queue and messages will be lost on restart
- requires two things for durable messages
  - declare a queue as durable when creating it (store queue to disk)
  - persistent delivery mode when publishing a message (messages flushed to disk)

### Queue types

- queues: classic & quorum
- stream

#### Classic queue

- currently default RMQ queue type
- good performance
- suitable where high availability & data replication are not critical
- single node for message storage
- rely on disk and memory for persistence
- limited fault tolerance (message loss possible if the node goes down)
- use case - when the data loss is acceptable

#### Quorum queues

- designed to prevent data loss
- replicate data across mulitple RMQ servers
- ensure data remains consistent accross nodes by using a consensus algorithm
- use case - when data integrity & no-message loss is critical (finance or healthcare systems e.g.)
- uses a cluster of odd number of nodes

- Classic vs quorum queues:

1. non-durable (temporary)
   - classic queue - queue can be temporary (automatically deleted when RMQ is restarted)
   - quorum queue - always durable (queue not deleted on RMQ restart)
2. message durability
   - set per message
   - all messages always durable
3. lazy mode
   - can have lazy mode (data stored on disk) or no (data stored in-memory)
   - always lazy (stored on disk)

#### Multiple consumers for a queue

- publisher (producer) works faster than consumer
- subscriber (consumer) bottleneck
- solution? use multiple consumers

## JSON messages

- Producer X publishes a message using format X
- Producer Y publishes a message using format Y
- Consumer C consumes messages from both of them; then it must know both formats

## Exchange

- exchange - distribute messages based on the routing key to queues
- it can send the same message to multiple queues (copy the same message multiple times)
- relation between exchanges and queues is called binding

### Fanout exchange

- multiple queues for a single message, broadcast to all queues bound to it
- naming convention (unofficial):
  - all letters are lowercase
  - exchange name: `x.[name]`
  - queue name: `q.[name].[sub-name]`

### Direct exchange

- send to selective queue(s) based on routing key and rule
- if routing key does not match any queue, the message will be discarded
- example:
  - input in our system is picture: png/jpg/svg
  - if png/jpg: create a thumbnail + publish
  - if svg: convert to png + create a thumbnail + publish
- file type can be routing key
- direct exchange: `x.picture`
- two queues: `q.picture.image` (png and jpg) and `q.picture.vector` (svg)

### Topic exchange

- multiple criteria routing; e.g picture type & source & size
- example:
  - process by picture type
  - additional requirements:
    - image filter for source `mobile`
    - log large-size vector
- topic exchange: multiple criteria (words with dot delimiter)
- based on routing key
  - `*` - substitutes exactly one word
  - `#` - substitutes zero or more words
- exchange: `x.picture2`
- 4 queues:
  1. `q.picture.image`
  2. `q.picture.vector`
  3. `q.picture.mobile`
  4. `q.picture.log`
- routing key format: `source.size.type`
- 5 bindings:
  1. `*.*.png` - `q.picture.image`
  2. `#.jpg` - `q.picture.image`
  3. `*.*.svg` - `q.picture.vector`
  4. `mobile.#` or `mobile.*.*` - `q.picture.mobile`
  5. `*.large.svg` - `q.picture.log`

## Handling error

### Dead letter exchange

- exception may happen
- Spring by default requeues the message which delivery failed
- But if the error is permanent (e.g. message to big), it will fail every time when delivery is attempted - infinite consumer loop
- send a problematic message to DLX with requeue = false (DLX = dead letter exchange) - the problematic message goes to a different queue then depending on the binding of DLX (a different consumer should handle such messages)
- also works for timeout (when the message times out, it will be sent to DLX)

### TTL - Time to live

- the time in milliseconds that a message can live in a queue without any consumer picking it up
- after TTL passed, the message is dead
- queue can be configured to send "dead" messages to DLX
