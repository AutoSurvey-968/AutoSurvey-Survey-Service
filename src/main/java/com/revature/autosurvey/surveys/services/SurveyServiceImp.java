package com.revature.autosurvey.surveys.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.autosurvey.surveys.beans.Question;
import com.revature.autosurvey.surveys.beans.QuestionType;
import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.data.SurveyRepo;
import com.revature.autosurvey.surveys.utils.FileReaderUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SurveyServiceImp implements SurveyService {

	private SurveyRepo surveyRepo;
	private ObjectMapper objectMapper;
	private static final String[] VALID_HEADERS = {"questionType","title","helpText","isRequired","choices","hasOtherOption"};

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
				return;
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

	@Override
	public Mono<Survey> addSurveyFromFile(Flux<FilePart> file, String title, String desc, String confirmation) {
		return file.flatMap(FileReaderUtil::readFile).flatMap(s -> {
			String[] values = s.split("\\r?\\n");
			String[] headers = values[0].split(",");
			
			Survey survey = new Survey();
			survey.setTitle(title);
			survey.setDescription(desc);
			survey.setConfirmation(confirmation);
			
			List<Question> quesList = new ArrayList<>();
			//Iterate over each row in the CSV
			for (int i = 1; i < values.length; i++) {
				String[] quesString = values[i].split(",");
				Map<String, String> quesMap = new HashMap<>();
				//Iterate over each part of the choice in the row
				for (int j = 0; j < quesString.length; j++) {
					quesMap.put(headers[j], quesString[j]);
				}
				
				//Ensure that there is a mapping and a value for each
				for (String header : VALID_HEADERS) {
					if (quesMap.get(header) == null) {
						return Mono.empty();
					}
				}
				
				//Make sure the mapping is correct
				try {
					Question ques = new Question();
					ques.setTitle(quesMap.get("title"));
					ques.setHasOtherOption(Boolean.valueOf(quesMap.get("hasOtherOption")));
					ques.setQuestionType(QuestionType.valueOf(quesMap.get("questionType")));
					ques.setHelpText(quesMap.get("helpText"));
					ques.setIsRequired(Boolean.valueOf(quesMap.get("isRequired")));
					
					//Need to check if choices is null
					if (quesMap.get("choices") != null && !quesMap.get("choices").isBlank()) {
						List<String> choiceList = Arrays.asList(quesMap.get("choices").split("//"));
						ques.setChoices(choiceList);
					}
					quesList.add(ques);
				} catch (Exception e) {
					return Mono.empty();
				}
				
			}
			
			survey.setQuestions(quesList);
			return addSurvey(survey);
		}).singleOrEmpty();
	}
}
