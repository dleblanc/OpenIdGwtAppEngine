package com.norex.openidtest.client;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("LoginGWT.rpc")
public interface LoginService extends RemoteService {
	AuthRedirectInfo loginAndGetAddressToRedirectTo(String email);
}
