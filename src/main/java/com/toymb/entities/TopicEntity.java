package com.toymb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "topic")
public class TopicEntity {

	@Id
	@Column(length = 16)
	String topicName;
	
	int partitions;
	
	String className;
	
	long currentOffset;
	
	@ManyToOne
	NodeEntity owner;

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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public TopicEntity() {
		
	}

	public long getCurrentOffset() {
		return currentOffset;
	}

	public void setCurrentOffset(long currentOffset) {
		this.currentOffset = currentOffset;
	}

	public TopicEntity(String topicName, int partitions, String className, long currentOffset) {
		super();
		this.topicName = topicName;
		this.partitions = partitions;
		this.className = className;
		this.currentOffset = currentOffset;
	}

	public NodeEntity getOwner() {
		return owner;
	}

	public void setOwner(NodeEntity owner) {
		this.owner = owner;
	}
	
	
}
