package com.revature.autosurvey.surveys.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.data.SurveyRepo;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class MessageReceiver {
	
	private static final String QUEUE_NAME = "https://sqs.us-east-1.amazonaws.com/855430746673/SurveyQueue";
	private static final  String DESTINATION_QUEUE = "https://sqs.us-east-1.amazonaws.com/855430746673/AnalyticsQueue";
	private static final  String MESSAGE_ID = "MessageId";
	private MessageSender sender;
	private Message<String> lastReceived; 
	private Survey emptySurvey;
	
	private SurveyRepo repository;
	private ObjectMapper mapper;

	
	public MessageReceiver() {
			super();
			emptySurvey = new Survey();
	}

	public MessageSender getSender() {
		return this.sender;
	}

	@Autowired
	public void setSender(MessageSender sender) {
		this.sender = sender;
	}

	public Message<String> getLastReceived() {
		return lastReceived;
	}

	@Autowired
	public void setRepository(SurveyRepo repository) {
		this.repository = repository;
	}
	
	public SurveyRepo getRepository() {
		return this.repository;
	}

	@Autowired
	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public static String getQueueName() {
		return QUEUE_NAME;
	}

	public static String getDestinationQueue() {
		return DESTINATION_QUEUE;
	}

	public static String getMessageId() {
		return MESSAGE_ID;
	}
	
	@SqsListener(value=QUEUE_NAME, deletionPolicy=SqsMessageDeletionPolicy.ON_SUCCESS)
	public void queueListener(Message<String> message) {		
		log.debug("Survey's queue listener invoked");

		Object headers_Mid = message.getHeaders().get(MESSAGE_ID);
		String messageId = null;
		
		if(headers_Mid != null) {
			messageId = (String) headers_Mid;
		}
		
		log.debug("Message ID Received from Headers: ", messageId);

    	// Extract target survey ID from message and remove extra quotes
    	String sid = message.getPayload().replaceAll(("^\"+|\"+$"), (""));
		log.debug("Payload received: ", sid);
    	
		UUID uid;
    	try {
    		uid = UUID.fromString(sid);
    	}
    	catch(Exception e) {
    		log.warn("Invalid UUID: " + message.getPayload());
    		log.error(e);
    		String response = "Invalid UUID: " + message.getPayload();
			sender.sendObject(response, DESTINATION_QUEUE, messageId);
			return;
    	}

		// Query DB with survey ID
		repository.getByUuid(uid).switchIfEmpty(Mono.just(emptySurvey)).map(survey -> {
			String response = "";
			String responseHeader = message.getHeaders().get(MESSAGE_ID) != null ?
					message.getHeaders().get(MESSAGE_ID).toString() : null;
			
			// Check that survey ID from query matches that in request
			if(uid.equals(survey.getUuid())) {
				// Send survey info back and return
		        try {
		        	response = mapper.writeValueAsString(survey);
		        	sender.sendObject(response, DESTINATION_QUEUE, responseHeader);
					
				} catch (JsonProcessingException e) {
					log.error(e.getMessage());
				}			
		        
		        log.debug("Posted response to queue: {}", response);
				return Mono.just(survey);
			}
			
			// Survey not found
			response = "Survey ID: " + uid + " not found";
	        log.debug("Posted response to queue: {}", response);
			
			sender.sendObject(response, DESTINATION_QUEUE, responseHeader);
			return Mono.empty();
		}).subscribe();
		
		lastReceived = message;
	}
	
}
