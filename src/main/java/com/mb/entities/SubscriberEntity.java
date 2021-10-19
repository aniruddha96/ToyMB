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
	
	String type;
	
	String ip;
	
	int port;
	
	boolean isAlive;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

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
