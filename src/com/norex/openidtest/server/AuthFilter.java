package com.norex.openidtest.server;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

public class AuthFilter implements javax.servlet.Filter {
	public static final String CLAIMED_ID_SESSION_ATTR = "claimedIdentity";
	public static final String CLAIMED_ID_COOKIE_NAME = "claimedIdentity";
	
	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		if (notLoggedIn((HttpServletRequest) request) && !isLoginRequest(httpRequest)) {
			removeOpenIdIdentityCookie((HttpServletRequest) request, (HttpServletResponse) response);
			redirectToLoginUrl(httpRequest, response);
		}
		else {
			// forwrd on to the chain
			filterChain.doFilter(request, response);
		}
		
	}

	private void removeOpenIdIdentityCookie(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html"); // Not sure why this is needed, but what the heck...
		
		Cookie cookie = new Cookie(CLAIMED_ID_COOKIE_NAME, "");
		cookie.setMaxAge(0); // Remove now
		cookie.setPath("/");
		cookie.setComment("EXPIRING COOKIE at " + System.currentTimeMillis());
		response.addCookie(cookie);

	}

	private boolean isLoginRequest(HttpServletRequest httpRequest) {
		// Obviously very insecure - don't port this to production!
		return httpRequest.getRequestURI().contains("/login") || httpRequest.getRequestURI().contains("/verifyLogin");
	}

	private boolean notLoggedIn(HttpServletRequest request) {
		String claimedIdentityFromSession = (String) request.getSession().getAttribute(CLAIMED_ID_SESSION_ATTR);
		return 
			claimedIdentityFromSession == null ||
			!claimedIdentityFromSession.equals(getCookieValue(request, CLAIMED_ID_COOKIE_NAME));
	}

	private String getCookieValue(HttpServletRequest request, String claimedIdCookieName) {
		for (Cookie cookie : request.getCookies()) {
			if (claimedIdCookieName.equals(cookie.getName())) {
				return cookie.getValue();
			}
		} 
		return null;
	}

	private void redirectToLoginUrl(HttpServletRequest request,
			ServletResponse response) throws IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String baseUrl = "http://localhost:8888/OpenIdTest.html?gwt.hosted=192.168.1.151:9997";
		String notLoggedInParam = "?loggedIn=false";
		httpResponse.sendRedirect(baseUrl + notLoggedInParam);
	}
}
