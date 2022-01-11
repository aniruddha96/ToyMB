package com.toymb.common;

import java.io.Serializable;

public class ClientMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6602433061426462007L;
	
	String id;
	String topicName;
	String key;
	String body;
	String requestor;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "TopicInstance [topicName=" + topicName + ", body=" + body + "]";
	}
	
	
}
