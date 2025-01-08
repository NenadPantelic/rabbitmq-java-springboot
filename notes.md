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
