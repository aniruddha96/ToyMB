package com.toymb.workers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.toymb.common.NotificationMessage;
import com.toymb.common.Topic;
import com.toymb.entities.GroupEntity;
import com.toymb.entities.MessageEntity;
import com.toymb.entities.SubscriberEntity;
import com.toymb.entities.TopicEntity;
import com.toymb.repositories.GroupRepository;
import com.toymb.repositories.MessageRepository;
import com.toymb.repositories.SubscriberRepository;
import com.toymb.repositories.TopicRepository;

public class MessageNotifier implements Runnable {

	MessageRepository messageRepository;

	TopicRepository topicRepo;

	GroupRepository groupRepo;

	RestTemplate template;

	SubscriberRepository subRepo;

	Long msgId;

	public MessageNotifier(MessageRepository messageRepository, TopicRepository topicRepo, GroupRepository groupRepo,
			RestTemplate template, SubscriberRepository subRepo, Long msgId) {
		super();
		this.messageRepository = messageRepository;
		this.topicRepo = topicRepo;
		this.groupRepo = groupRepo;
		this.template = template;
		this.msgId = msgId;
		this.subRepo=subRepo;
	}

	@Override
	public void run() {
		MessageEntity msg = messageRepository.findById(msgId).get();
		TopicEntity topicEntity = topicRepo.findById(msg.getTopicName()).get();
		for (GroupEntity group : groupRepo.getByTopic(topicEntity)) {
			SubscriberEntity subscriber = subRepo.getByGroupAndPartitions(group, msg.getPartitionNumber());
			if (subscriber.getType().contentEquals("push")) {
				StringBuilder sb = new StringBuilder("http://").append(subscriber.getIp()).append(":")
						.append(subscriber.getPort()).append("/receivemessage");
				NotificationMessage sendMessage = new NotificationMessage();
				sendMessage.setEntity(msg.getMessage());
				sendMessage.setForSubscriber(subscriber.getId());
				sendMessage.setMessageId(msg.getId());
				sendMessage.setTopicName(topicEntity.getTopicName());
				template.postForEntity(sb.toString(), sendMessage, String.class);

			}

		}

	}

}
