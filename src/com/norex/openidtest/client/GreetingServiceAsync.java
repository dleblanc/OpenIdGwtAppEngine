package com.norex.openidtest.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void doSomethingInteresting(AsyncCallback<String> callback);
}
