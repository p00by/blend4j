package com.github.jmchilton.blend4j.exceptions;

import org.glassfish.jersey.client.ClientResponse;

import com.github.jmchilton.blend4j.util.MoreObjects;

public class ResponseException extends ApiException {
  private final Integer statusCode;
  private final String rawResponse;
  
  public ResponseException(final ClientResponse clientResponse) {
    super();
    this.statusCode = clientResponse.getStatus();
    this.rawResponse = clientResponse.readEntity(String.class);
  }

  protected MoreObjects.ToStringHelper toStringHelper() {
    return MoreObjects
            .toStringHelper(getClass())
            .add("status", statusCode)
            .add("responseBody", rawResponse);
    
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }

  public String getResponseBody() {
    return rawResponse;
  }

  public Integer getStatusCode() {
    return statusCode;
  }
  
}
