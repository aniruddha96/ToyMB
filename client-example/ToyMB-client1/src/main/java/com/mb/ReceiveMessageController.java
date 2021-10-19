package com.mb;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mb.common.ClientSubscribe;

public class ReceiveMessageController {

	@PostMapping("/receivemessage")
	public String subscribe(@RequestBody String message) {
		System.out.println(message);
		return "ack";
	}
}
