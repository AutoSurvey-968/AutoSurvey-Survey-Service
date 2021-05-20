package com.revature.autosurvey.surveys;

import com.intuit.karate.junit5.Karate;

class SurveyTests {

	/*
	 * This is where we put the code to run our Karate tests when we have the
	 * .feature files in place. We keep it empty for now in the hopes that the
	 * sonarcloud tester doesn't try to test with it.
	 */
	@Karate.Test
	Karate testTags() {
		return Karate.run("survey-get").tags("get-survey-by-id").relativeTo(getClass());
	}
}
