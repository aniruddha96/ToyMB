package com.mb.classpath;

import com.mb.common.EnrichableTopic;

public class MyTopic implements EnrichableTopic{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2522334170434363084L;
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
	public void enrich() {
		name=name+" enriched";
		
	}
	
	@Override
	public String toString() {
		return "MyTopic [name=" + name + ", id=" + id + "]";
	}
	
	
}
