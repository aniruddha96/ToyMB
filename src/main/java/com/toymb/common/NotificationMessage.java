package com.toymb.common;

import java.io.Serializable;

public class NotificationMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8997567750796141310L;
	String topicName;
	String forSubscriber;
	long messageId;
	String entity;
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getForSubscriber() {
		return forSubscriber;
	}
	public void setForSubscriber(String forSubscriber) {
		this.forSubscriber = forSubscriber;
	}
	public long getMessageId() {
		return messageId;
	}
	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	
	
}
