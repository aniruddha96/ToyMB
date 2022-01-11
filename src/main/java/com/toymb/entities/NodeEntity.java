package com.toymb.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "nodes")
public class NodeEntity {

	@Id
	@Column(length = 64)
	String id;
	
	String ip;
	
	int port;
	
	@OneToMany()
	List<TopicEntity> topics = new ArrayList<TopicEntity>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<TopicEntity> getTopics() {
		return topics;
	}

	public void setTopics(List<TopicEntity> topics) {
		this.topics = topics;
	}

	
	
	
	
}
