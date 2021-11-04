package com.toymb.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.toymb.common.Topic;

@Entity
@Table(name = "subscriber_group")
public class GroupEntity{

	@Id
	@Column(length = 32)
	String groupName;
	 
	@ManyToOne
	TopicEntity topic;
	
	@OneToMany(fetch = FetchType.EAGER)
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
