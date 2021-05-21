package com.revature.autosurvey.surveys.karate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;

class SurveyKarateTests {

	List<String> tests = new ArrayList<>();

	@Test
	void testParallel() {
		System.setProperty("karate.env", "dev");
		tests.add("classpath:/com/revature/autosurvey/surveys/karate/survey-get.feature");
		tests.add("classpath:/com/revature/autosurvey/surveys/karate/survey-post.feature");
		Results results = Runner.path(tests).parallel(5);
		assertEquals(0, results.getFailCount(), results.getErrorMessages());
	}
}
