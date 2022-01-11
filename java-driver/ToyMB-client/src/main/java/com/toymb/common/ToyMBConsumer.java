package com.toymb.common;

import org.springframework.stereotype.Component;


public interface ToyMBConsumer {

	public void consume(NotificationMessage message);
}
