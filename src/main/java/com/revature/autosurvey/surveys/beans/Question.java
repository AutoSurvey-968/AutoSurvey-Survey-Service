package com.revature.autosurvey.surveys.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Question implements Serializable {
	private static final long serialVersionUID = -6157862126403322171L;

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
