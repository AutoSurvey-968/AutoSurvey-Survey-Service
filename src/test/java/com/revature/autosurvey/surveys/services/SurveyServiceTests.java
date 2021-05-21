package com.revature.autosurvey.surveys.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.autosurvey.surveys.beans.Question;
import com.revature.autosurvey.surveys.beans.Survey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.revature.autosurvey.surveys.data.SurveyRepo;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTests {

	@Mock
	private static SurveyRepo repoMock;
	@Mock
	private static ObjectMapper objMapMock;
	
	private static SurveyServiceImp ssi;
	
	private static Survey survey1;
	private static UUID id1;
	private static Question ques1;
	private static String mapQues1;
	private static List<Question> quesList;
	private static List<String> mapQuesList;
	
	@BeforeAll
	static void setup() {
		survey1 = new Survey();
		id1 = new UUID(0,1);
		ques1 = new Question();
		mapQues1 = "{\"questionType\":\"MULTIPLE_CHOICE\",\"title\":\"Here is a question?\",\"helpText\":\"this is help text\",\"isRequired\":true,\"choices\":[\"One\",\"Two\"],\"hasOtherOption\":false}";
		quesList = new ArrayList<Question>();
		mapQuesList = new ArrayList<String>();
	}
	
	@BeforeEach
	void init() {
		ssi = new SurveyServiceImp();
		ssi.setSurveyRepo(repoMock);
		ssi.setObjectMapper(objMapMock);
		survey1.setUuid(id1);
		quesList.add(ques1);
		mapQuesList.add(mapQues1);
		survey1.setMappedQuestions(mapQuesList);
		survey1.setQuestions(quesList);
	}
	
	@Test
	void testGetByUuid() throws JsonMappingException, JsonProcessingException {
		doReturn(Mono.just(survey1)).when(repoMock).getByUuid(id1);
		doReturn(ques1).when(objMapMock).readValue(mapQues1, Question.class);
		
		Survey result= ssi.getByUuid(id1).block();
		assertEquals(id1, result.getUuid());
		assertEquals(ques1, result.getQuestions().get(0));
	}
	
	@Test
	void testDeleteByUuid() {
		
		doReturn(Mono.just(true)).when(repoMock).deleteByUuid(id1);
		
		Boolean idResult= ssi.deleteSurvey(id1).block();
		assertEquals(id1, idResult);
	}
	
	

	
}
