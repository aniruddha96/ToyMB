package com.toymb.classpath;

import com.toymb.annotations.Subscribe;
import com.toymb.common.NotificationMessage;
import com.toymb.common.ToyMBConsumer;

@Subscribe(group = "g1",topic = "mytopic")
public class MyConsumer implements ToyMBConsumer{

	@Override
	public void consume(NotificationMessage message) {
		// TODO Auto-generated method stub
		
	}

}
