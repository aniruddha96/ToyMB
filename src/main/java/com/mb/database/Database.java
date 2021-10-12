package com.mb.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mb.common.Topic;
import com.mb.server.Message;
import com.mb.server.TopicInfo;

public class Database {

	public static Map<String,TopicInfo> topicInfos = new HashMap<String,TopicInfo>();
	
	public static Map<String, HashMap<Integer, ConcurrentLinkedQueue<Message>>> database = 
			new HashMap<String,HashMap<Integer,ConcurrentLinkedQueue<Message>>>();
	
	
	public static void addMessage(String topicName,String key,Topic message) {
		TopicInfo info = topicInfos.get(topicName);
		Integer partition = key.hashCode()%info.getPartitions();
		HashMap<Integer, ConcurrentLinkedQueue<Message>> topicData=
				database.get(topicName);
		
		if(topicData==null) {
			topicData = new HashMap<Integer, ConcurrentLinkedQueue<Message>>();
			database.put(topicName, topicData);
		}
		
		ConcurrentLinkedQueue<Message> messageList = topicData.get(partition);
		
		if(messageList==null) {
			messageList = new ConcurrentLinkedQueue<Message>();
			topicData.put(partition, messageList);
		}
		Message m = new Message();
		m.setId(0);
		m.setTopic(message);
		m.setTopicName(topicName);
		messageList.add(m);
	}
	
}
