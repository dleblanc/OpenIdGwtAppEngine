package com.norex.openidtest.server;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.*;
import com.google.step2.*;
import com.google.step2.discovery.IdpIdentifier;
import com.norex.openidtest.client.*;

@SuppressWarnings("serial")
@Singleton
public class LoginServlet extends RemoteServiceServlet implements
LoginService {

	// TODO: use guice here instead! store it session-wide
	private static final String DISCOVERED_INFO_SESSION_ATTR = "discoveredInfo";
	
	@Inject
	private ConsumerHelper consumerHelper;
	  
	@Override
	public AuthRedirectInfo loginAndGetAddressToRedirectTo(String email) {
	    // NOTE: would throw an exception normally
	    assert(email.length() > 0);
	    
		// TODO: test the crap out of this
		
		// NOTE: need to grab this dynamically (like below), not hard code it
		
//	    // posted means they're sending us an OpenID
//	    StringBuffer realmBuf = new StringBuffer(req.getScheme())
//	        .append("://").append(req.getServerName());
//
//	    if ((req.getScheme().equalsIgnoreCase("http")
//	         && req.getServerPort() != 80)
//	        || (req.getScheme().equalsIgnoreCase("https")
//	            && req.getServerPort() != 443)) {
//	      realmBuf.append(":").append(req.getServerPort());
//	    }
//
//	    String realm = realmBuf.toString();
		
//	    String returnToUrl = new StringBuffer(realm)
//	        .append(realm).append("/redirected").toString();
	    String realm = "http://localhost:8888/";
		String returnToUrl = "http://localhost:8888/openidtest/verifyLoginRedirect";
	    
	    // if the user typed am email address, ignore the user part
	    String emailSuffix = email.replaceFirst(".*@", "");

	    
	    // we assume that the user typed an identifier for an IdP, not for a user
	    IdpIdentifier openId = new IdpIdentifier(emailSuffix);

	    AuthRequestHelper helper = consumerHelper.getAuthRequestHelper(
	        openId, returnToUrl.toString());

	    helper.requestUxIcon(true);
	    
	    // if OAUTH:
//	      try {
//	        OAuthAccessor accessor = providerStore.getOAuthAccessor("google");
//	        helper.requestOauthAuthorization(accessor.consumer.consumerKey,
//	            "http://www.google.com/m8/feeds/");
//	      } catch (ProviderInfoNotFoundException e) {
//	        log("could not find provider info for Google", e);
//	        // we'll just ignore the OAuth request and proceed without it.
//	      }

	    helper.requestAxAttribute(Step2.AxSchema.EMAIL, true);
	    // Add other attributes here...


	    AuthRequest authReq = null;
	    try {
	      authReq = helper.generateRequest();
	      authReq.setRealm(realm);
	      
	      // Hold on to this (guice? -- session wide, assuming I need it - I don't believe I do!)
	      getThreadLocalRequest().getSession().setAttribute(DISCOVERED_INFO_SESSION_ATTR, helper.getDiscoveryInformation());
	    } catch (DiscoveryException e) {
	    	// NOTE: they do some kind of handling of discovery (that we probably don't have to do)
    	  throw new RuntimeException("Discovery failed", e);
	    } catch (MessageException e) {
	      throw new RuntimeException(e);
	    } catch (ConsumerException e) {
	      throw new RuntimeException(e);
	    }
	    
		return new AuthRedirectInfo(authReq.getDestinationUrl(true));
	}
}
