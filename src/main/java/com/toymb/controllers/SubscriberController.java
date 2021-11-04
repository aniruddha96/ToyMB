package com.toymb.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.toymb.common.ClientSubscribe;
import com.toymb.entities.GroupEntity;
import com.toymb.entities.MessageEntity;
import com.toymb.entities.SubscriberEntity;
import com.toymb.entities.TopicEntity;
import com.toymb.repositories.GroupRepository;
import com.toymb.repositories.MessageRepository;
import com.toymb.repositories.SubscriberRepository;
import com.toymb.repositories.TopicRepository;

@RestController("/subscriber")
public class SubscriberController {

	@Autowired
	TopicRepository topicRepo;
	
	@Autowired
	GroupRepository groupRepo;
	
	@Autowired
	SubscriberRepository subscriberRepo;
	
	@Autowired
	MessageRepository messageRepo;
	
	@PostMapping("/subscribe")
	public String subscribe(@RequestBody ClientSubscribe message) throws Exception {
		String retval = "";
		UUID id = UUID.randomUUID();
		Optional<TopicEntity> topicOptional = topicRepo.findById(message.getTopicName());
		if(!topicOptional.isPresent()) {
			TopicEntity newTopic = new TopicEntity();
			newTopic.setCurrentOffset(0);
			newTopic.setPartitions(100);
			newTopic.setTopicName(message.getTopicName());
			topicRepo.save(newTopic);
		}
		TopicEntity topic = topicRepo.findById(message.getTopicName()).get();
		GroupEntity group=null;
		Optional<GroupEntity> retgroup = groupRepo.findById(message.getGroupName());
		if(retgroup.isPresent()) {
			group=retgroup.get();
		}
		if(group==null) {
			group= new GroupEntity();
			group.setGroupName(message.getGroupName());
			group.setTopic(topic);
			groupRepo.save(group);
		}
		group.setBeingServed(false);
		groupRepo.save(group);
		
		SubscriberEntity subscriber = new SubscriberEntity();
		subscriber.setId(id.toString());
		subscriber.setGroup(group);
		subscriber.setCurrentOffset(topic.getCurrentOffset());
		subscriber.setAlive(true);
		subscriber.setIp(message.getIp());
		subscriber.setPort(message.getPort());
		subscriber.setType(message.getType());
		
		subscriberRepo.save(subscriber);
		group.getSubscribers().add(subscriber);	
		groupRepo.save(group);
		
		redistributePartitions(group);
		
		System.out.println(groupRepo.getByTopic(topic));
		System.out.println(subscriberRepo.getByGroupAndPartitions(group, 1).getId());
		return id.toString();
	}

	@GetMapping("/poll/{id}")
	public String getNextMessage(@PathVariable("id") String consumerId) {
		
		SubscriberEntity subscriberEntity = subscriberRepo.findById(consumerId).get();
		
		TopicEntity topic =subscriberEntity.getGroup().getTopic();
		
		MessageEntity message= messageRepo.findFirstByIdGreaterThanAndTopicNameAndPartitionNumberInOrderById
				(subscriberEntity.getCurrentOffset(), topic.getTopicName(), subscriberEntity.getPartitions());
		
		if(null==message) {
			return "No messages right now";
		}
		subscriberEntity.setCurrentOffset(message.getId());
		subscriberRepo.save(subscriberEntity);
		return message.getMessage();
		
	}
	
	private void redistributePartitions(GroupEntity group) {
		List<SubscriberEntity> subscribers=group.getSubscribers();
		for(SubscriberEntity sub:group.getSubscribers()) {
			sub.setPartitions(new ArrayList<>());
		}
		for(int i = 0;i<group.getTopic().getPartitions();i++) {
			subscribers.get(i%subscribers.size()).getPartitions().add(i);
		}
		
		subscriberRepo.saveAll(subscribers);
		groupRepo.save(group);
	}
	
	
}
