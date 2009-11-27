package com.norex.openidtest.client;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	AuthRedirectInfo loginAndGetAddressToRedirectTo(String email);
}
