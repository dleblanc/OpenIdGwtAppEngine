package com.norex.openidtest;

import java.net.*;
import java.util.concurrent.*;

import org.openid4java.consumer.*;
import org.openid4java.message.*;

import com.google.inject.*;
import com.google.inject.servlet.*;
import com.google.step2.discovery.*;
import com.google.step2.http.HttpFetcher;
import com.google.step2.hybrid.HybridOauthMessage;
import com.google.step2.openid.ax2.AxMessage2;
import com.google.step2.servlet.ConsumerManagerProvider;
import com.norex.openidtest.client.*;
import com.norex.openidtest.server.*;

public class GuiceServletConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector();
	}
}

class OpenIdExampleServletModule extends ServletModule {
	@Override
	protected void configureServlets() {
		// Configured according to: http://stuffthathappens.com/blog/2009/09/14/guice-with-gwt/

		filter("/openidtest/*").through(AuthFilter.class);
		
		serve("/openidtest/GWT.rpc").with(GuiceRemoteServiceServlet.class);

		serve("/openidtest/login/*").with(LoginServlet.class);
		serve("/openidtest/greet/*").with(GreetingServiceImpl.class);
		serve("/openidtest/verifyLoginRedirect/*").with(VerifyLoginRedirectServlet.class);
		
		bind(LoginService.class).to(LoginServlet.class);
		bind(GreetingService.class).to(GreetingServiceImpl.class);

		// The minimum requirements for discovery-only (non-oauth)
		try {
			Message.addExtensionFactory(AxMessage2.class);
		} catch (MessageException e) {
			throw new CreationException(null);
		}

		try {
			Message.addExtensionFactory(HybridOauthMessage.class);
		} catch (MessageException e) {
			throw new CreationException(null);
		}

		bind(ConsumerManager.class).toProvider(ConsumerManagerProvider.class)
				.in(Scopes.SINGLETON);

		bind(ConsumerAssociationStore.class).to(
				InMemoryConsumerAssociationStore.class).in(Scopes.SINGLETON);

		bind(HostMetaFetcher.class).toProvider(HostMetaFetcherProvider.class)
				.in(Scopes.SINGLETON);
	}

	@Singleton
	private static class HostMetaFetcherProvider implements
			Provider<HostMetaFetcher> {

		private final HostMetaFetcher fetcher;

		@Inject
		public HostMetaFetcherProvider(DefaultHostMetaFetcher fetcher1,
				GoogleHostedHostMetaFetcher fetcher2) {

			// we're waiting at most 10 seconds for the two host-meta fetchers
			// to find
			// a host-meta
			long hostMetatimeout = 10; // seconds.

			// we're supplying at most 20 threads for host-meta fetchers
			ExecutorService executor = Executors.newFixedThreadPool(20);

			fetcher = new ParallelHostMetaFetcher(executor, hostMetatimeout,
					fetcher1, fetcher2);
		}

		public HostMetaFetcher get() {
			return fetcher;
		}
	}

	class GoogleHostedHostMetaFetcher extends UrlHostMetaFetcher {

		private static final String SOURCE_PARAM = "step2.hostmeta.google.source";
		private static final String DEFAULT_SOURCE = "https://www.google.com";
		private static final String HOST_META_PATH = "/accounts/o8/.well-known/host-meta";
		private static final String DOMAIN_PARAM = "hd";

		@Inject
		public GoogleHostedHostMetaFetcher(HttpFetcher fetcher) {
			super(fetcher);
		}

		@Override
		protected URI getHostMetaUriForHost(String host)
				throws URISyntaxException {
			String source = System.getProperty(SOURCE_PARAM, DEFAULT_SOURCE);
			String uri = source + HOST_META_PATH + "?" + DOMAIN_PARAM + "="
					+ host;
			return new URI(uri);
		}
	}

}