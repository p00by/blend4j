package com.github.jmchilton.blend4j.galaxy;

import java.util.HashMap;

import org.glassfish.jersey.client.ClientResponse;

public class SearchClientImpl extends Client implements SearchClient {
  
  SearchClientImpl(GalaxyInstanceImpl galaxyInstance) {
    super(galaxyInstance, "search");
  }
  
  public SearchResponse search(String searchQuery) {
    final HashMap<String, String> postObject = new HashMap<String, String>();
    postObject.put("query", searchQuery);
    final ClientResponse response = super.create(postObject);
    return response.readEntity(SearchResponse.class);
  }

  
}
