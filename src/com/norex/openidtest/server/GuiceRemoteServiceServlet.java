package com.norex.openidtest.server;

import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.server.rpc.*;
import com.google.inject.*;

@Singleton
public class GuiceRemoteServiceServlet extends RemoteServiceServlet {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Injector injector;

	@Override
	public String processCall(String payload) throws SerializationException {
		try {
			RPCRequest req = RPC.decodeRequest(payload, null, this);

			RemoteService service = getServiceInstance(req.getMethod()
					.getDeclaringClass());

			return RPC.invokeAndEncodeResponse(service, req.getMethod(), req
					.getParameters(), req.getSerializationPolicy());
		} catch (IncompatibleRemoteServiceException ex) {
			log(
					"IncompatibleRemoteServiceException in the processCall(String) method.",
					ex);
			return RPC.encodeResponseForFailure(null, ex);
		}
	}

	@SuppressWarnings( { "unchecked" })
	private RemoteService getServiceInstance(Class serviceClass) {
		return (RemoteService) injector.getInstance(serviceClass);
	}
}