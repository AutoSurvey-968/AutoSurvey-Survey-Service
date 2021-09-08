package com.revature.autosurvey.surveys.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.autosurvey.surveys.beans.Question;
import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.data.SurveyRepo;

import reactor.core.publisher.Mono;

@Service
public class SurveyServiceImp implements SurveyService {

	private SurveyRepo surveyRepo;
	private ObjectMapper objectMapper;

	@Autowired
	public void setSurveyRepo(SurveyRepo surveyRepo) {
		this.surveyRepo = surveyRepo;
	}

	@Autowired
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Mono<Survey> getByUuid(UUID uuid) {
		return surveyRepo.getByUuid(uuid).doOnNext(survey -> {
			try {
				List<Question> list = new ArrayList<>();
				for (String json : survey.getMappedQuestions()) {
					list.add(objectMapper.readValue(json, Question.class));
				}
				survey.setQuestions(list);
			} catch (Exception e) {
				return;
			}
		});
	}
	@Override
	public Mono<Survey> getByTitle(String title){
		return surveyRepo.getByTitle(title).doOnNext(survey -> {
			try {
				List<Question> list = new ArrayList<>();
				for (String json : survey.getMappedQuestions()) {
					list.add(objectMapper.readValue(json, Question.class));
				}
				survey.setQuestions(list);
			}catch(Exception e) {
				
			}
		});
	}

	@Override
	public Mono<Survey> addSurvey(Survey survey) {
		try {
			List<String> list = new ArrayList<>();
			for (Question question : survey.getQuestions()) {
				list.add(objectMapper.writeValueAsString(question));
			}
			survey.setMappedQuestions(list);
			return surveyRepo.save(survey);
		} catch (JsonProcessingException e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<Boolean> deleteSurvey(UUID uuid) {
		return surveyRepo.deleteByUuid(uuid);
	}

	@Override

	public Mono<Survey> editSurvey(Survey bodySurvey) {
		return surveyRepo.save(bodySurvey);
	}

	public Mono<Map<UUID, String>> getAllSurveyList() {
		return surveyRepo.findAll().collectMap(Survey::getUuid, Survey::getTitle);

	}
}
