package com.github.jmchilton.blend4j.galaxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.jmchilton.blend4j.galaxy.beans.InstallableRepositoryRevision;
import com.github.jmchilton.blend4j.galaxy.beans.InstalledRepository;
import com.github.jmchilton.blend4j.galaxy.beans.RepositoryInstall;
import com.github.jmchilton.blend4j.galaxy.beans.RepositoryWorkflow;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientResponse;

class ToolShedRepositoriesClientImpl extends Client implements ToolShedRepositoriesClient {
  private static final TypeReference<List<InstalledRepository>> TOOL_SHED_REPOSITORY_LIST_TYPE_REFERENCE = new TypeReference<List<InstalledRepository>>() {
  };
  private static final TypeReference<List<RepositoryWorkflow>> TOOL_SHED_REPOSITORY_WORKFLOW_LIST_TYPE_REFERENCE = 
    new TypeReference<List<RepositoryWorkflow>>() {
    };
  private static final TypeReference<List<Workflow>> WORKFLOW_LIST_TYPE_REFERENCE = 
    new TypeReference<List<Workflow>>() {
    };
  

  ToolShedRepositoriesClientImpl(GalaxyInstanceImpl galaxyInstance) {
    super(galaxyInstance, "tool_shed_repositories");
  }

  public List<InstalledRepository> getRepositories() {
    return super.get(TOOL_SHED_REPOSITORY_LIST_TYPE_REFERENCE);
  }

  public InstalledRepository showRepository(final String toolShedId) {
    return super.show(toolShedId, InstalledRepository.class);
  }

  public ClientResponse installRepositoryRequest(final RepositoryInstall install) {
    final WebTarget resource = super.webResource.path("new").path("install_repository_revision");
    return super.create(resource, install);
  }
  
  public List<InstalledRepository> installRepository(final RepositoryInstall install) {
    final ClientResponse response = this.installRepositoryRequest(install);
    return super.read(response, TOOL_SHED_REPOSITORY_LIST_TYPE_REFERENCE);
  }
  
  public ClientResponse repairRepositoryRequest(final InstallableRepositoryRevision repositoryIdentifier) {
    final WebTarget resource = super.webResource.path("repair_repository_revision");
    return super.create(resource, repositoryIdentifier);
  }

  public ClientResponse exportedWorkflowsRequest(String toolShedId) {
    final WebTarget resource = super.webResource.path(toolShedId).path("exported_workflows");
    return resource.request().get(ClientResponse.class);
  }

  public List<RepositoryWorkflow> exportedWorkflows(String toolShedId) {
    return super.read(this.exportedWorkflowsRequest(toolShedId), 
                      TOOL_SHED_REPOSITORY_WORKFLOW_LIST_TYPE_REFERENCE);
  }

  public ClientResponse importWorkflowRequest(String toolShedId, int index) {
    final WebTarget resource = super.webResource.path(toolShedId).path("import_workflow");
    final HashMap<String, Object> postObject = new HashMap<String, Object>();
    postObject.put("index", index);
    return super.create(resource, postObject);
  }

  public Workflow importWorkflow(String toolShedId, int index) {
    final ClientResponse response = this.importWorkflowRequest(toolShedId, index);
    return response.readEntity(Workflow.class);
  }

  public ClientResponse importWorkflowsRequest(String toolShedId) {
    final WebTarget resource = super.webResource.path(toolShedId).path("import_workflows");
    return super.create(resource);
  }

  public List<Workflow> importWorkflows(String toolShedId) {
    return read(importWorkflowsRequest(toolShedId), WORKFLOW_LIST_TYPE_REFERENCE);
  }

}
