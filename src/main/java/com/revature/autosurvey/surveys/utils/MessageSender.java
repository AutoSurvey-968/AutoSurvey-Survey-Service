package com.revature.autosurvey.surveys.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class MessageSender {

	private QueueMessagingTemplate queueMessagingTemplate;
    private String queueName = "https://sqs.us-east-1.amazonaws.com/855430746673/SurveyQueue";
    
    @Autowired
    public MessageSender(AmazonSQSAsync sqs) {
    	super();
    	this.queueMessagingTemplate = new QueueMessagingTemplate(sqs);
    }
    
    @Autowired
    public QueueMessagingTemplate getQueueMessagingTemplate() {
    	return this.queueMessagingTemplate;
    }
    
    @Autowired
    public QueueMessagingTemplate QueueMessagingTemplate(AmazonSQSAsync sqs) {
    	return new QueueMessagingTemplate(sqs);
    }
    
    @Autowired
    public void setQueueMessagingTemplate(AmazonSQSAsync sqs) {
    	this.queueMessagingTemplate = new QueueMessagingTemplate(sqs);
    }

    @Async
    public void sendObject(String payload, String qname, String req_header) {
        log.debug("sendObject method called.. ");
        log.debug("Payload received: ", payload);
        
    	// Use payload input for new request and set MessageID in header to request header
        String target = payload;
        
        // Header should not be null since it is extracted from request header 
        // however, if it is then attach new message ID
        req_header = req_header == null ? UUID.randomUUID().toString() : req_header;
		
        this.queueName = qname == null ? this.queueName : qname;
        log.debug("Attaching request header (MessageId): " + req_header);

        this.queueMessagingTemplate.send(this.queueName,     
			     MessageBuilder.withPayload(target)
			     .setHeader("MessageId", req_header)
			     .build());

		log.debug("sending payload: " + target + "\nDestination Queue: " + this.queueName);
    }
}
