package com.revature.autosurvey.surveys.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

class MessageSenderTests {
	
	@InjectMocks
	private MessageSender sender;
	
	@Mock
	private QueueMessagingTemplate queueMessagingTemplate;
	
	private String qname = "TestQueue.aws";
	private String payload = "TestID9876-5432-10";
	private String req_header = "1234-5678-901";
	
	private Message<String> message;
		
	@BeforeAll
	static void beforeAll() {
		
	}
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		this.message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", req_header)
				.build();
	}
	
	@Test
	void testSendObject() {
		Mockito.doNothing().when(sender.getQueueMessagingTemplate()).send(qname, this.message);
		
		sender.sendObject(this.message.getPayload(), qname, req_header);
		
		assertNotNull(qname);
		assertNotNull(this.message);
	}
	
	
}
