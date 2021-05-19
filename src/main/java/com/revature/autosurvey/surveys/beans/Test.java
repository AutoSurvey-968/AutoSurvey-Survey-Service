package com.revature.autosurvey.surveys.beans;

import org.springframework.data.annotation.Id;

import lombok.Data;
@Data
public class Test {

	@Id
	private Long id;
	private String firstName;
	private int age;
	private String email;

	public Test() {
		super();
	}

}
