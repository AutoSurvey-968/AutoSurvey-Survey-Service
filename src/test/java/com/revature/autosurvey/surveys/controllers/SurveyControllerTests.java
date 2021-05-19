package com.revature.autosurvey.surveys.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.services.SurveyService;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
public class SurveyControllerTests {
	
	@TestConfiguration
	static class Configuration {
		@Bean
		public SurveyController getSurveyController(SurveyService surveyService) {
			SurveyController surveyController = new SurveyController();
			surveyController.setSurveyService(surveyService);
			return surveyController;
		}
		
		@Bean
		public SurveyService getSurveyService() {
			return Mockito.mock(SurveyService.class);
		}
	}
	
	@Autowired
	private SurveyController surveyController;
	
	@Autowired 
	private SurveyService surveyService;
	
	@Test
	void testGetSurveyResponseIsNotNull() {
		Mono<ResponseEntity<Survey>> result = surveyController.getSurvey();
		Assert.notNull(result, "Response should not be null, but it is.");
	}
	
	@Test
	void testGetSurveyRespondsWithMono() {
		Mono<ResponseEntity<Survey>> result = surveyController.getSurvey();
		Assert.isTrue(result.getClass() == Mono.class, "Response should be a Mono, but it is not.");
	}
	
}
