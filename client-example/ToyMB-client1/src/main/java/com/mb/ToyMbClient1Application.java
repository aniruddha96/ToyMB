package com.mb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mb.common.ClientSubscribe;

@SpringBootApplication
public class ToyMbClient1Application implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ToyMbClient1Application.class, args);
	}

	@Autowired
	RestTemplate template;
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	
	@Override
	public void run(String... args) throws Exception {
		//send ClientSubscribe object witj ip and port etc.
		ClientSubscribe subscriber = new ClientSubscribe();
		subscriber.setGroupName("g1");
		subscriber.setIp("localhost");
		subscriber.setPort(8081);
		subscriber.setTopicName("name1");
		subscriber.setType("push");
		
		ResponseEntity<String> uuid=template.postForEntity("http://localhost:8080/subscribe", subscriber, String.class);
		
		System.out.println(uuid.getBody().toString());
		
	}
	

}
