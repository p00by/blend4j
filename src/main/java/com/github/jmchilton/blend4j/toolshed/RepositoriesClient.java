package com.github.jmchilton.blend4j.toolshed;

import com.github.jmchilton.blend4j.toolshed.beans.Repository;
import com.github.jmchilton.blend4j.toolshed.beans.RepositoryDetails;
import com.github.jmchilton.blend4j.toolshed.beans.RepositoryRevision;
import java.util.List;

import org.glassfish.jersey.client.ClientResponse;

public interface RepositoriesClient {

  List<RepositoryDetails> getRepositories();
  
  RepositoryDetails showRepository(final String repositoryId);
  
  ClientResponse getInstallableRevisionsRequest(final Repository repository);
  
  List<String> getInstallableRevisions(final Repository repository);
  
  // API returns a list (length 3) of heterogenous data structures. Maybe should wait until a cleaned up
  // version of this is available.
  ClientResponse getRepositoryRevisionInstallInfoRequest(final RepositoryRevision revision);
  
}
