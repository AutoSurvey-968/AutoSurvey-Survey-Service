package com.revature.autosurvey.surveys.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.services.SurveyService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class SurveyControllerTests {

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

	private static UUID validUuid;
	private static UUID invalidUuid;
	private static String validTitle;

	@BeforeAll
	static void before() {
		invalidUuid = UUID.fromString("5ec294ec-b8d5-11eb-8529-0242ac130003");
		validUuid = UUID.fromString("186d7fd1-1aae-44f4-8755-c3ebb5d4711f");
		validTitle = "This is a valid title!";
	}

	@Test
	void testGetByUuidRespondsWith404WhenServiceReturnsEmptyMono() {
		doReturn(Mono.empty()).when(surveyService).getByUuid(any());

		Mono<ResponseEntity<Object>> result = surveyController.getSurveyByUuid(invalidUuid);

		StepVerifier.create(result).expectNext(ResponseEntity.notFound().build()).verifyComplete();
	}

	@Test
	void testGetByUuidRespondsWithSurveyWhenGivenValidUuid() {
		Survey survey = new Survey();
		survey.setUuid(validUuid);

		doReturn(Mono.just(survey)).when(surveyService).getByUuid(any());
		Mono<ResponseEntity<Object>> result = surveyController.getSurveyByUuid(validUuid);

		StepVerifier.create(result).expectNext(ResponseEntity.ok(survey)).verifyComplete();
	}
	
	@Test
	void testGetByTitleRespondsWith404WhenServiceReturnsEmptyMono() {
		doReturn(Mono.empty()).when(surveyService).getByTitle(any());

		Mono<ResponseEntity<Object>> result = surveyController.getSurveyByTitle("bad title");

		StepVerifier.create(result).expectNext(ResponseEntity.notFound().build()).verifyComplete();
	}
	@Test
	void testGetByTitleRespondsWithSurveyWhenGivenValidTitle() {
		Survey survey = new Survey();
		survey.setTitle(validTitle);
		
		doReturn(Mono.just(survey)).when(surveyService).getByTitle(any());
		Mono<ResponseEntity<Object>> result = surveyController.getSurveyByTitle(validTitle);
		
		StepVerifier.create(result).expectNext(ResponseEntity.ok(survey)).verifyComplete();
	}

	@Test
	void testGetAllRespondsWithList() {		
		Map<UUID, String> testData = new HashMap<>();
		testData.put(validUuid, validTitle);
		
		doReturn(Mono.just(testData)).when(surveyService).getAllSurveyList();
		Mono<ResponseEntity<Map<UUID, String>>> result = surveyController.getAllSurveyList();

		StepVerifier.create(result).expectNext(ResponseEntity.ok(testData)).verifyComplete();
	}

	@Test
	void testDeleteByUuidRespondsWithNoContentWhenGivenValidUuid() {
		Survey survey = new Survey();
		survey.setUuid(validUuid);

		doReturn(Mono.just(true)).when(surveyService).deleteSurvey(any());
		Mono<ResponseEntity<Object>> result = surveyController.deleteSurvey(validUuid);

		StepVerifier.create(result).expectNext(ResponseEntity.noContent().build()).verifyComplete();
	}

	@Test
	void testAddSurveyAddsASurvey() throws JsonProcessingException {
		Survey survey = new Survey();
		doReturn(Mono.just(survey)).when(surveyService).addSurvey(any());
		Mono<ResponseEntity<Object>> result = surveyController.addSurvey(survey);
		StepVerifier.create(result).expectNext(ResponseEntity.status(HttpStatus.CREATED).body(survey)).verifyComplete();
	}

	@Test()
	void testAddSurveyErrorsOnBadMapping() throws JsonProcessingException {
		Survey survey = new Survey();

		doThrow(JsonProcessingException.class).when(surveyService).addSurvey(any());

		Mono<ResponseEntity<Object>> result = surveyController.addSurvey(survey);

		StepVerifier.create(result).expectNext(ResponseEntity.badRequest().build()).verifyComplete();
	}
	@Test
	void testEditRespondsWithSurveyWhenGivenValidUuid() {
		Survey survey = new Survey();
		survey.setUuid(validUuid);
		
		doReturn(Mono.just(survey)).when(surveyService).editSurvey(any());
		Mono<ResponseEntity<Survey>> result = surveyController.editSurvey(validUuid, survey);
		
		StepVerifier.create(result).expectNext(ResponseEntity.ok(survey)).verifyComplete();
	}
}
