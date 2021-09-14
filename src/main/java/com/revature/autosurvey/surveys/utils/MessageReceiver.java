package com.revature.autosurvey.surveys.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;
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
	
	private final static String qname = "https://sqs.us-east-1.amazonaws.com/855430746673/SurveyQueue";
	private final static String destQName = "https://sqs.us-east-1.amazonaws.com/855430746673/AnalyticsQueue";
	private MessageSender sender;
	private Message<String> lastReceived; 
	private Survey emptySurvey;
	
	private SurveyRepo repository;
	private ObjectMapper mapper;

	
	public MessageReceiver() {
			super();
			emptySurvey = new Survey();
	}

	@Autowired
	public void setSender(MessageSender sender) {
		this.sender = sender;
	}
	
	@Autowired
	public void setSurveyService(SurveyRepo repository) {
		this.repository = repository;
	}
	
	@Autowired
	public void setObjectMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	public String getDestQname() {
		return destQName;
	}

	public Message<String> getLastReceived() {
		return lastReceived;
	}

	public void setLastReceived(Message<String> lastReceived) {
		this.lastReceived = lastReceived;
	}

	public MessageSender getSender() {
		return sender;
	}

	public SurveyRepo getRepository() {
		return repository;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	@SqsListener(value= qname, deletionPolicy=SqsMessageDeletionPolicy.ON_SUCCESS)
	public void queueListener(Message<String> message) {		
		log.debug("Survey Queue listener invoked");
		System.out.println("Survey Queue listener invoked");

		String messageHeader = null;
		log.debug("Headers received: {}", message.getHeaders());
		System.out.println("Headers received: " + message.getHeaders());
		
		if(null != message.getHeaders().get("MessageId")) {
			messageHeader = message.getHeaders().get("MessageId").toString();
	    	log.debug("Message ID Received: {}", messageHeader);
	    	System.out.println("Message ID Received: " + messageHeader);
		}
    	
    	String payload = message.getPayload();
		log.debug("Payload received: ", payload);
		System.out.println("Payload received: " + payload);
		
    	// Extract target survey ID from message
    	String sid = message.getPayload();
    	UUID uid;
    	try {
    		uid = UUID.fromString(sid);
    	}
    	catch(Exception e) {
    		log.warn("Invalid UUID");
    		log.error(e);
    		String response = "Invalid UUID";
			sender.sendObject(response, destQName, messageHeader);
			return;
    	}

		// Query DB with survey ID
		repository.getByUuid(uid).switchIfEmpty(Mono.just(emptySurvey)).map((survey) -> {
			String response = "";
			String responseHeader = message.getHeaders().get("MessageId") != null ?
					message.getHeaders().get("MessageId").toString() : null;
			
			// Check that survey ID from query matches that in request
			if(uid.equals(survey.getUuid())) {
				// Send survey info back and return
		        try {
		        	response = mapper.writeValueAsString(survey);
		        	sender.sendObject(response, destQName, responseHeader);
					
				} catch (JsonProcessingException e) {
					log.error(e.getMessage());
				}			
		        
		        log.debug("Posted response to queue: {}", response);
		        System.out.println("Posted response to queue: " + response);
				return Mono.just(survey);
			}
			
			// Survey not found
			response = "Survey ID: " + uid + " not found";
	        log.debug("Posted response to queue: {}", response);
	        System.out.println("Posted response to queue: " + response);
			
			sender.sendObject(response, destQName, responseHeader);
			return Mono.empty();
		}).subscribe();
		
		lastReceived = message;
	}

	public Message<String> getLastReceivedMessage() {
		if(lastReceived == null) {
			log.debug("No messages have been received");
			System.out.println("No messages have been received");
			return null;
		}
		
		return lastReceived;
	}
	
}
