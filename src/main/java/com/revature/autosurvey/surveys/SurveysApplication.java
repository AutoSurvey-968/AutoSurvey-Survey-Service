package com.revature.autosurvey.surveys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

import com.revature.autosurvey.surveys.sqs.MessageSender;

@SpringBootApplication
@EnableEurekaClient
public class SurveysApplication {

	public static void main(String[] args) {
		
		ApplicationContext ctx = SpringApplication.run(SurveysApplication.class, args);
		MessageSender sendMsg = ctx.getBean(MessageSender.class);
		
		try {
			sendMsg.sendObject(null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}

}
