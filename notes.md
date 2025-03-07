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

## Advanced concepts

### JSON message converter

- `SimpleMessageConverter` - String <-> RabbitMQ
- `Jackson2JsonMessageConverter` - Java object <-> RabbitMQ (with JSON as body)
- Example:
  - fanout exchange `x.dummy` -> `q.dummy`
- entity being serde must have the same path (package + name) on both sides

### Scheduling consumer

- schedule turning on/off for consumer if we want that it is active only at certain time
- `[fanout] x.dummy` -> `q.dummy`

### Consumer prefetch

- RabbitMQ can send several messages to its consumer. However consumer can process only one message at time. A consumer receives a message, acknowledges it and it stays in its memory. Meaning, those message are unacknowledged and received by consumer.
- Spring has a prefetch config set to 250. What does this mean?
  - if we have 500 messages in a queue and two consumers
  - then 250 messages will be pushed to consumer 1 and 250 to consumer 2 and queue will be empty
  - the problem: what if message processing is taking too much time. Then these two consumers will have of all the messages and even if we add another consumer, it will not have any message to process.
- Spring allows us to set the prefetch value:
  - for fast message processing time:
    - one or few consumers -> larger consumer prefetch number
    - many -> moderate consumer prefetch number (too small value, consumers are idle, too big number, one of them is super busy, while others are mostly idle)
  - for slow message processing time -> consumer prefetch = 1
- RabbitMQ schema:
  - `[fanout] x.dummy` -> `q.dummy`

### Multiple prefetch values

- Example:
  - `x.transaction` -> `q.transaction` -> 100 ms/message (fast processing time) -> high prefetch value (e.g. Spring's default 250)
  - `x.scheduler` -> `q.scheduler` -> 1 minute/message (slow) -> prefetch value = 1

### Message order

- messages come to queue in order
- what about the consumer?
  - if we have only one consumer, then the consuming order is guaranteed
  - if we have multiple consumers, then the message consumption order is not guaranteed
- Impact:
  - message: invoice-created, invoice-updated, invoice-paid
  - we have a problem if they are not processed in order when we have multiple consumers - e.g. consumer 1 takes `invoice-created` and `invoice-updated` while consumer 2 takes a message `invoice-paid`. An option is that consumer1 processes `invoice-created` and then consumer 2 processes `invoice-paid` and then after that consumer 1 processes `invoice-updated`.

### Multiple message types

### One queue for one message type

- `x.invoice` -> `q.invoice.paid` with routing key `paid`; one type of message
- `q.invoice.created` with routing key `created`; second type of message

### One queue for multiple message types

- `x.invoice` -> `q.invoice` which contains two types of messages and in header `TypeID` we can set the message type (e.g. `invoice.created` and `invoice.paid`)

### Pros and cons

- maintain queues
  - one queue/message type - many queues
  - one queue + multiple message types - less queue
- consumer code
  - one queue/message type - relatively easy
  - one queue + multiple message types need to filter message; might be harder if not using Spring

#### One queue + multiple message types

- Example:

  - `[fanout] x.invoice` -> `q.invoice`

- `@RabbitListener`
  - on class
    - combined with `@RabbitHandler`
    - invoke different method based on payload

### Consistent hash exchange

- How to achieve message order and multiple consumers in RabbitMQ?
- hash: mathematical function that converts an input to a consistent numerical value output
- RabbitMQ has a plugin for consistent hash exchange
- Example:
  - 4 messages in a queue - XYYX; PAID (4) PAID (3) NEW (2) NEW (1)
  - `x.invoice` and `q.invoice`
  - First and third message are processed by consumer 1 and second and fourth by consumer 2
  - Consumer 1 needs 1 second to process each message and consumer 2 needs 3 second to process each message
  - in second 2, consumer 1 has already finished the process; data based on invoice Y is now PAID
  - slower consumer, consumer 2 finishes processing invoice Y at the second 3; the problem is at second 2 data for invoice Y is already on PAID status; so at second 3 status is rewritten to NEW; it should be PAID by message order
  - one queue per invoice would solve the problem since it guarantees the message order -> `q.invoice.one` and `q.invoice.two`
- How different exchanges will distribute invoices?
  - turn exchange: odd messages will go to the first queue, even ones to the second queue
    - Y(PAID)X(NEW) -> consumer 1
    - X(PAID)Y(NEW) -> consumer 2
    - consumer 1 is faster and will still process the blue invoice first and consumer 2 process later; we still have the wrong status of Y
- Consistent hashing exchange distributes routing keys among queues instead of messages, all messages with the same routing key will go to the same queue. We use the identifier of the invoice (X or Y) as routing key. RabbitMQ will calculate the hash and all messages with the same routing key will go to the same queue. E.g. all messages with identifier X will be hashed to queue one and messages with routing key Y will be hashed and routed to queue 2.
- What is the problem - the message distribution might change if we change the number of queues; if we have to change the number of queues, we better do it when there are no pending messages

- Example:
  - `x.invoice`
  - `q.invoice.one` and `q.invoice.two`
  - routing key is ratio - `q.invoice.one` is 2n (receives twice as many messages as queue 2) and for `q.invoice.two` - n

### Single active consumer

- only one consumer per queue -> `x-single-active-consumer`
- guaranteed message order
- if we have two consumers and we set only one active consumer (app). If that consumer dies, the other one is automatically turned on

### Reliable publish

- is my message published?
- something wrong? (invalid exchange; correct exchange, invalid routing key)
- publisher can confirm (tells to Java code if the message is really published)
- async callback from RabbitMQ server to publisher; we have to add correlated data so we can track which message and which callback

  ```yml
  spring:
    rabbitmq:
      publisher-confirm-type: correlated
      publisher-returns: true
      template:
        mandatory: true
  ```

- `[direct] x.dummy2 -> (routing key dummy2) -> q.dummy2`

### Request/Reply

- publisher sends a message to `x.invoice` which routes it to `q.invoice`
- if something goes wrong in consumer in its business logic, e.g. it should remove some funds from someone's account or add it, then a rollback event should happen
- consumer then sends an invoice cancel message to `x.invoice.cancel` and it routes it to `q.invoice.cancel` and that message is consumed by Payment cancelation service (consumer)
- This pattern is known as **request/reply**. Request direction goes to `x.invoice` and `q.invoice`, while the reply direction goes from invoice consumer to `x.invoice.cancel` and `q.invoice.cancel`

## Stream

- has a similar task as queue, but messages are stored and consumed differently
- new message is appended to the end of the stream
- in a queue, once the message is consumed by a consumer, it is removed from the queue, meaning only one ocnsumer consumes a message
- messages in stream are read by using the `non-destructive consumer semantic`
  - message stays in a stream even after some consumer read it, so multiple consumers can read the same message
  - the only way to remove a message from a stream is to set max-age (similar to TTL) - how long the message remains in a stream; after that time it elapses and will be removed from stream
- use cases:

  - large fan-outs
  - replay (time-travelling) - once read, messages from queue are removed, while in streams we can go back to any position and replay the message reading
  - performance - better performance than queues
  - large backlog
    - RabbitMQ are optimized for emptying quickly and may perform poorly with millions of messages
    - streams efficiently store large volumes of data with minimal in-memory overhead

- Queues vs streams

| Aspect               | Queue                                                                                   | Stream                                                                                                                                                   |
| -------------------- | --------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Queue durability     | Can be durable (permanent queue)/transient (temporary queue, deleted on server restart) | Always durable                                                                                                                                           |
| Message durability   | Need setting onqueue and per-message delivery mode                                      | Always durable                                                                                                                                           |
| Lazy mode            | Can be lazy (data stored on disk) or no (data stored in-memory)                         | Always lazy (data stored on disk). Streams store all data directly on disk, after a message has been written it does not use any memory until it is read |
| Prefetch             | Supports global prefetch for all consumers                                              | Requires per-consumer prefetch                                                                                                                           |
| Message live time    | Setting via TTL (Time To Live)                                                          | Setting via retention (max-age)                                                                                                                          |
| Dead letter exchange | Yes                                                                                     | No                                                                                                                                                       |

- coding and RabbitMQ streams
  - off-the self RabbitMQ feature
  - for coding, we need to enable `rabbitmq stream plugin`
    - create/delete stream
    - publish/consume
  - using port 5552
  - set `advertised_host`
  - optionally - set `advertised_port`
- use `org.springframework.amqp:spring-rabbit-stream`
- To publish to stream directly, we can use `RabbitStreamTemplate` (one per stream) - for high throuhput. To publish via exchange we can use `RabbitTemplate` and `RabbitListener` to consume it.

```
docker run -d --restart always --name rabbitmq --hostname docker-rabbitmq -p 5672:5672 -p 15672:15672 -p 5552:5552  -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -e RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS="-rabbitmq_stream_advertised_host_localhost" -v ./data:/var/lib/rabbitmq/mnesia rabbitmq:3.12-management
```

- Stream can be bound to any exchange: `[direct] x.hello` -> `s.hello (stream)` and `q.hello (queue)`
- Stream consumer reads messages produced just after its activation

## Offset tracking

- offset is like an index in a queue
- every consumer tracks its offset
- a consumer can start reading messages from a particular offset specification
- apart from the offset number, every message has a timestamp when it was received
- a consumer can choose to start consuming from a specific timestamp
- stream stores messages in batches (of 10_000) for performance reasons. So, if a stream has 17000 messages, they will be stored in 2 batches.
- offset specifications:
  1. first - consume from the very beginning of the stream
  2. (default) next - consume from the next incoming message
  3. last - from the beginning of the last batch
- offset is stored as part of the RabbitMQ stream; RMQ server tracks offsets for each consumer
- each consumer must have a name
  - should be unique per consumer
  - name stays the same after the consumer restarts
- RabbitMQ does not enforce the name uniqueness per consumer
- multiple stream consumer instances with the same name might interleave wehn consuming from a stream; mess with offset and events (e.g. processing the same message multiple times)

- Example:
  - `[fanout] x.number` -> `s.number (stream)`
- Offset types in Spring:
  1. absolute
  2. first
  3. last
  4. next (default)
  5. timestamp

1.

```java
@RabbitListener(queue = "mystream")
void listen(String s) {

}
```

2.

```java
@RabbitListener(queue = "mystream")
void listen(org.springframework.amqp.core.Message message) {

}
```

3.

```java

@RabbitListener(queue = "mystream", containerFactory = "myCf")
void listen(
  com.rabbitmq.stream.Message message,
  com.rabbitmq.stream.MessageHandler.Context ctx
)
```

- Define a container factory with offset type

```java

@Bean
RabbitListenerContainerFactory<StreamListenerContainer> myContainerFactory(Environment environment) {
  var factory = new StreamRabbitListenerContainerFactory(environment);
  factory.setNativeListener(true);

  // other factory setting
  return factory;
}

```

- `factory.nativeListener` by default is false - RabbitListener will not take message data type and context as input
- the default tracking strategy is - `builder.autoTrackingStrategy()` -> code will automatically update offset, no need to it manually
- when using `builder.offset()`, we must set consumer name using `builder.name()`

#### Consumer goes first

- 5 messages produced when the producer is activated (0-indexed); consumer active before that
  |Consumer's offset type | Messages consumed |
  |---|---|
  | Absolute (example: start with offset 3) | 3, 4|
  | First | 0, 1, 2, 3, 4 |
  | Last | 0, 1, 2, 3, 4 |
  | Next | 0, 1, 2, 3, 4 |
  | Timestamp (T-5 minutes) | 0, 1, 2, 3, 4 |
  | Default (using default offset: next) | 0, 1, 2, 3, 4 |

- **Offset tracking stored in stream itself, as additional messages in stream, but it does not interfere with the consumer logic that listens to stream and consumes messages (the number of messages increases by some rule, time or number of messages)**
- **avoid making decisions based on offset values, might not behave as expected**

#### Producer goes first

- producer is activated first, produced 5 messages and then the consumer is brought up
  | Consumer's offset type | Message consumed | Notes |
  |---|---|---|
  |Absolute (example: start with offset 3) |3, 4 | Starts from anm absolute offset|
  | First | 0,1,2,3,4 | Starts from the beginning |
  | Last | 0,1,2,3,4 | No stored offset, so starts from the beggining|
  | Next | - | Does not take the existing data from stream |
  | Timestamp (T-5 minutes) | - | In this example, data in the stream is older than 5 minutes|
  | Default (using default offset: next) | - | See "next" |

#### Manual offset tracking

- manually update the offset
- if the consumer does not update its offset, when restarted, it will start reprocessing messages from the beginning
- storing the offset is quick operation, but every offset update means the stream is getting bigger
- store offset every few thousand messages; if the consumer is restarted it might happen that the stored offset is behind the last processed message which leads to reprocessing the same messages (that were already processed)
- make consumer idempotent

## Single active consumer (stream)

- Queue - if we have multiple consumers, then each instance receives a different message
- Stream - if we have multiple consumers, then all of them receive all messages (message stay in stream)

- if we have a single active consumer, it may happen that some messages are reprocessed when another instance of queue takes over
- why is that so?
  - well the offset is stored in stream and it's not stored per message, but at same interval/no of messages
  - so, that means when one consumer is brought down and another consumer instance takes over, it will start from the offset that is stored in stream, despite the fact that messages might be processed by the first consumer after the offset had been stored. So messages with an offset larger than the last stored one wil be reprocessed.

## Super Stream

- a logical stream of an exchange and several streams. From an application perspective, those "several streams" are seens as a single stream
- each stream can be seen as stream partition
- so, we can enable a single active consumer and then with superstream streams, every consumer listens to just one stream having a parallel processing without duplicate processing
- for redundancy sake, we can have multiple instance consumers per consumer group, in case one consumer goes down, another instance can jump in and take over the processing

### Super stream in RabbitMQ vs Kafka

- RabbitMQ super stream is similar to Kafka's topic & partition
- many technical details are left, but in the essence, they are the same
- Example:
  - exchange: `s.super.stream`
  - three streams: `s.super-stream-0`, `s.super-stream-1` and `s.super-stream-2`

#### Using MessageID as routing key

- it has several benefits:
  - RabbitMQ client library automatically converts message ID to routing key
  - can use various data types (long, string, UUID, raw bytes) and any value that is useful to our business case (user id, invoice id etc.)
  - the library takes care to compute the ID, so that every partition gets message uniformly (as much as possible)
  - client will guarantee that all messages with the same message ID go to the same partition

### Super stream consumer

- we cannot use `@RabbitListener` for super stream consumers
- when we have multiple partitions and want to create a single active consumer for each super stream partition, we must set `Environment.maxConsumersByPartition = 1`
