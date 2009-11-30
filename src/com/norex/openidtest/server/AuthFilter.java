package com.norex.openidtest.server;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openid4java.discovery.DiscoveryInformation;

import com.google.inject.*;
import com.google.step2.ConsumerHelper;
import com.google.step2.servlet.GuiceServletContextListener;

public class AuthFilter implements javax.servlet.Filter {
	public static final String CLAIMED_ID_SESSION_ATTR = "claimedIdentity";
	public static final String CLAIMED_ID_COOKIE_NAME = "claimedIdentity";
	
	private static final String DISCOVERED_INFO_SESSION_ATTR = "discoveredInfo";

	@Inject
	private ConsumerHelper consumerHelper;

	@Override
	public void init(FilterConfig config) throws ServletException {
	    ServletContext context = config.getServletContext();
	    Injector injector = (Injector)
	        context.getAttribute(GuiceServletContextListener.INJECTOR_ATTRIBUTE);

	    if (injector == null) {
	      throw new ServletException("could not find Guice injector");
	    }
	    injector.injectMembers(this);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		
		if (notLoggedIn((HttpServletRequest) request) && !isLoginRequest(httpRequest)) {
			removeOpenIdIdentityCookie((HttpServletRequest) request, (HttpServletResponse) response);
			redirectToLoginUrl(response, httpRequest);
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

	private void redirectToLoginUrl(ServletResponse response,
			HttpServletRequest httpRequest) throws IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String baseUrl = "http://localhost:8888/OpenIdTest.html?gwt.hosted=192.168.1.151:9997";
		String notLoggedInParam = "?loggedIn=false";
		httpResponse.sendRedirect(baseUrl + notLoggedInParam);
	}
}
