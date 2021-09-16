package com.revature.autosurvey.surveys.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.autosurvey.surveys.beans.Question;
import com.revature.autosurvey.surveys.beans.QuestionType;
import com.revature.autosurvey.surveys.beans.Survey;
import com.revature.autosurvey.surveys.data.SurveyRepo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
	void testGetByTitle() throws JsonMappingException, JsonProcessingException {
		doReturn(Mono.just(survey1)).when(repoMock).getByTitle(survey1.getTitle());
		doReturn(ques1).when(objMapMock).readValue(mapQues1, Question.class);
		
		Survey result= ssi.getByTitle(survey1.getTitle()).block();
		assertEquals(id1, result.getUuid());
		assertEquals(ques1, result.getQuestions().get(0));
	}
	
	@Test
	void testDeleteByUuid() {
		
		doReturn(Mono.just(true)).when(repoMock).deleteByUuid(id1);
		
		Boolean idResult= ssi.deleteSurvey(id1).block();
		assertEquals(true, idResult);
	}
	
	@Test
	void testAddSurveyFromFileValidChoices() {
		String header = "questionType,title,helpText,isRequired,choices,hasOtherOption\n";
		String csv = header + "MULTIPLE_CHOICE,This is a title,This is help text,false,one//two//three,false";
		ArgumentCaptor<Survey> captor = ArgumentCaptor.forClass(Survey.class);
		
		//Create the fake FilePart
		FilePart filePart = Mockito.mock(FilePart.class);
		DataBuffer dataBuffer = Mockito.mock(DataBuffer.class);
		byte[] byteArray = csv.getBytes();
		ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
		byteBuffer.put(byteArray);
		byteBuffer.position(0);
		Flux<DataBuffer> fluxDataBuffer = Flux.just(dataBuffer);
		when(filePart.content()).thenReturn(fluxDataBuffer);
		when(dataBuffer.readableByteCount()).thenReturn(byteArray.length);
		when(dataBuffer.read(any(byte[].class))).thenAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			byteBuffer.get((byte[])args[0]);
			return null;
		});
		
		//Create a new survey and set a new question to it
		Survey survey = new Survey();
		survey.setTitle("Title");
		survey.setDescription("Description");
		survey.setConfirmation("Confirm");
		Question ques = new Question();
		ques.setQuestionType(QuestionType.MULTIPLE_CHOICE);
		ques.setTitle("This is a title");
		ques.setHelpText("This is help text");
		ques.setIsRequired(false);
		ques.setHasOtherOption(false);
		List<String> choices = new ArrayList<>();
		choices.add("one");
		choices.add("two");
		choices.add("three");
		ques.setChoices(choices);
		survey.setQuestions(new ArrayList<>());
		survey.getQuestions().add(ques);
		String quesString = "{\"questionType\":\"MULTIPLE_CHOICE\",\"title\":\"This is a title\",\"helpText\":\"This is help text\",\"isRequired\":false,\"choices\":[\"one\",\"two\", \"three\"],\"hasOtherOption\":false}";
		survey.setMappedQuestions(new ArrayList<>());
		survey.getMappedQuestions().add(quesString);
		try {
			doReturn(quesString).when(objMapMock).writeValueAsString(ques);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		doReturn(Mono.just(survey)).when(repoMock).save(captor.capture());
		
		Mono<Survey> monoSurvey = ssi.addSurveyFromFile(Flux.just(filePart), 
				survey.getTitle(), survey.getDescription(), survey.getConfirmation());
		
		StepVerifier.create(monoSurvey).expectNext(survey).verifyComplete();
		
		Survey captured = captor.getValue();
		assertEquals(survey.getTitle(), captured.getTitle(), "Assert that the title is the same");
		assertEquals(survey.getDescription(), captured.getDescription(), "Assert that the description is the same");
		assertEquals(survey.getConfirmation(), captured.getConfirmation(), "Assert that the confirmation is the same");
		assertEquals(survey.getMappedQuestions(), captured.getMappedQuestions(), "Assert that the mapped questions is correct");
		assertEquals(survey.getQuestions(), captured.getQuestions(), "Assert that the questions is correct");
	}
	
	@Test
	void testAddSurveyFromFileValidNotChoices() {
		String header = "questionType,title,helpText,isRequired,choices,hasOtherOption\n";
		String csv = header + "PARAGRAPH,This is a title,This is help text,false,,false";
		ArgumentCaptor<Survey> captor = ArgumentCaptor.forClass(Survey.class);
		
		//Create the fake FilePart
		FilePart filePart = Mockito.mock(FilePart.class);
		DataBuffer dataBuffer = Mockito.mock(DataBuffer.class);
		byte[] byteArray = csv.getBytes();
		ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
		byteBuffer.put(byteArray);
		byteBuffer.position(0);
		Flux<DataBuffer> fluxDataBuffer = Flux.just(dataBuffer);
		when(filePart.content()).thenReturn(fluxDataBuffer);
		when(dataBuffer.readableByteCount()).thenReturn(byteArray.length);
		when(dataBuffer.read(any(byte[].class))).thenAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			byteBuffer.get((byte[])args[0]);
			return null;
		});
		
		//Create a new survey and set a new question to it
		Survey survey = new Survey();
		survey.setTitle("Title");
		survey.setDescription("Description");
		survey.setConfirmation("Confirm");
		Question ques = new Question();
		ques.setQuestionType(QuestionType.PARAGRAPH);
		ques.setTitle("This is a title");
		ques.setHelpText("This is help text");
		ques.setIsRequired(false);
		ques.setHasOtherOption(false);
		survey.setQuestions(new ArrayList<>());
		survey.getQuestions().add(ques);
		String quesString = "{\"questionType\":\"PARAGRAPH\",\"title\":\"This is a title\",\"helpText\":\"This is help text\",\"isRequired\":false,\"choices\":[],\"hasOtherOption\":false}";
		survey.setMappedQuestions(new ArrayList<>());
		survey.getMappedQuestions().add(quesString);
		try {
			doReturn(quesString).when(objMapMock).writeValueAsString(ques);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		doReturn(Mono.just(survey)).when(repoMock).save(captor.capture());
		
		Mono<Survey> monoSurvey = ssi.addSurveyFromFile(Flux.just(filePart), 
				survey.getTitle(), survey.getDescription(), survey.getConfirmation());
		
		StepVerifier.create(monoSurvey).expectNext(survey).verifyComplete();
		
		Survey captured = captor.getValue();
		assertEquals(survey.getTitle(), captured.getTitle(), "Assert that the title is the same");
		assertEquals(survey.getDescription(), captured.getDescription(), "Assert that the description is the same");
		assertEquals(survey.getConfirmation(), captured.getConfirmation(), "Assert that the confirmation is the same");
		assertEquals(survey.getQuestions(), captured.getQuestions(), "Assert that the questions is a new list");
		assertEquals(survey.getMappedQuestions(), captured.getMappedQuestions(), "Assert that the mapped questions is correct");
	}
	
	@Test
	void testAddSurveyFromFileValidMultipleChoices() {
		String header = "questionType,title,helpText,isRequired,choices,hasOtherOption\n";
		String csv = header + "MULTIPLE_CHOICE,This is a title,This is help text,false,one//two//three,false\n";
		csv = csv + "PARAGRAPH,This is a title,This is help text,false,,false";
		ArgumentCaptor<Survey> captor = ArgumentCaptor.forClass(Survey.class);
		
		//Create the fake FilePart
		FilePart filePart = Mockito.mock(FilePart.class);
		DataBuffer dataBuffer = Mockito.mock(DataBuffer.class);
		byte[] byteArray = csv.getBytes();
		ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
		byteBuffer.put(byteArray);
		byteBuffer.position(0);
		Flux<DataBuffer> fluxDataBuffer = Flux.just(dataBuffer);
		when(filePart.content()).thenReturn(fluxDataBuffer);
		when(dataBuffer.readableByteCount()).thenReturn(byteArray.length);
		when(dataBuffer.read(any(byte[].class))).thenAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			byteBuffer.get((byte[])args[0]);
			return null;
		});
		
		//Create a new survey and set a new question to it
		Survey survey = new Survey();
		survey.setTitle("Title");
		survey.setDescription("Description");
		survey.setConfirmation("Confirm");
		
		//Set the first question
		Question ques = new Question();
		ques.setQuestionType(QuestionType.MULTIPLE_CHOICE);
		ques.setTitle("This is a title");
		ques.setHelpText("This is help text");
		ques.setIsRequired(false);
		ques.setHasOtherOption(false);
		List<String> choices = new ArrayList<>();
		choices.add("one");
		choices.add("two");
		choices.add("three");
		ques.setChoices(choices);
		survey.setQuestions(new ArrayList<>());
		survey.getQuestions().add(ques);
		String quesString = "{\"questionType\":\"MULTIPLE_CHOICE\",\"title\":\"This is a title\",\"helpText\":\"This is help text\",\"isRequired\":false,\"choices\":[\"one\",\"two\", \"three\"],\"hasOtherOption\":false}";
		survey.setMappedQuestions(new ArrayList<>());
		survey.getMappedQuestions().add(quesString);
		
		//Set the second question
		Question ques2 = new Question();
		ques2.setQuestionType(QuestionType.PARAGRAPH);
		ques2.setTitle("This is a title");
		ques2.setHelpText("This is help text");
		ques2.setIsRequired(false);
		ques2.setHasOtherOption(false);
		survey.getQuestions().add(ques2);
		String quesString2 = "{\"questionType\":\"PARAGRAPH\",\"title\":\"This is a title\",\"helpText\":\"This is help text\",\"isRequired\":false,\"choices\":[],\"hasOtherOption\":false}";
		survey.getMappedQuestions().add(quesString2);
		
		try {
			doReturn(quesString).when(objMapMock).writeValueAsString(ques);
			doReturn(quesString2).when(objMapMock).writeValueAsString(ques2);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		doReturn(Mono.just(survey)).when(repoMock).save(captor.capture());
		
		Mono<Survey> monoSurvey = ssi.addSurveyFromFile(Flux.just(filePart), 
				survey.getTitle(), survey.getDescription(), survey.getConfirmation());
		
		StepVerifier.create(monoSurvey).expectNext(survey).verifyComplete();
		
		Survey captured = captor.getValue();
		assertEquals(survey.getTitle(), captured.getTitle(), "Assert that the title is the same");
		assertEquals(survey.getDescription(), captured.getDescription(), "Assert that the description is the same");
		assertEquals(survey.getConfirmation(), captured.getConfirmation(), "Assert that the confirmation is the same");
		assertEquals(survey.getQuestions(), captured.getQuestions(), "Assert that the questions is a new list");
		assertEquals(survey.getMappedQuestions(), captured.getMappedQuestions(), "Assert that the mapped questions is correct");
	}
	
	
	@Test
	void testAddSurveyFromFileInvalidMissingValue() {
		String header = "questionType,title,helpText,isRequired,choices,hasOtherOption\n";
		String csv = header + "MULTIPLE_CHOICE,This is a title,This is help text,false,one//two/three";
		
		//Create the fake FilePart
		FilePart filePart = Mockito.mock(FilePart.class);
		DataBuffer dataBuffer = Mockito.mock(DataBuffer.class);
		byte[] byteArray = csv.getBytes();
		ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
		byteBuffer.put(byteArray);
		byteBuffer.position(0);
		Flux<DataBuffer> fluxDataBuffer = Flux.just(dataBuffer);
		when(filePart.content()).thenReturn(fluxDataBuffer);
		when(dataBuffer.readableByteCount()).thenReturn(byteArray.length);
		when(dataBuffer.read(any(byte[].class))).thenAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			byteBuffer.get((byte[])args[0]);
			return null;
		});
		
		//Create a new survey and set a new question to it
		Survey survey = new Survey();
		survey.setTitle("Title");
		survey.setDescription("Description");
		survey.setConfirmation("Confirm");
		Question ques = new Question();
		ques.setQuestionType(QuestionType.MULTIPLE_CHOICE);
		ques.setTitle("This is a title");
		ques.setHelpText("This is help text");
		ques.setIsRequired(false);
		ques.setHasOtherOption(false);
		List<String> choices = new ArrayList<>();
		choices.add("one");
		choices.add("two");
		choices.add("three");
		ques.setChoices(choices);
		survey.setQuestions(new ArrayList<>());
		survey.getQuestions().add(ques);
		
		
		Mono<Survey> monoSurvey = ssi.addSurveyFromFile(Flux.just(filePart), 
				survey.getTitle(), survey.getDescription(), survey.getConfirmation());
		
		StepVerifier.create(monoSurvey).verifyComplete();
		
		verifyNoInteractions(repoMock);
	}
	
	
	
}
