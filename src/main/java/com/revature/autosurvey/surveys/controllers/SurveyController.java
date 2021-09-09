package com.revature.autosurvey.surveys.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.services.SurveyService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class SurveyController {
	private final Survey emptySurvey = new Survey();
	private SurveyService surveyService;

	@Autowired
	public void setSurveyService(SurveyService surveyService) {
		this.surveyService = surveyService;
	}

	@PostMapping(consumes = "application/json")
	public Mono<ResponseEntity<Object>> addSurvey(@RequestBody Survey bodySurvey) {
		try {
			return surveyService.addSurvey(bodySurvey).defaultIfEmpty(emptySurvey)
					.map(survey -> ResponseEntity.status(HttpStatus.CREATED).body(survey));
		} catch (JsonProcessingException e) {
			return Mono.just(ResponseEntity.badRequest().build());
		}
	}
	
	@PostMapping(consumes = "multipart/form-data")
	public Mono<ResponseEntity<Object>> addSurveyFromFile(
			@RequestPart("file") Flux<FilePart> fileFlux,
			@RequestPart("name") String name, 
			@RequestPart("description") String desc, 
			@RequestPart("confirmation") String confirm) {
		return null;
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
	@GetMapping("/title/{title}")
	public Mono<ResponseEntity<Object>> getSurveyByTitle(@PathVariable("title")String title){
		return surveyService.getByTitle(title).defaultIfEmpty(emptySurvey).map(survey -> {
			if(title.equals(survey.getTitle())) {
				return ResponseEntity.ok(survey);
			}
			return ResponseEntity.notFound().build();
		});
	}

	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Object>> deleteSurvey(@PathVariable("id") UUID uuid) {
		return surveyService.deleteSurvey(uuid).map(survey -> ResponseEntity.noContent().build())
				.onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
	}

	@PutMapping("{id}")
	public Mono<ResponseEntity<Survey>> editSurvey(@PathVariable("id") UUID uuid, @RequestBody Survey bodySurvey) {
		Survey s = bodySurvey;
		s.setUuid(uuid);
		return surveyService.editSurvey(s).defaultIfEmpty(emptySurvey).map(survey -> ResponseEntity.ok(survey))
				.onErrorResume(error -> Mono.just(ResponseEntity.badRequest().build()));

	}

	@GetMapping
	public Mono<ResponseEntity<Map<UUID, String>>> getAllSurveyList() {
		return surveyService.getAllSurveyList().map(list -> ResponseEntity.ok(list));
	}
}
