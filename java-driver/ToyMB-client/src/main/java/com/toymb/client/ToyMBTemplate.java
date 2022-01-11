package com.toymb.client;

import java.io.Serializable;

public interface ToyMBTemplate {

	void send(String topicName, String key, Serializable object);
}
