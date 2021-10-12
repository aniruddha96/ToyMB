package com.mb.common;

import java.io.Serializable;

public class ClientSubscribe implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8919040202279028769L;

	String topicName;
	
	String groupName;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
	
}
