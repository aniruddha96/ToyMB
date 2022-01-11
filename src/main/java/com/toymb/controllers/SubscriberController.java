package com.toymb.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.toymb.common.ClientSubscribe;
import com.toymb.entities.GroupEntity;
import com.toymb.entities.MessageEntity;
import com.toymb.entities.NodeEntity;
import com.toymb.entities.SubscriberEntity;
import com.toymb.entities.TopicEntity;
import com.toymb.repositories.GroupRepository;
import com.toymb.repositories.MessageRepository;
import com.toymb.repositories.NodeRepository;
import com.toymb.repositories.SubscriberRepository;
import com.toymb.repositories.TopicRepository;
import com.toymb.server.RuntimeInfo;
import com.toymb.server.Utils;

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
	
	@Autowired
	NodeRepository nodeRepo;
	
	@Autowired
	RestTemplate template;
	
	@PostMapping("/subscribe")
	public String subscribe(@RequestBody ClientSubscribe message) throws Exception {
		if(message.getId()==null) {
			message.setId(UUID.randomUUID().toString());
		}
		String retval = message.getId();
		Optional<TopicEntity> topicOptional = topicRepo.findById(message.getTopicName());
		if(!topicOptional.isPresent()) {
			TopicEntity newTopic = new TopicEntity();
			newTopic.setCurrentOffset(0);
			newTopic.setPartitions(100);
			newTopic.setTopicName(message.getTopicName());
			Iterator<NodeEntity> nodes = nodeRepo.findAll().iterator();
			List<String> nodeIds = new ArrayList<String>();
			while(nodes.hasNext()) {
			    NodeEntity element = nodes.next();
			    nodeIds.add(element.getId());
			}
			String owner = Utils.getFirstClosestString(nodeIds, message.getTopicName());
			NodeEntity ownerNode = nodeRepo.findById(owner).get();
			newTopic.setOwner(ownerNode);
			topicRepo.save(newTopic);
			ownerNode.getTopics().add(newTopic);
			nodeRepo.save(ownerNode);
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
		group.setBeingServed(true);
		groupRepo.save(group);
		
		NodeEntity me = nodeRepo.findById(RuntimeInfo.myId).get();
		if(me.getTopics().contains(topic)) {
			if (!subscriberRepo.findById(message.getId()).isPresent()) {
				SubscriberEntity subscriber = new SubscriberEntity();
				subscriber.setId(message.getId());
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
			}	
		}else {
			String requestor = "";
			if(message.getRequestor()!=null) {
				requestor = message.getRequestor();
			}
			message.setRequestor(RuntimeInfo.myAddress);
			for(String peer : RuntimeInfo.myPeers) {
				if(!peer.contentEquals(requestor)) {
					StringBuilder sb = new StringBuilder("http://").append(peer).append("/subscribe");
					System.out.println(sb.toString());
					ResponseEntity<String> response = template.postForEntity(sb.toString(), message, String.class);
					if(!response.getBody().contentEquals(retval)) {
						System.out.println("Returned id is new, Something might have went wrong");
					}
				}
			}
		}

		return retval;
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
