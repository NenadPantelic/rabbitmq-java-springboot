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

- Two options
  - automatic exception throwing
  - manual reject + acknowledgment (risky, prone to dev mistake)

## Headers exchange

- multiple criteria in message header (instead of the routing key)
- special header - `x-match`

  - `all` (default): boolean `AND`
  - `any`: boolean `OR`

- Example:
  - criterias: color, material
  - promotion: discount, free delivery
  - details:
    - white AND wood = discount
    - red AND steel = discount
    - red OR wood = free delivery

## Retry mechanism

- requeue or send to DLX
- retry after x seconds, n times; after that, send to DLX

- Example:

1. work - `x.guideline.work` and `q.guideline.image.work` - primary business process that does the heavylifting; if an exception occurs, it will trigger the retry mechanism
2. wait - `x.guideline.wait` and `q.guideline.image.wait` - invalid message will be kept here for some time before sending again to work exchange/queue (ttl and dlx)
3. dead - `x.guideline.dead` and `q.guideline.image.dead` - message where all attempts failed are stored here

- wait is DLX for work and dead
- work is DLX & TTL for wait
- `ImageConsumer` - consumes work, rejects to wait and republishes to dead
- `DeadImageConsumer` - consumes from dead queue

- Exchanges:
  1. `x.guideline.work`
  2. `x.guideline.wait`
  3. `x.guideline.dead`
- Queues:
  1. `q.guideline.image.work`
  2. `q.guideline.image.wait`
  3. `q.guideline.image.dead`
  4. `q.guideline.vector.work`
  5. `q.guideline.vector.wait`
  6. `q.guideline.vector.dead`

#### Consumer with retry mechanism

1. Check retry limit
2. if the retry limit is less than threshold, send message to wait exchange
3. otherwise, send message to dead exchange

- RabbitMQ message has a header telling how many retries have been tried to process this message - `X-Death` (retry count)

### Fanout exchange retry

- 4 exchanges:

1. `x.guideline2.work`
2. `x.guideline2.retry`
3. `x.guideline2.wait`
4. `x.guideline2.dead`

- 3 queues:

1. `q.guideline2.accounting.work`
2. `q.guideline2.accounting.wait`
3. `q.guideline2.accounting.dead`

- Bindings:

1. `x.guideline2.work` -> `q.guideline2.accounting.work`
2. `q.guideline2.work` -> DLX -> `x.guideline2.wait`
3. `x.guideline2.wait` -> `q.guideline2.accounting.wait`
4. `q.guideline2.accounting.wait` -> DLX & TTL -> `q.guideline2.accounting.wait`
5. `x.guideline2.retry` -> routes invalid messages -> `q.guideline2.accounting.work`. We need a direct exchange here, not fanout because otherwise if this exchange is fanout also, all queues binded to it will receive the same message after requeue and that means some of them which successfully processed the message the first time when it arrived now will process it again processing duplicate messages.

- `AccountingConsumer`

1. consumes from `x.guideline2.work`
2. rejects to `x.guideline2.wait`
3. publishes to `x.guideline2.dead`

- `DeadAccountingConsumer`

1. consumes from `q.guideline2.accounting.dead`

#### Consumer with retry mechanism

1. Check retry limit
2. if the retry limit is less than threshold, send message to wait exchange (direct exchange, use correct routing key)
3. otherwise, send message to dead exchange

## RabbitMQ vs Apache Kafka

### Message retention

- Kafka: by policy (defines a retention policy, in that period the message is hold on disk)
- RabbitMQ: by acknowledgment

- in Kafka multiple consumers can consume the same message; stores on disk everything
- in RabbitMQ if we want to have multiple consumers consuming the same message, the publisher must publish it; stores on disk or in memory

### Message routing

- Kafka: no routing mechanism, publisher publishes a message to some topic (and partition)
- RabbitMQ: routing mechanism using exchange, so messages are routed using a key

### Multiple consumers

- Kafka: topic - partition - one consumer per partition, guaranteed order
- RabbitMQ: multiple consumers per queue (suitable for fast producer, slow consumer), order not guaranteed (consumers are competing against each other)

### Consumer push/pull model

- Kafka: consumer pulls from Kafka topic; only one consumer accesses one partition
- RabbitMQ: pushes message to consumer; to prevent the consumer overload, a consumer can set the prefetch limit (the number of unprocessed messages it can accept; helps in fair distribution of messages accross consumers)

- RabbitMQ is easier to manage
- Kafka is more scalable & good for big data

## RabbitMQ plugins

- https://www.rabbitmq.com/docs/plugins

```
docker exec -it rabbitmq bash
rabbitmq-plugins enable plugin-name
# e.g.
rabbitmq-plugins enable rabbitmq_shovel_management
#  disable plugin
rabbitmq-plugins disable plugin-name
# e.g.
rabbitmq-plugins enable rabbitmq_shovel_management
```

### Delayed message exchange

- use case:
  - some reports contain very large data (heavy processing & affects the system performance)
  - report listener process all reports immediately (fixed process)
  - workaround: publish large report requests after 19:00; delay publish process, listener still process immediately
- RabbitMQ does not natively support delayed publishing
- use plugin Delayed Message (community plugin)
- hold message until specified delay time, then publish it
  - install plugin
  - use exchange type `x-delayed-message`
  - add header on message, indicating delay duration
  - published after the delay period elapses
- check the location of the plugin folder: `rabbitmq-plugins directories -s`
- copy the binary into a container - `docker cp rabbitmq_delayed_message_exchange-3.12.0.ez rabbitmq:/opt/rabbitmq/plugins`
- enable plugin: `rabbitmq-plugins enable rabbitmq_delayed_message_exchange`
- Example:
  - 1 exchange - `x.delayed`; have to set `x-delayed-type: direct` (any exchange type)
  - 1 queue - `q.delayed`
  - bind using routing key `delayThis`

## RabbitMQ in cloud

- `cloudamqp.com`
- free to use (basic functionalities for free plan)

## Spring Retry mechanism

### Direct exchange

- no code
- configuration in `application.yml`
- define interval for retry
- define maximum retry attempts
- retry interval multiplier

- Example, 2 exchanges:
  1. `x.spring.work` -> based on image type sends jpg/png to `q.spring.image.work` and svg to `q.spring.vector.work`
  2. `x.spring.dead` -> DLX from `q.spring.image.work` and `q.spring.vector.work` go to this exchange. It routes messages based on type:
     - jpg/png to `q.spring.image.dead`
     - svg to `q.spring.vector.dead`
- no "wait" exchange, Spring will handle that for us
- one consumer class, two methods

- Exchanges:
  - `x.spring.work`
- Queues:
  - `q.spring.image.work`
  - `q.spring.vector.work`

### Fanout exchange

- Example, 2 exchanges:

  1. `x.spring2.work` -> sends/fans out messages `q.spring2.accounting.work` and `q.spring2.marketing.work`
  2. `x.spring2.dead` -> DLX from `q.spring2.accounting.work` and `q.spring2.marketing.work` to this exchange. It routes messages by routing key (direct exchange):
     - `dead-accounting` to `q.spring2.accounting.dead`
     - `dead-marketing` to `q.spring2.marketing.dead`

- Exchanges:
  - `x.spring.work`
- Queues:
  - `q.spring.image.work`
  - `q.spring.vector.work`
