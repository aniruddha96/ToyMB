package com.toymb.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.toymb.annotations.Subscribe;
import com.toymb.common.ClientSubscribe;
import com.toymb.common.ToyMBConsumer;

@Component
public class ToyMbClientFactory implements ApplicationContextAware {

	@Value("${toymb.address}")
	String address;
	
	@Value("${toymb.port}")
	int port;
	
	@Value("${server.port}")
	int serverPort;
	
	/*
	 * @Bean public RestTemplate restTemplate(RestTemplateBuilder builder) { return
	 * builder.build(); }
	 * 
	 * @Bean public ToyMBTemplate toyMBTemplate() { ToyMBTemplateImpl impl = new
	 * ToyMBTemplateImpl(); impl.setAddress(this.address); impl.setPort(this.port);
	 * impl.setTemplate(this.template); return impl; }
	 */
	
	@Autowired
	@Qualifier("toyMBInternalTemplate")
	RestTemplate template;
	
	Map<String,ToyMBConsumer> consumerMap = new HashMap<>();
	
	
	public Map<String, ToyMBConsumer> getConsumerMap() {
		return consumerMap;
	}


	public void setConsumerMap(Map<String, ToyMBConsumer> consumerMap) {
		this.consumerMap = consumerMap;
	}

	ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
		initConsumers();
	}


	public void initConsumers(){
		Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(Subscribe.class);
		if (serviceMap!=null && serviceMap.size()>0) {
            for (Object serviceBean : serviceMap.values()) {
            	
                if (serviceBean instanceof ToyMBConsumer) {
                	Subscribe sub = serviceBean.getClass().getAnnotation(Subscribe.class);
                	
                	ClientSubscribe clientSubscribe = new ClientSubscribe();
                	clientSubscribe.setGroupName(sub.group());
                	clientSubscribe.setTopicName(sub.topic());
                	clientSubscribe.setType("push");
                	InetAddress ip = null;
					try {
						ip = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	clientSubscribe.setIp(ip.getHostAddress());
                	clientSubscribe.setPort(serverPort);
                	StringBuilder sb = new StringBuilder("http://").append(address).append(":").append(port).append("/subscribe");
                	ResponseEntity<String> consumberUUId = template.postForEntity(sb.toString(), clientSubscribe, String.class);
                	System.out.println(consumberUUId);
                	consumerMap.put(consumberUUId.getBody(), (ToyMBConsumer) serviceBean);
                }
            }
        }
	}
	

}
