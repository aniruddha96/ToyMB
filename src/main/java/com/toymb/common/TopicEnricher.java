package com.toymb.common;


public interface TopicEnricher<T extends EnrichableTopic> {

	void init() throws Exception;
	
	void enrich(T t) throws Exception;
	
}
