package com.toymb.controllers;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toymb.ToyMbApplication;
import com.toymb.common.ClientMessage;
import com.toymb.common.EnrichableTopic;
import com.toymb.common.NotificationMessage;
import com.toymb.common.Topic;
import com.toymb.common.TopicEnricher;
import com.toymb.database.Database;
import com.toymb.entities.GroupEntity;
import com.toymb.entities.MessageEntity;
import com.toymb.entities.SubscriberEntity;
import com.toymb.entities.TopicEntity;
import com.toymb.repositories.GroupRepository;
import com.toymb.repositories.MessageRepository;
import com.toymb.repositories.SubscriberRepository;
import com.toymb.repositories.TopicRepository;
import com.toymb.workers.MessageNotifier;

@RestController("/message")
public class MessageController {

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	MessageRepository messageRepository;

	@Autowired
	TopicRepository topicRepo;

	@Autowired
	GroupRepository groupRepo;

	@Autowired
	RestTemplate template;

	@Autowired
	SubscriberRepository subRepo;
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(32);
	
	@PostMapping("/sendmessage")
	public String addMessage(@RequestBody ClientMessage clientMessage) throws Exception {
		String retval = "";
		try {
			String msg=null;
			if(ToyMbApplication.topicMapping.containsKey(clientMessage.getTopicName())){
				Topic topicMessage = (Topic) mapper.readValue(clientMessage.getBody(),
						ToyMbApplication.topicMapping.get(clientMessage.getTopicName()));

				if (topicMessage instanceof EnrichableTopic) {
					TopicEnricher<EnrichableTopic> enricher = ToyMbApplication.enricherMapping.get(clientMessage.getTopicName());
					EnrichableTopic t = (EnrichableTopic) topicMessage;
					enricher.enrich(t);
				}
				msg= mapper.writeValueAsString(topicMessage);
			}else {
				msg=clientMessage.getBody();
			}
			
			
			Optional<TopicEntity> topicOptional = topicRepo.findById(clientMessage.getTopicName());
			if(!topicOptional.isPresent()) {
				TopicEntity newTopic = new TopicEntity();
				newTopic.setCurrentOffset(0);
				newTopic.setPartitions(100);
				newTopic.setTopicName(clientMessage.getTopicName());
				topicRepo.save(newTopic);
			}
			TopicEntity topic = topicRepo.findById(clientMessage.getTopicName()).get();
			MessageEntity entity = new MessageEntity();
			entity.setMessage(msg);
			int hash = clientMessage.getKey().hashCode();
			if(hash<0) {
				hash=hash*-1;
			}
			entity.setPartitionNumber(hash%topic.getPartitions());
			entity.setTopicName(clientMessage.getTopicName());
			messageRepository.save(entity);
			
			topic.setCurrentOffset(entity.getId());
			topicRepo.save(topic);
			/*
			 * MessageNotifier notifier = new MessageNotifier(groupRepo, entity, topic,
			 * template); executor.submit(notifier);
			 */
			/*
			 * for(GroupEntity group: groupRepo.getByTopic(topic)) { for(SubscriberEntity
			 * subscriber : group.getSubscribers()) {
			 * if(subscriber.getPartitions().contains(entity.getPartitionNumber())) {
			 * if(subscriber.getType().contentEquals("push")) { StringBuilder sb = new
			 * StringBuilder("http://").append(subscriber.getIp())
			 * .append(":").append(subscriber.getPort()).append("/receivemessage");
			 * NotificationMessage sendMessage= new NotificationMessage();
			 * sendMessage.setEntity(entity.getMessage());
			 * sendMessage.setForSubscriber(subscriber.getId());
			 * sendMessage.setMessageId(entity.getId());
			 * sendMessage.setTopicName(topic.getTopicName());
			 * template.postForEntity(sb.toString(), sendMessage, String.class);
			 * 
			 * }
			 * 
			 * } } }
			 */
			MessageNotifier notifier = new MessageNotifier(messageRepository, topicRepo, groupRepo, template, subRepo,entity.getId());
			executor.submit(notifier);
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "ack";
	}

	@GetMapping("/health")
	public String getMessage() {
		return "up";
	}
}
