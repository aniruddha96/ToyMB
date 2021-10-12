package com.mb.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "subscribers")
public class SubscriberEntity {

	@Id
	String id;	
	
	@ManyToOne
	GroupEntity group;
	
	long currentOffset;
	
	@ElementCollection
	List<Integer> partitions = new ArrayList<Integer>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GroupEntity getGroup() {
		return group;
	}

	public void setGroup(GroupEntity group) {
		this.group = group;
	}

	public long getCurrentOffset() {
		return currentOffset;
	}

	public void setCurrentOffset(long currentOffset) {
		this.currentOffset = currentOffset;
	}

	public List<Integer> getPartitions() {
		return partitions;
	}

	public void setPartitions(List<Integer> partitions) {
		this.partitions = partitions;
	}
	
	
}
