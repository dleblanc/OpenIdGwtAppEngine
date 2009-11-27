package com.norex.openidtest.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AuthRedirectInfo implements Serializable {
	private String redirectTo;

	public AuthRedirectInfo() {}
	
	public AuthRedirectInfo(String redirectTo) {
		this.redirectTo = redirectTo;
	}

	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
}
