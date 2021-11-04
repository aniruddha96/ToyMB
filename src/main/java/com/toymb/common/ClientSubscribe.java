package com.toymb.common;

import java.io.Serializable;

public class ClientSubscribe implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8919040202279028769L;

	String topicName;
	
	String groupName;
	
	String type;
	
	String ip;
	
	int port;

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
	
	
	
}
