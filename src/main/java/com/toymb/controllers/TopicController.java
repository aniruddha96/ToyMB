package com.toymb.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.toymb.common.ClientMessage;
import com.toymb.common.CreateTopic;
import com.toymb.entities.NodeEntity;
import com.toymb.entities.TopicEntity;
import com.toymb.repositories.NodeRepository;
import com.toymb.repositories.TopicRepository;
import com.toymb.server.Utils;

@RestController()
public class TopicController {

	@Autowired
	TopicRepository repo;
	
	@Autowired
	NodeRepository nodeRepo;
	
	@PostMapping("/advertise")
	public String addMessage(@RequestBody CreateTopic clientMessage) throws Exception {
		Optional<TopicEntity> topic = repo.findById(clientMessage.getTopicName());
		if(!topic.isPresent()) {
			TopicEntity newTopic = new TopicEntity();
			newTopic.setCurrentOffset(0);
			newTopic.setPartitions(clientMessage.getPartitions());
			newTopic.setTopicName(clientMessage.getTopicName());
			Iterator<NodeEntity> nodes = nodeRepo.findAll().iterator();
			List<String> nodeIds = new ArrayList<String>();
			while(nodes.hasNext()) {
			    NodeEntity element = nodes.next();
			    nodeIds.add(element.getId());
			}
			String owner = Utils.getFirstClosestString(nodeIds, clientMessage.getTopicName());
			NodeEntity ownerNode = nodeRepo.findById(owner).get();
			newTopic.setOwner(ownerNode);
			repo.save(newTopic);
			ownerNode.getTopics().add(newTopic);
			nodeRepo.save(ownerNode);
		}
		return "ack";
	}
	
}
