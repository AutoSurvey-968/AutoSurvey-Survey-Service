package com.revature.autosurvey.surveys.services;

import java.util.UUID;

import com.revature.autosurvey.surveys.beans.Survey;

import reactor.core.publisher.Mono;

public interface SurveyService {
	Mono<Survey> getByUuid(UUID uuid);
}
