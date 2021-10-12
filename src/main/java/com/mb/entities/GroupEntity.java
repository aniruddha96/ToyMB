package com.mb.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.mb.common.Topic;

@Entity
@Table(name = "groups")
public class GroupEntity{

	@Id
	String groupName;
	 
	@ManyToOne
	TopicEntity topic;
	
	@OneToMany
	List<SubscriberEntity> subscribers = new ArrayList<SubscriberEntity>();
	
	boolean isBeingServed;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isBeingServed() {
		return isBeingServed;
	}

	public void setBeingServed(boolean isBeingServed) {
		this.isBeingServed = isBeingServed;
	}

	public TopicEntity getTopic() {
		return topic;
	}

	public void setTopic(TopicEntity topic) {
		this.topic = topic;
	}

	public List<SubscriberEntity> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(List<SubscriberEntity> subscribers) {
		this.subscribers = subscribers;
	}

	
	
	
}
