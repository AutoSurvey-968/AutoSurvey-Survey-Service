package com.revature.autosurvey.surveys.sqs;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import org.json.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.data.SurveyRepo;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class MessageReceiver {
	
	public final static String qname = "https://sqs.us-east-1.amazonaws.com/855430746673/SurveyQueue";
	private String destQname = "https://sqs.us-east-1.amazonaws.com/855430746673/AnalyticsQueue";
	private MessageSender sender;
	private Message<String> lastReceived; 
	private Survey emptySurvey;
	
	public SurveyRepo repository;
	public ObjectMapper mapper;

	
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
		return destQname;
	}

	public void setDestQname(String destQname) {
		this.destQname = destQname;
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

		log.debug("Headers received: {}", message.getHeaders());
		System.out.println("Headers received: " + message.getHeaders());
		String req_header = message.getHeaders().get("MessageId").toString();
    	log.debug("Message ID Received: {}", req_header);
    	System.out.println("Message ID Received: " + req_header);
    	
    	String payload = message.getPayload();
		log.debug("Payload received: ", payload);
		System.out.println("Payload received: " + payload);
		
    	// Parse JSON payload and extract target survey ID from message
    	String sid = "";
    	
		try {
	    	JSONObject obj = new JSONObject(payload);
			System.out.println("Survey ID received: " + obj.getString("surveyUuid"));
			sid = obj.getString("surveyUuid");
		} catch (JSONException e1) {
			log.error(e1);
		}
    	
		UUID uid = UUID.fromString(sid);

		// Query DB with survey ID
		repository.getByUuid(uid).switchIfEmpty(Mono.just(emptySurvey)).map(survey -> {
			String response = "";
			
			// Check that survey ID from query matches that in request
			if(uid.equals(survey.getUuid())) {
				// Send survey info back and return
		        try {
		        	response = mapper.writeValueAsString(survey);
		        	sender.sendObject(response, destQname, req_header);
					
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
			
			sender.sendObject(response, destQname, req_header);
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
