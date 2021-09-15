package com.revature.autosurvey.surveys.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	@Mock
	private ObjectMapper mapper;
	private Message<String> message;
		
	private Survey emptySurvey;
	private Survey validSurvey;
	
	private String requestHeader = UUID.randomUUID().toString();
	private String validId;
	private String noMatch;
	private String invalidId;
	private String response;
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		validId = "d50ca970-14ac-11ec-a00f-df792d7e6b27";
		noMatch = UUID.randomUUID().toString();
		invalidId = "1234-5678-901";

		emptySurvey = new Survey();
		validSurvey = new Survey();
		validSurvey.setUuid(UUID.fromString(validId));

	}
	
	@Test
    void testQueueListenerValidUUIDNoMatch() {
		this.message = MessageBuilder
				.withPayload(noMatch)
				.setHeader("MessageId", requestHeader).build();
		
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", MessageReceiver.class);
        applicationContext.refresh();
        

        MessageReceiver messageHandler = applicationContext.getBean(MessageReceiver.class);
        messageHandler.setRepository(repo);
        messageHandler.setSender(sender);

        Mockito.when(messageHandler.getRepository()
        		.getByUuid(Mockito.any()))
        .thenReturn(Mono.just(emptySurvey));
		response = "Survey ID: " + noMatch + " not found";

        Mockito.doNothing().when(messageHandler.getSender()).
        sendObject(response, MessageReceiver.getDestinationQueue(), requestHeader);

        messageHandler.queueListener(message);
        
        Mockito.verify(messageHandler.getRepository()).getByUuid(UUID.fromString(noMatch));
        verify(messageHandler.getSender(), times(1)).
        sendObject(response, MessageReceiver.getDestinationQueue(), requestHeader);
        
        applicationContext.close();
    }
	
	@Test
    void testQueueListenerValidUUIDWithMatch() {
		message = MessageBuilder
				.withPayload(validId)
				.setHeader("MessageId", requestHeader)
				.build();
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", MessageReceiver.class);
        applicationContext.refresh();       
        
        MessageReceiver messageHandler = applicationContext.getBean(MessageReceiver.class);
        messageHandler.setRepository(repo);
        messageHandler.setSender(sender);
        messageHandler.setMapper(mapper);

        Mockito.when(messageHandler.getRepository().getByUuid(Mockito.any())).thenReturn(Mono.just(validSurvey));
        response = Jackson.toJsonString(validSurvey);
        		
        Mockito.doNothing().when(messageHandler.getSender()).
        sendObject(response, MessageReceiver.getDestinationQueue(), requestHeader);

        messageHandler.queueListener(message);
        
        Mockito.verify(messageHandler.getRepository()).getByUuid(UUID.fromString(validId));
        verify(messageHandler.getSender(), times(1))
        .sendObject(Mockito.any(), Mockito.anyString(), Mockito.anyString());
        
        assertEquals(validId, validSurvey.getUuid().toString(), 
        		"The survey UUID searched for is what we included in the response");
        
        applicationContext.close();
 
    }
	
	@Test
    void testQueueListenerInvalidUUID() {
		message = MessageBuilder
				.withPayload(invalidId)
				.setHeader("MessageId", requestHeader)
				.build();
		
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerPrototype("incomingMessageHandler", MessageReceiver.class);
        applicationContext.refresh();
        

        MessageReceiver messageHandler = applicationContext.getBean(MessageReceiver.class);
        messageHandler.setRepository(repo);
        messageHandler.setSender(sender);

        Mockito.when(messageHandler.getRepository().getByUuid(Mockito.any())).thenReturn(Mono.just(emptySurvey));
		response = "Invalid UUID: " + message.getPayload();
        
        Mockito.doNothing().when(messageHandler.getSender())
        .sendObject(invalidId, MessageReceiver.getDestinationQueue(), requestHeader);

        messageHandler.queueListener(message);
        
        Mockito.verifyNoInteractions(messageHandler.getRepository());
        verify(messageHandler.getSender(), times(1))
        .sendObject(response, MessageReceiver.getDestinationQueue(), requestHeader);
        
        applicationContext.close();
    }
	
	@Test
	void testGetLastReceivedNewMessage() {
		message = MessageBuilder
				.withPayload(validId)
				.setHeader("MessageId", requestHeader)
				.build();
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerPrototype("incomingMessageHandler", MessageReceiver.class);
        applicationContext.refresh();
        

        MessageReceiver messageHandler = applicationContext.getBean(MessageReceiver.class);
        messageHandler.setRepository(repo);
        messageHandler.setSender(sender);

        Mockito.when(messageHandler.getRepository().
        		getByUuid(Mockito.any())).thenReturn(Mono.just(emptySurvey));
        
        Mockito.doNothing().when(messageHandler.getSender())
        .sendObject(validId, MessageReceiver.getDestinationQueue(), requestHeader);

        messageHandler.queueListener(message);
        
        assertEquals(message, messageHandler.getLastReceived());
        
        applicationContext.close();
	}
	
	@Test
	void testGetLastReceivedNoMessages() {
		MessageReceiver receiver = Mockito.mock(MessageReceiver.class);
		
		verify(receiver, times(0)).queueListener(message);
		
		assertNull(receiver.getLastReceived(), "New receiver should have no messages");
	}
}
