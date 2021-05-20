package com.revature.autosurvey.surveys.services;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.autosurvey.surveys.beans.Survey;

import reactor.core.publisher.Mono;

public interface SurveyService {
	Mono<Survey> getByUuid(UUID uuid);
	Mono<Survey> addSurvey(Survey survey) throws JsonProcessingException;
	Mono<Survey> deleteSurvey(UUID uuid);
}
