package com.revature.autosurvey.surveys.beans;

public class TokenVerifierRequest {
	private String token;
	private boolean returnSecureToken;
	
	public TokenVerifierRequest() {
		super();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isReturnSecureToken() {
		return returnSecureToken;
	}

	public void setReturnSecureToken(boolean returnSecureToken) {
		this.returnSecureToken = returnSecureToken;
	}
	
	
}
