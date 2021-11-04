package com.toymb.common;

import java.io.Serializable;

public class CreateTopic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String topicName;
	int partitions;
	
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public int getPartitions() {
		return partitions;
	}
	public void setPartitions(int partitions) {
		this.partitions = partitions;
	}
	
	
	

}
