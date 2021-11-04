package com.toymb.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.toymb.classpath.MyTopic;
import com.toymb.classpath.MyTopic2;
import com.toymb.classpath.MyTopic3;


public class MyTopicEnricher2 implements TopicEnricher<MyTopic3>{

	@Value("${enricher}")
	private String en;
	
	@Override
	public void init() {
		System.out.println("In init method of 2");
		System.out.println("value of en "+en);
	}


	@Override
	public void enrich(MyTopic3 t) {
		// TODO Auto-generated method stub
		
	}

}
