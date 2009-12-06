package com.norex.openidtest.server;

import javax.servlet.*;
import javax.servlet.http.Cookie;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.*;
import com.google.step2.servlet.GuiceServletContextListener;
import com.norex.openidtest.client.GreetingService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@Singleton
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String doSomethingInteresting() { // NOTE: email here is an IdP (identity provider)
		
		// NOTE: doesn't need to be a RemoteServiceServlet anymore (according to guice docs) - so can do away with thread local stuff.
		String identityFromCookie = "unknown";
		for (Cookie cookie : getThreadLocalRequest().getCookies()) {
			if (AuthFilter.CLAIMED_ID_COOKIE_NAME.equals(cookie.getName())) {
				identityFromCookie = cookie.getValue();
			}
		}
		
		return "This is an interesting call from an authenticated session! Open id identity according to cookie: " + identityFromCookie;
	}
}
