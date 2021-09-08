package com.revature.autosurvey.surveys.services;

import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.autosurvey.surveys.beans.Survey;

import reactor.core.publisher.Mono;

public interface SurveyService {
	Mono<Survey> getByUuid(UUID uuid);

	Mono<Survey> addSurvey(Survey survey) throws JsonProcessingException;

	Mono<Boolean> deleteSurvey(UUID uuid);

	Mono<Survey> editSurvey(Survey bodySurvey);

	Mono<Map<UUID, String>> getAllSurveyList();

	Mono<Survey> getByTitle(String title);
}
