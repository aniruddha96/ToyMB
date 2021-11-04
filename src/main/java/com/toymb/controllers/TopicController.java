package com.toymb.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.toymb.common.ClientMessage;
import com.toymb.common.CreateTopic;
import com.toymb.entities.TopicEntity;
import com.toymb.repositories.TopicRepository;

@RestController()
public class TopicController {

	@Autowired
	TopicRepository repo;
	
	@PostMapping("/advertise")
	public String addMessage(@RequestBody CreateTopic clientMessage) throws Exception {
		Optional<TopicEntity> topic = repo.findById(clientMessage.getTopicName());
		if(!topic.isPresent()) {
			TopicEntity newTopic = new TopicEntity();
			newTopic.setCurrentOffset(0);
			newTopic.setPartitions(clientMessage.getPartitions());
			newTopic.setTopicName(clientMessage.getTopicName());
			repo.save(newTopic);
		}
		return "ack";
	}
	
}
