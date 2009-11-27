package com.norex.openidtest.server;

import javax.servlet.*;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.*;
import org.openid4java.message.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.*;
import com.google.step2.*;
import com.google.step2.discovery.IdpIdentifier;
import com.google.step2.servlet.GuiceServletContextListener;
import com.norex.openidtest.client.*;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private static final String DISCOVERED_INFO_SESSION_ATTR = "discoveredInfo";
	
	@Inject
	private ConsumerHelper consumerHelper;
	
	// Magic to inject members into the servlet - we would normally break this out to a separate place
	  @Override
	  public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    ServletContext context = config.getServletContext();
	    Injector injector = (Injector)
	        context.getAttribute(GuiceServletContextListener.INJECTOR_ATTRIBUTE);

	    if (injector == null) {
	      throw new ServletException("could not find Guice injector");
	    }
	    injector.injectMembers(this);
	  }

	public String doSomethingInteresting() { // NOTE: email here is an IdP (identity provider)
		return "This is an interesting call from an authenticated session!";
	}
}
