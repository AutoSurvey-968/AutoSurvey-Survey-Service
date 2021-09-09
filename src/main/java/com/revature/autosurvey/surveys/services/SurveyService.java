package com.revature.autosurvey.surveys.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.autosurvey.surveys.beans.Survey;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SurveyService {
	Mono<Survey> getByUuid(UUID uuid);

	Mono<Survey> addSurvey(Survey survey) throws JsonProcessingException;

	Mono<Boolean> deleteSurvey(UUID uuid);

	Mono<Survey> editSurvey(Survey bodySurvey);

	Mono<Map<UUID, String>> getAllSurveyList();
	
	Mono<Survey> addSurveyFromFile(Flux<FilePart> file, String title, String desc, String confirmation);

	Mono<Survey> getByTitle(String title);

}
