package com.revature.autosurvey.surveys.beans;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Question {
	private QuestionType questionType;
	private String title;
	private String helpText;
	private Boolean isRequired;
	private List<String> choices;
	private Boolean hasOtherOption;

	public Question() {
		super();
		this.questionType = QuestionType.MULTIPLE_CHOICE;
		this.title = "";
		this.helpText = "";
		this.isRequired = true;
		this.hasOtherOption = false;
		this.choices = new ArrayList<>();
	}
	
}
