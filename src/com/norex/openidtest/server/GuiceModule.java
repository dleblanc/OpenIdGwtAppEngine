package com.norex.openidtest.server;

import java.net.*;
import java.util.concurrent.*;

import org.openid4java.consumer.*;
import org.openid4java.message.*;

import com.google.inject.*;
import com.google.step2.discovery.*;
import com.google.step2.http.HttpFetcher;
import com.google.step2.hybrid.HybridOauthMessage;
import com.google.step2.openid.ax2.AxMessage2;
import com.google.step2.servlet.ConsumerManagerProvider;

public class GuiceModule extends AbstractModule {
	@Override
	protected void configure() {

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

	    bind(HostMetaFetcher.class)
        .toProvider(HostMetaFetcherProvider.class).in(Scopes.SINGLETON);
	    
	    
	    // OAuth related dependencies follow
	}

	  @Singleton
	  private static class HostMetaFetcherProvider
	      implements Provider<HostMetaFetcher> {

	    private final HostMetaFetcher fetcher;

	    @Inject
	    public HostMetaFetcherProvider(
	        DefaultHostMetaFetcher fetcher1,
	        GoogleHostedHostMetaFetcher fetcher2) {

	      // we're waiting at most 10 seconds for the two host-meta fetchers to find
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
		  protected URI getHostMetaUriForHost(String host) throws URISyntaxException {
		    String source = System.getProperty(SOURCE_PARAM, DEFAULT_SOURCE);
		    String uri = source + HOST_META_PATH + "?" + DOMAIN_PARAM + "=" + host;
		    return new URI(uri);
		  }
		}

}
