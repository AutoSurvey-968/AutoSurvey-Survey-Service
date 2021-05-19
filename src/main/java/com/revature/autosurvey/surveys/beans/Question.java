package com.revature.autosurvey.surveys.beans;

import lombok.Data;

@Data
public class Question {
	private QuestionType questionType;
	private String title;
	private String helpText;
	private Boolean isRequired;

	public Question() {
		super();
	}
}
