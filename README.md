# ToyMB

This is a guide to ToyMB (Toy Message Broker).

## Features
- Persistent Messages.
- Message groups to facilitate publisher-subscriber model. 
- Topic partitions for loadbalancing among consumers.
- Pull and push interface available for subscribers.
- On the fly topic creation.
- Easy integration with spring applications.
- Java Spring boot driver available with annotation and template style interface.
- Clustering with rendezvous routing.
- Straightforward enrichment API.
- Internally works with http, new drivers can be implemented easily. 

## Driver usage
To interact with ToyMB, import the client jar or copy the classes from 'middleware-driver'.
##### Subscriber
To subscribe to a topic "topic-name" as a consumer of group "my-group" :
```java
import com.toymb.annotations.Subscribe;
import com.toymb.common.NotificationMessage;
import com.toymb.common.ToyMBConsumer;

@Subscribe(group = "my-group", topic = "topic-name")
public class MyConsumer implements ToyMBConsumer{
	
	@Override
	public void consume(NotificationMessage message) {
		String json = message.getEntity();
		// Do stuff with the json	
	}
}
```
Whenever there is a message to "topic-name", TopyMB will send it to this consumer and the driver will propagate it to the correct consumer. 
##### Publisher
To send message (publish) to "topic-name" :
```java
import com.toymb.client.ToyMBTemplate;

public class MyClass{
	
	@Autowired
	ToyMBTemplate template;
	
	public void someMethod(Serializable message) {
		template.send("topic-name","my-key",message);
	}
}
```
Whenever a message is published to topic "topic-name", ToyMB will send it to all subscribers who subscribed to it. "my-key" is used to find the partition. All messages belonging to a partitions will always go to the same consumer.
##### Enriching API 
The topic whose messages should be enriched should implement `EnrichableTopic`.
```java
package com.toymb.common;

public interface EnrichableTopic extends Topic{
	
	void enrich();
}
```
The enrichment logic is defined by implementing `TopicEnricher`.
```java
package com.toymb.common;
public interface TopicEnricher<T extends EnrichableTopic> {

	void init() throws Exception;
	void enrich(T t) throws Exception;
}
```
The "init()" method is called only once by ToyMB while starting. One time activities that are required for enrichment should be defined here eg. connecting to a database.
When ToyMB receives message for this topic, it will propagate it  enricher and call the "enrich" method.
###### Example : 
```java

public class MyTopic implements EnrichableTopic{

	String s='';
	
	@Override
	public void enrich() {
		s=s+" enriched";
	}
}


@Component
public class MyTopicEnricher implements TopicEnricher<MyTopic>{

	DatabaseConnection conn=null;

	@Override
	public void init() {	
		conn= // Establish connection
	}

	@Override
	public void enrich(MyTopic t) {
		// get some data using conn and changes 't'
		
		t.enrich(); // additional/optional enrichment encapsulated in the Topic
	}
}
```

<div style="page-break-after: always;"></div>


## Internal working
### Node startup
1. Generate an random UUID.
2. Save self information with ID, ip, port in database.
3. If the topics mentioned in "topics" property are not present, create those topics each with 100 partitions.
4. If topic in classpath:
	1. Create a map of topic names and the `Class`.
	2. Create a map of topic names and `TopicEnricher` objects and call "init" method of all enrichers.

### When a new message arrives:
1. If the message has no id, a new unique random UUID is allocated to it.
2. If it is a known `Enrichable` topic :
	1. Deserialize the message.
	2. Enrich it using the enricher.
3. If it is unknown topic :
	1. Find who should be the owner of this topic according to the topic name and the node ids. It does not matter who creates the topic (owner or non-owner) as Levenshtein distance is used to allocate owner and it will always allocate the same owner no matter who creates the topic.
	2. Create a new topic with 100 partitions.
4. If the current node is the owner of the topic:
	1. Calculate partition number for the message using the key's hash and dividing by number of partitions, the mod of remainder is the partition number.
	2. Persist the message and do the following asynchronously:
		1. Find all the groups for this topic.
		2. Find the subscriber who ones the partition of this message and is a "push" type.
		3. Send via http the message to the subscriber.
5. If the current node is not the owner of the topic:
	1. Send the message to all its peers except for the one which sent the message.
6. Send acknowledgment.

<div style="page-break-after: always;"></div>


### When a new subscriber arrives:
1. If the subscribe-message has no id, a new unique random UUID is allocated to it.
2. If the subscriber is subscribing to a unknown topic:
	1. Find the owner of this new topic.
	2. Create new topic with 100 partitions.
3. If the subscriber is from a unknown group, create the new group.
4. If the current node is the owner of the topic:
	1. Create a new subscriber in database with it address and port.
	2. Redistribute the partitions among the group's subscribers 
5. If the current node is not the owner of the topic:
	1. Send the subscribe-message to all its peers except for the one which sent the message.
6. Send the unique id generated for the subscriber, the driver will use this id to send messages to the correct consumer.

	
## Deploying 
- Dockerfile available.
- Dockerfile has healthcheck if container sequence orchestration is required.
- Environment configs available :
	- spring.datasource.url : Address of the SQL database.
	- spring.datasource.username
	- spring.datasource.password
	- spring.datasource.driver-class-name : tested with MySQL and H2
	- spring.jpa.hibernate.ddl-auto 
	- spring.jpa.properties.hibernate.dialect
	- spring.jpa.database-platform
	- server.port
	- node.name : name to be used externally, internally a new id is allocated to each node on startup.
	- topics : comma separated values of names of topic that the node owns, if topic is not present, new topic will be created with 100 partitions.
	- enrichingtopics : comma separated values of enrichable class and its enricher.
	- peers : external name/ips of other(can be subset) of nodes in the cluster.
	

