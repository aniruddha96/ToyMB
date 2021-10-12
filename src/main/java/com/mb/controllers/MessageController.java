package com.mb.controllers;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.ToyMbApplication;
import com.mb.common.ClientMessage;
import com.mb.common.Topic;
import com.mb.common.TopicEnricher;
import com.mb.database.Database;
import com.mb.entities.MessageEntity;
import com.mb.entities.TopicEntity;
import com.mb.repositories.MessageRepository;
import com.mb.repositories.TopicRepository;
import com.mb.common.EnrichableTopic;

@RestController("/message")
public class MessageController {

	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	TopicRepository topicRepo;

	@PostMapping("/sendmessage")
	public String addMessage(@RequestBody ClientMessage clientMessage) throws Exception {
		String retval = "";
		try {
			Topic topicMessage = (Topic) mapper.readValue(clientMessage.getBody(),
					ToyMbApplication.topicMapping.get(clientMessage.getTopicName()));

			if (topicMessage instanceof EnrichableTopic) {
				TopicEnricher<EnrichableTopic> enricher = ToyMbApplication.enricherMapping.get(clientMessage.getTopicName());
				EnrichableTopic t = (EnrichableTopic) topicMessage;
				enricher.enrich(t);
			}
			Database.addMessage(clientMessage.getTopicName(), clientMessage.getKey(), topicMessage);
			MessageEntity entity = new MessageEntity();
			entity.setMessage(mapper.writeValueAsString(topicMessage));
			entity.setPartition(clientMessage.getKey().hashCode()%Database.topicInfos.get(clientMessage.getTopicName()).getPartitions());
			entity.setTopicName(clientMessage.getTopicName());
			messageRepository.save(entity);
			TopicEntity topic = topicRepo.findById(clientMessage.getTopicName()).get();
			topic.setCurrentOffset(entity.getId());
			topicRepo.save(topic);
			retval = topicMessage.toString();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return retval;
	}

	@GetMapping("/get")
	public String getMessage() {
		return Database.database.toString();
	}
}
