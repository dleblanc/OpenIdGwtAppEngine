package com.norex.openidtest.client;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("GWT.rpc")
public interface LoginService extends RemoteService {
	AuthRedirectInfo loginAndGetAddressToRedirectTo(String email);
}
