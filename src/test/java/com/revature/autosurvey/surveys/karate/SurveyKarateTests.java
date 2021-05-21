package com.revature.autosurvey.surveys.karate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
<<<<<<< HEAD
//import net.masterthought.cucumber.Configuration;
//import net.masterthought.cucumber.ReportBuilder;
//import org.apache.commons.io.FileUtils;
=======
>>>>>>> 46d2f6e1e112f00750aec06c958737af65727fd8

class SurveyKarateTests {

	List<String> tests = new ArrayList<>();

	@Test
	void testParallel() {
		System.setProperty("karate.env", "dev");
		tests.add("classpath:/com/revature/autosurvey/surveys/karate/survey-get.feature");
		//tests.add("classpath:/com/revature/autosurvey/surveys/karate/survey-post.feature");
		tests.add("classpath:/com/revature/autosurvey/surveys/karate/survey-delete.feature");
		Results results = Runner.path(tests).parallel(5);
		assertEquals(0, results.getFailCount(), results.getErrorMessages());
	}
}
