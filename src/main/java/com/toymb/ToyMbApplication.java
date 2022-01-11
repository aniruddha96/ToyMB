package com.toymb;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toymb.common.EnrichableTopic;
import com.toymb.common.Topic;
import com.toymb.common.TopicEnricher;
import com.toymb.database.Database;
import com.toymb.entities.NodeEntity;
import com.toymb.entities.TopicEntity;
import com.toymb.repositories.NodeRepository;
import com.toymb.repositories.TopicRepository;
import com.toymb.server.RuntimeInfo;
import com.toymb.server.TopicInfo;

@SpringBootApplication
public class ToyMbApplication implements CommandLineRunner, ApplicationContextAware{

	@Value("${server.port}")
	int serverPort;
	
	@Value("#{'${topics}'.split(',')}") 
	List<String> myTopics;
	
	@Value("#{'${peers}'.split(',')}") 
	List<String> myPeers;

	@Value("${node.name}")
	String name;
	
	public static Map<String, Class> topicMapping = new HashMap<String, Class>();

	public static Map<String, TopicEnricher> enricherMapping = new HashMap<String, TopicEnricher>();

	public static void main(String[] args) {
		SpringApplication.run(ToyMbApplication.class, args);
	}

	@Autowired
	TopicRepository topicRepo;

	@Autowired
	NodeRepository nodeRepo;
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		RuntimeInfo.myPeers=myPeers;
		String id = UUID.randomUUID().toString();

		RuntimeInfo.myId=id;
		NodeEntity me = new NodeEntity();
		me.setId(id);
		
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	me.setIp(ip.getHostAddress());
    	me.setPort(serverPort);
    	
    	nodeRepo.save(me);
    	RuntimeInfo.myAddress = new StringBuilder().append(name).append(":").append(serverPort).toString();
    	
		System.out.println("My address : "+ RuntimeInfo.myAddress);
    	if(myTopics!=null) {
    		for(String topicName : myTopics) {
    			TopicEntity newTopic = new TopicEntity();
				newTopic.setCurrentOffset(0);
				newTopic.setPartitions(100);
				newTopic.setTopicName(topicName);
				newTopic.setOwner(me);
				topicRepo.save(newTopic);
				me.getTopics().add(newTopic);
				nodeRepo.save(me);
    		}
    	}
		/*
		 * Object c = Class.forName(topicName).newInstance(); System.out.println(c
		 * instanceof EnrichableTopic);
		 * 
		 * if (c instanceof EnrichableTopic) { EnrichableTopic topic = (EnrichableTopic)
		 * c; topicMapping.put("name1", Class.forName(topicName));
		 * Database.topicInfos.put("name1", new TopicInfo("name1", 100));
		 * topicRepo.save(new TopicEntity("name1", 100, topicName,0));
		 * 
		 * } else if (c instanceof Topic) { topicMapping.put("name1",
		 * Class.forName(topicName)); Database.topicInfos.put("name1", new
		 * TopicInfo("name1", 100)); }
		 * 
		 * Object c2 = Class.forName(topicName2).newInstance(); System.out.println(c2
		 * instanceof EnrichableTopic);
		 * 
		 * if (c2 instanceof EnrichableTopic) { EnrichableTopic topic =
		 * (EnrichableTopic) c2; topicMapping.put("name2", Class.forName(topicName2));
		 * Database.topicInfos.put("name2", new TopicInfo("name2", 100)); } else if (c2
		 * instanceof Topic) { topicMapping.put("name2", Class.forName(topicName2));
		 * Database.topicInfos.put("name2", new TopicInfo("name2", 100));
		 * topicRepo.save(new TopicEntity("name2", 100, topicName2,0)); }
		 * 
		 * extracted(en); //extracted("com.mb.common.MyTopicEnricher2");
		 * System.out.println(topicMapping); System.out.println(enricherMapping);
		 */
		
		
	}

	private void extracted(String en) throws Exception {
		Object cl = Class.forName(en).newInstance();
		System.out.println(cl instanceof TopicEnricher);
		Class cx = Class.forName(en);
		if (cl instanceof TopicEnricher) {
			TopicEnricher topicEnricher = (TopicEnricher) cl;
			System.out.println(cx);
			String[] beanNames =applicationContext.getBeanNamesForType(cx);
			System.out.println(beanNames.length);
			if(beanNames!=null && beanNames.length==1) {
				String name=applicationContext.getBeanNamesForType(cx)[0];
				topicEnricher = (TopicEnricher) applicationContext.getBean(name);
			}else {
				System.out.println("Multiple Beans found");
			}
			topicEnricher.init();
			enricherMapping.put("name1", topicEnricher);
		}
	}

	ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
		
	}
	

}
