package com.revature.autosurvey.surveys;

import com.intuit.karate.junit5.Karate;

class SurveyKarateTests {

	@Karate.Test
	Karate testTags() {
		return Karate.run("survey-get").tags("@get_all_surveys").relativeTo(getClass());
	}
}
