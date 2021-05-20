package com.revature.autosurvey.surveys.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.data.SurveyRepo;

import reactor.core.publisher.Mono;

@Service
public class SurveyServiceImp implements SurveyService {
	
	private SurveyRepo surveyRepo;
	
	@Autowired
	public void setSurveyRepo(SurveyRepo surveyRepo) {
		this.surveyRepo = surveyRepo;
	}

	@Override
	public Mono<Survey> getByUuid(UUID uuid) {
		return surveyRepo.getByUuid(uuid);
	}

	@Override
	public Mono<Survey> deleteSurvey(UUID uuid) {
		return surveyRepo.deleteByUuid(uuid);
	}

}
