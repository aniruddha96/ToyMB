package com.toymb.classpath;

import com.toymb.common.EnrichableTopic;
import com.toymb.common.Topic;

public class MyTopic2 implements Topic{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8961621155675291107L;
	String name;
	String id ;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "MyTopic2 [name=" + name + ", id=" + id + "]";
	}
}
