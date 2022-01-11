package com.toymb.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.toymb.common.NotificationMessage;
import com.toymb.common.ToyMBConsumer;


@RestController
public class ReceiveMessageController {

	
	@Autowired
	ToyMbClientFactory factory;

	@PostMapping("/receivemessage")
	public String subscribe(@RequestBody NotificationMessage message) {
		ToyMBConsumer consumer = factory.getConsumerMap().get(message.getForSubscriber());
		consumer.consume(message);
		return "ack"+message.getMessageId();
	}
}
