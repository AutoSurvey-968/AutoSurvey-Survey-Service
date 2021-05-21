package com.revature.autosurvey.surveys.karate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import java.io.File;
import java.util.Collection;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;

class SurveyKarateTests {

	List<String> tests = new ArrayList<>();

	@Test
	void testParallel() {
		tests.add("classpath:com/revature/autosurvey/surveys/karate/survey-get.feature");
		tests.add("classpath:com/revature/autosurvey/surveys/karate/survey-post.feature");
		Results results = Runner.path(tests).parallel(5);
		generateReport(results.getReportDir());
		assertEquals(0, results.getFailCount(), results.getErrorMessages());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void generateReport(String karateOutputPath) {
		Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] { "json" }, true);
		List<String> jsonPaths = new ArrayList(jsonFiles.size());
		jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
		Configuration config = new Configuration(new File("target"), "ReqRes.in SampleAPI Testing");
		ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
		reportBuilder.generateReports();
	}

}
