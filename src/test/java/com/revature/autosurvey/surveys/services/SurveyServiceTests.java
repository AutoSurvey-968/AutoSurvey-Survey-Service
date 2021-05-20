package com.revature.autosurvey.surveys.services;

import com.revature.autosurvey.surveys.beans.Survey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.revature.autosurvey.surveys.data.SurveyRepo;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTests {

	@Mock
	private static SurveyRepo repoMock;
	
	private static SurveyServiceImp ssi;
	
	private static Survey survey1;
	private static UUID id1;
	
	@BeforeAll
	static void setup() {
		survey1 = new Survey();
		id1 = new UUID(0,1);
	}
	
	@BeforeEach
	void init() {
		ssi = new SurveyServiceImp();
		ssi.setSurveyRepo(repoMock);
		survey1.setUuid(id1);
	}
	
	@Test
	void testGetByUuid() {
		doReturn(Mono.just(survey1)).when(repoMock).getByUuid(id1);
		
		UUID idResult= ssi.getByUuid(id1).block().getUuid();
		assertEquals(id1, idResult);
	}
	
	@Test
	void testDeleteByUuid() {
		doReturn(Mono.just(survey1)).when(repoMock).deleteById(id1);
		
		UUID idResult= ssi.deleteSurvey(id1).block().getUuid();
		assertEquals(id1, idResult);
	}
	
	

	
}
