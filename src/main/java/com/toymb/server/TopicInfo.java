package com.toymb.server;

public class TopicInfo {

	String topicName;
	int partitions;
	public TopicInfo(String topicName, int partitions) {
		super();
		this.topicName = topicName;
		this.partitions = partitions;
	}
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
