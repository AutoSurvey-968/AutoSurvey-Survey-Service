package com.revature.autosurvey.surveys.sqs;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

public class MessageReceiverTests {
	
	@Mock
	private MessageSender sender;
	
	@Mock
	private QueueMessagingTemplate queueMessagingTemplate;
	
	@Mock
	private SurveyRepo repo;
	
	private String qname = "TestQueue.aws";
	private String payload = UUID.randomUUID().toString();
	private String req_header = "1234-5678-901";
	
	private Message<String> message;
		
	private Survey emptySurvey;
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		this.message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", req_header)
				.build();
		emptySurvey = new Survey();
	}
	
	@Test
    public void receiveMessage_methodAnnotatedWithSqsListenerAnnotation_methodInvokedForIncomingMessage() throws Exception {
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", MessageReceiver.class);
        applicationContext.refresh();
        

        MessageReceiver messageHandler = applicationContext.getBean(MessageReceiver.class);
        messageHandler.setSurveyService(repo);
        messageHandler.setSender(sender);

        Mockito.when(messageHandler.repository.getByUuid(Mockito.any())).thenReturn(Mono.just(emptySurvey));
        Mockito.doNothing().when(messageHandler.getSender()).sendObject(payload, qname, req_header);

        messageHandler.queueListener(message);
        
        assertEquals(message, messageHandler.getLastReceivedMessage());
        
        applicationContext.close();
    }
}
