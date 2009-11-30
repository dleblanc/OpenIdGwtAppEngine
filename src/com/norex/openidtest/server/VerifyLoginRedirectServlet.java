package com.norex.openidtest.server;

import java.io.IOException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openid4java.association.AssociationException;
import org.openid4java.discovery.*;
import org.openid4java.message.*;
import org.openid4java.message.ax.*;

import com.google.inject.*;
import com.google.step2.*;
import com.google.step2.AuthResponseHelper.ResultType;
import com.google.step2.servlet.*;

/**
 * This servlet is what google redirects to after a succesfull authentication,
 * it contains a whole wack of info it it's url.
 * 
 * We'll verify that the google's response matches what we sent to them (so that
 * outsiders can't poison our identity information), and then store that
 * identity information in the session, and set a cookie on the client.
 * 
 * We'll match up this cookie with what's stored in the session in our
 * AuthFilter for all subsequent (non-static) requests.
 * 
 */
public class VerifyLoginRedirectServlet extends InjectableServlet {

	// Magic to inject members into the servlet - we would normally break this
	// out to a separate place
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = config.getServletContext();
		Injector injector = (Injector) context
				.getAttribute(GuiceServletContextListener.INJECTOR_ATTRIBUTE);

		if (injector == null) {
			throw new ServletException("could not find Guice injector");
		}
		injector.injectMembers(this);
	}

	@Inject
	private ConsumerHelper helper;
	private static final String UNKNOWN = "Unknown";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = req.getSession();
		ParameterList openidResp = Step2.getParameterList(req);
		String receivingUrl = Step2.getUrlWithQueryString(req);
		DiscoveryInformation discovered = (DiscoveryInformation) session
				.getAttribute("discovered");

		// Try to get the OpenId, AX, and OAuth values from the auth response
		try {
			AuthResponseHelper authResponse = helper.verify(receivingUrl,
					openidResp, discovered);


			// Get Claimed Identifier
			// FIXME: set this in the session, as well as in our cookie.
			Identifier claimedId = authResponse.getClaimedId();
			if (claimedId == null) {
				throw new ServletException("Cannot have an empty claimed identity");
			}
			
			Cookie identifierCookie = new Cookie("claimedIdentity", claimedId.getIdentifier());
			response.addCookie(identifierCookie);
			identifierCookie.setPath("/");
			// NOTE: may want to have the identifier last longer than the session?
			
			session.setAttribute("claimedIdentity", claimedId.getIdentifier());

			if (authResponse.getAuthResultType() == ResultType.SETUP_NEEDED) {
				throw new ServletException("setup needed");
			}

			if (authResponse.getAuthResultType() == ResultType.AUTH_FAILURE) {
				throw new ServletException("auth failure");
			}

			if (authResponse.getAuthResultType() == ResultType.AUTH_SUCCESS) {
				// Would normally pull out (extended) attributes here, email address, name, etc. 
			}
		} catch (MessageException e) {
			throw new ServletException(e);
		} catch (DiscoveryException e) {
			throw new ServletException(e);
		} catch (AssociationException e) {
			throw new ServletException(e);
		} catch (VerificationException e) {
			throw new ServletException(e);
		}

		String replaceMeUrl = "http://localhost:8888/OpenIdTest.html?gwt.hosted=192.168.1.151:9997&loggedIn=true";
		
		// TODO: set the magic cookie here, and redirect to the normal URL
		response.sendRedirect(replaceMeUrl);
	}
}
