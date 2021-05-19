package com.revature.autosurvey.surveys.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.services.SurveyService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path="/surveys")
public class SurveyController {
	
	private SurveyService surveyService;
	
	@Autowired
	public void setSurveyService(SurveyService surveyService) {
		this.surveyService = surveyService;
	}
	
	@GetMapping(path="/{id}")
	public Mono<ResponseEntity<Survey>> getSurvey() {
		Survey survey = new Survey();
		survey.setTitle("Test Title");
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(survey));
	}
}
