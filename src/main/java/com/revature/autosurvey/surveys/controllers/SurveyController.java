package com.revature.autosurvey.surveys.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.services.SurveyService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("surveys")
public class SurveyController {
	private final Survey emptySurvey = new Survey();
	private SurveyService surveyService;

	@Autowired
	public void setSurveyService(SurveyService surveyService) {
		this.surveyService = surveyService;
	}
	
	@PostMapping
	public Mono<ResponseEntity<Object>> addSurvey(@RequestBody Survey bodySurvey) {
		try {
			return surveyService.addSurvey(bodySurvey).defaultIfEmpty(emptySurvey).map(survey -> {
				return ResponseEntity.status(HttpStatus.CREATED).body(survey);
			});
		} catch (JsonProcessingException e) {
			System.out.println(e);
			return Mono.just(ResponseEntity.badRequest().build());
		}
	}
	
	@GetMapping("{id}")
	public Mono<ResponseEntity<Object>> getSurveyByUuid(@PathVariable("id") UUID uuid) {
		return surveyService.getByUuid(uuid).defaultIfEmpty(emptySurvey).map(survey -> {
			if (uuid.equals(survey.getUuid())) {
				return ResponseEntity.ok(survey);
			}
			return ResponseEntity.notFound().build();
		});
	}

	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Object>> deleteSurvey(@PathVariable("id") UUID uuid) {
		return surveyService.deleteSurvey(uuid)
				.map(survey -> ResponseEntity.noContent().build())
				.onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
	}
}
