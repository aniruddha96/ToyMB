package com.toymb.client;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toymb.common.ClientMessage;

@Component
public class ToyMBTemplateImpl implements ToyMBTemplate{

	@Value("${toymb.address}")
	String address;
	
	@Value("${toymb.port}")
	int port;
	
	@Value("${server.port}")
	int serverPort;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	@Qualifier("toyMBInternalTemplate")
	RestTemplate template;
	
	@Override
	public void send(String topicName, String key, Serializable object) {
		ClientMessage message = new ClientMessage();
		try {
			message.setBody(mapper.writeValueAsString(object));
			message.setKey(key);
			message.setTopicName(topicName);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder("http://").append(address).append(":").append(port).append("/sendmessage");
    	ResponseEntity<String> ack = template.postForEntity(sb.toString(), message, String.class);

		
	}

}
