package com.toymb.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
	NodeRepository nodeRepo;

	@Autowired
	RestTemplate template;

	@Autowired
	SubscriberRepository subRepo;
	

	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(32);
	
	@PostMapping("/sendmessage")
	public String addMessage(@RequestBody ClientMessage clientMessage) throws Exception {
		if(clientMessage.getId()==null) {
			clientMessage.setId(UUID.randomUUID().toString());
		}
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
				Iterator<NodeEntity> nodes = nodeRepo.findAll().iterator();
				List<String> nodeIds = new ArrayList<String>();
				while(nodes.hasNext()) {
				    NodeEntity element = nodes.next();
				    nodeIds.add(element.getId());
				}
				String owner = Utils.getFirstClosestString(nodeIds, clientMessage.getTopicName());
				NodeEntity ownerNode = nodeRepo.findById(owner).get();
				newTopic.setOwner(ownerNode);
				topicRepo.save(newTopic);
				ownerNode.getTopics().add(newTopic);
				nodeRepo.save(ownerNode);
			}
			TopicEntity topic = topicRepo.findById(clientMessage.getTopicName()).get();
			NodeEntity me = nodeRepo.findById(RuntimeInfo.myId).get();
			if(me.getTopics().contains(topic)) {
				if(messageRepository.findByIdentifier(clientMessage.getId()) == null) {
					MessageEntity entity = new MessageEntity();
					entity.setMessage(msg);
					int hash = clientMessage.getKey().hashCode();
					if(hash<0) {
							hash=hash*-1;
					}
					entity.setIdentifier(clientMessage.getId());
					entity.setPartitionNumber(hash%topic.getPartitions());
					entity.setTopicName(clientMessage.getTopicName());
					entity.setStatus("new");
					messageRepository.save(entity);
					topic.setCurrentOffset(entity.getId());
					topicRepo.save(topic);	
					MessageNotifier notifier = new MessageNotifier(messageRepository, topicRepo, groupRepo, template, subRepo,entity.getId());
					executor.submit(notifier);
				}
				
			}else {
				String requestor = "";
				if(clientMessage.getRequestor()!=null) {
					requestor = clientMessage.getRequestor();
				}
				clientMessage.setRequestor(RuntimeInfo.myAddress);
				for(String peer : RuntimeInfo.myPeers) {
					if(!peer.contentEquals(requestor)) {
						StringBuilder sb = new StringBuilder("http://").append(peer).append("/sendmessage");
						ResponseEntity<String> response = template.postForEntity(sb.toString(), clientMessage, String.class);
						if(!response.getBody().contentEquals("ack")) {
							System.out.println("Acknowledgement not cascaded properly, Something might have went wrong");
						}
					}
				}
			}
			
			
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
