package com.norex.openidtest.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	void loginAndGetAddressToRedirectTo(String email, AsyncCallback<AuthRedirectInfo> callback);
}
