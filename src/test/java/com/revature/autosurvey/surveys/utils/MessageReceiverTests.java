package com.revature.autosurvey.surveys.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.data.SurveyRepo;

import reactor.core.publisher.Mono;

class MessageReceiverTests {
	
	@Mock
	private MessageSender sender;
	
	@Mock
	private QueueMessagingTemplate queueMessagingTemplate;
	
	@Mock
	private SurveyRepo repo;
	
	private String payload = UUID.randomUUID().toString();
	private String requestHeader = "1234-5678-901";
	private String response = "Survey ID: " + payload + " not found";
	
	private Message<String> message;
		
	private Survey emptySurvey;
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		this.message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", requestHeader)
				.build();
		emptySurvey = new Survey();
	}
	
	@Test
    void testQueueListener() {
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", MessageReceiver.class);
        applicationContext.refresh();
        

        MessageReceiver messageHandler = applicationContext.getBean(MessageReceiver.class);
        messageHandler.setRepository(repo);
        messageHandler.setSender(sender);

        Mockito.when(messageHandler.getRepository().getByUuid(Mockito.any())).thenReturn(Mono.just(emptySurvey));
        Mockito.doNothing().when(messageHandler.getSender()).
        sendObject(payload, MessageReceiver.getDestinationQueue(), requestHeader);

        messageHandler.queueListener(message);
        
        Mockito.verify(messageHandler.getRepository()).getByUuid(UUID.fromString(payload));
        verify(messageHandler.getSender(), times(1)).
        sendObject(response, MessageReceiver.getDestinationQueue(), requestHeader);
        applicationContext.close();
    }
	
	@Test
	void testGetLastReceivedMessage() {
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", MessageReceiver.class);
        applicationContext.refresh();
        

        MessageReceiver messageHandler = applicationContext.getBean(MessageReceiver.class);
        messageHandler.setRepository(repo);
        messageHandler.setSender(sender);

        Mockito.when(messageHandler.getRepository().
        		getByUuid(Mockito.any())).thenReturn(Mono.just(emptySurvey));
        
        Mockito.doNothing().when(messageHandler.getSender()).
        sendObject(payload, MessageReceiver.getDestinationQueue(), requestHeader);

        messageHandler.queueListener(message);
        
        assertEquals(message, messageHandler.getLastReceivedMessage());
        
        applicationContext.close();
	}
}
