package com.revature.autosurvey.surveys.sqs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.EnableAsync;
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
	
	public final String qname = "https://sqs.us-east-1.amazonaws.com/855430746673/SurveyQueue";
	public String destQname = "https://sqs.us-east-1.amazonaws.com/855430746673/AnalyticsQueue";
	public MessageSender sender;
	public List<Message<String>> receivedMsgs; 
	public Survey emptySurvey;
	
	public SurveyRepo repository;
	public ObjectMapper mapper;

	
	public MessageReceiver() {
			super();
			receivedMsgs = new ArrayList<Message<String>>();
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
	
	@SqsListener(value= qname, deletionPolicy=SqsMessageDeletionPolicy.ON_SUCCESS)
	public void queueListener(Message<String> message) {		
		log.debug("Survey Queue listener invoked");
		System.out.println("Survey Queue listener invoked");
		log.debug("Headers received: {}", message.getHeaders());
		System.out.println("Headers received: " + message.getHeaders());
    	String req_header = message.getHeaders().get("MessageId").toString();
    	log.debug("Request ID Received: {}", req_header);
    	System.out.println("Request ID Received: " + req_header);
    	
    	String payload = message.getPayload();
    	receivedMsgs.add(message);
    	
    	// Extract target survey ID from received message
		UUID sid = UUID.fromString(payload);
		log.debug("Payload received: " + payload);
		System.out.println("Payload received: " + payload);

		// Query DB with survey ID
		repository.getByUuid(sid).switchIfEmpty(Mono.just(emptySurvey)).map(survey -> {
			String response = "";
			
			// Check that survey ID from query matches that in request
			if(sid.equals(survey.getUuid())) {
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
			response = "Survey ID: " + sid + " not found";
	        log.debug("Posted response to queue: {}", response);
	        System.out.println("Posted response to queue: " + response);
			
			sender.sendObject(response, destQname, req_header);
			return Mono.empty();
		}).subscribe();
	}

	public Message<String> getLastReceivedMessage() {
		if(receivedMsgs == null || this.receivedMsgs.size() == 0) {
			log.debug("No messages have been received");
			System.out.println("No messages have been received");
			return null;
		}
		
		return receivedMsgs.get(receivedMsgs.size()-1);
	}
	
}
