package com.mb;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.common.EnrichableTopic;
import com.mb.common.MyTopicEnricher;
import com.mb.common.Topic;
import com.mb.common.TopicEnricher;
import com.mb.database.Database;
import com.mb.entities.TopicEntity;
import com.mb.repositories.TopicRepository;
import com.mb.server.TopicInfo;

@SpringBootApplication
public class ToyMbApplication implements CommandLineRunner, ApplicationContextAware{

	@Value("${test}")
	private String topicName;

	@Value("${test2}")
	private String topicName2;

	@Value("${enricher}")
	private String en;

	public String getTopicName2() {
		return topicName2;
	}

	public void setTopicName2(String topicName2) {
		this.topicName2 = topicName2;
	}

	public String getTopicName() {
		return topicName;
	}

	public static Map<String, Class> topicMapping = new HashMap<String, Class>();

	public static Map<String, TopicEnricher> enricherMapping = new HashMap<String, TopicEnricher>();

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public static void main(String[] args) {
		SpringApplication.run(ToyMbApplication.class, args);
	}

	@Autowired
	TopicRepository topicRepo;

	@Override
	public void run(String... args) throws Exception {

		Object c = Class.forName(topicName).newInstance();
		System.out.println(c instanceof EnrichableTopic);

		if (c instanceof EnrichableTopic) {
			EnrichableTopic topic = (EnrichableTopic) c;
			topicMapping.put("name1", Class.forName(topicName));
			Database.topicInfos.put("name1", new TopicInfo("name1", 100));
			topicRepo.save(new TopicEntity("name1", 100, topicName,0));

		} else if (c instanceof Topic) {
			topicMapping.put("name1", Class.forName(topicName));
			Database.topicInfos.put("name1", new TopicInfo("name1", 100));
		}

		Object c2 = Class.forName(topicName2).newInstance();
		System.out.println(c2 instanceof EnrichableTopic);

		if (c2 instanceof EnrichableTopic) {
			EnrichableTopic topic = (EnrichableTopic) c2;
			topicMapping.put("name2", Class.forName(topicName2));
			Database.topicInfos.put("name2", new TopicInfo("name2", 100));
		} else if (c2 instanceof Topic) {
			topicMapping.put("name2", Class.forName(topicName2));
			Database.topicInfos.put("name2", new TopicInfo("name2", 100));
			topicRepo.save(new TopicEntity("name2", 100, topicName2,0));
		}

		extracted(en);
		//extracted("com.mb.common.MyTopicEnricher2");
		System.out.println(topicMapping);
		System.out.println(enricherMapping);
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
