package com.github.jmchilton.blend4j.galaxy;

import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;


/**
 * Default and simplest possible implementation of WebResourceFactory.
 *
 * @author John Chilton (jmchilton@gmail.com)
 *
 */
public class DefaultWebResourceFactoryImpl implements WebResourceFactory {
  private static final boolean DEBUG = false;
  public static String API_PATH = "api";
  private String url;
  private String key;
  private boolean debug;

  public DefaultWebResourceFactoryImpl(final String url, final String key, final boolean debug) {
    this.url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    this.key = key;
    this.debug = debug;
  }

  public DefaultWebResourceFactoryImpl(final String url, final String key) {
    this(url, key, DEBUG);
  }

  /**
   * @return A Jersey WebResource setup to target http://<galaxy_url>/api and with the key query parameter set to
   * desired Galaxy key.
   */
  public WebTarget get() {
    final String apiKey = getApiKey();
    WebTarget resource = getRawWebResource();
    if(apiKey != null) {
      resource = resource.queryParam("key", apiKey);
    }
    return resource;
  }
  
  public String getUrl() {
    return url;
  }

  @Deprecated
  public String getGalaxyUrl() {
    return getUrl();
  }

  public String getApiKey() {
    return key;
  }

  /**
   * Return a configured Jersey client for Galaxy API code to interact with. If this method is overridden ensure
   * FEATURE_POJO_MAPPING is enabled.
   *
   * clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
   *
   * @return Jersey client.
   *
   */
  protected javax.ws.rs.client.Client getJerseyClient() {
    final ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
    com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create(clientConfig);
    if(this.debug) {
      client.addFilter(new LoggingFilter(System.out));
    }
    return client;
  }
  
  /**
   * Build Jersey client, and get {@link WebTarget} targeting Galaxy API.
   * Use public method get() to get higher level WebResource with key populated
   * for instance.
   * 
   * @return Web resource corresponding to target Galaxy API.
   */
  protected WebTarget getRawWebResource() {
    final com.sun.jersey.api.client.Client client = getJerseyClient();
    return client.resource(getUrl()).path(API_PATH);
  }
}
