package com.revature.autosurvey.surveys.karate;

import com.intuit.karate.junit5.Karate;

class SurveyKarateTests {

//	@Karate.Test
//	Karate testGetAll() {
//		return Karate.run("survey-get").tags("@get_all_surveys").relativeTo(getClass());
//	}
	
	@Karate.Test
	Karate testCreate() {
		return Karate.run("survey-post").tags("@create_new_survey").relativeTo(getClass());
	}
}
