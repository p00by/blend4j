package com.github.jmchilton.blend4j.galaxy;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInvocationInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInvocationOutputs;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientResponse;

class WorkflowsClientImpl extends Client implements WorkflowsClient {
  public WorkflowsClientImpl(GalaxyInstanceImpl galaxyInstance) {
    super(galaxyInstance, "workflows");
  }

  public List<Workflow> getWorkflows() {
    return get(new TypeReference<List<Workflow>>() {
    });
  }

  public ClientResponse showWorkflowResponse(final String id) {
    return super.show(id, ClientResponse.class);
  }

  public WorkflowDetails showWorkflow(final String id) {
    return super.show(id, WorkflowDetails.class);
  }

  public String exportWorkflow(final String id) {
    WebTarget webResource = getWebResource().path("download").path(id);
    return webResource.request().get(String.class);
  }

  @Deprecated
  public ClientResponse runWorkflowResponse(WorkflowInputs workflowInputs) {
    return super.create(workflowInputs);
  }

  @Deprecated
  public WorkflowOutputs runWorkflow(final WorkflowInputs workflowInputs) {
    return runWorkflowResponse(workflowInputs).readEntity(WorkflowOutputs.class);
  }

  public ClientResponse runWorkflowInvocationResponse(WorkflowInvocationInputs workflowInvocationInputs) {
    return create(getWebResource().path(workflowInvocationInputs.getWorkflowId()).path("invocations"),workflowInvocationInputs);
  }

  public WorkflowInvocationOutputs invokeWorkflow(final WorkflowInvocationInputs workflowInvocationInputs) {
    return runWorkflowInvocationResponse(workflowInvocationInputs).readEntity(WorkflowInvocationOutputs.class);
  }

  public ClientResponse importWorkflowResponse(final String json, final boolean publish) {
    final String payload = String.format("{\"workflow\": %s, \"publish\": %s}", json, publish);
    return create(getWebResource().path("upload"), payload);
  }

  public Workflow importWorkflow(String json) {
    return importWorkflowResponse(json, false).readEntity(Workflow.class);
  }

  public Workflow importWorkflow(String json, boolean publish) {
    return importWorkflowResponse(json, publish).readEntity(Workflow.class);
  }

  @Override
  public ClientResponse deleteWorkflowRequest(String id) {
    return deleteResponse(getWebResource(id));
  }
}
