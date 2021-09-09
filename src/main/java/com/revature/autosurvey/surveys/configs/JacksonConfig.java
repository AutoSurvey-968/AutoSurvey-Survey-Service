package com.revature.autosurvey.surveys.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
/**
 * 
 * @author MuckJosh
 *
 *	Class is used to configure Jackson for LocalTimeDate in Surveys
 */
@Configuration
@EnableAsync
//@ComponentScan("com.revature.autosurvey.surveys")
public class JacksonConfig {

	@Bean
	@Primary
//	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
	public ObjectMapper objectMapper() {
//		ObjectMapper mapper = builder.createXmlMapper(false).build();
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return mapper;
	}
}
