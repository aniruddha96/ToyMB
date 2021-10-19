package com.mb.common;

import org.springframework.stereotype.Component;


public interface TopicEnricher<T extends EnrichableTopic> {

	void init() throws Exception;
	
	void enrich(T t) throws Exception;
	
}
