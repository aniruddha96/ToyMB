package com.toymb.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.toymb.classpath.MyTopic;

@Component
public class MyTopicEnricher implements TopicEnricher<MyTopic>{

	@Value("${enricher}")
	private String en;
	
	@Override
	public void init() {
		System.out.println("In init method of 1");
		System.out.println("value of en "+en);
	}

	@Override
	public void enrich(MyTopic t) {
		t.enrich();
		
	}

}
