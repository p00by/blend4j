package com.github.jmchilton.blend4j;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jmchilton.blend4j.exceptions.ResponseException;
import com.github.jmchilton.blend4j.exceptions.SerializationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

public class BaseClient {
  protected final ObjectMapper mapper = new ObjectMapper();
  protected final WebTarget webResource;

  public BaseClient(final WebTarget baseWebResource,
                    final String module) {
    this.webResource = baseWebResource.path(module);
  }
  
  protected ClientResponse create(final Object object) {
    return create(object, true);
  }

  protected ClientResponse create(final Object object, final boolean checkResponse) {
    final ClientResponse response = create(getWebResource(), object, checkResponse);
    return response;
  }

  protected ClientResponse create(final WebTarget webResource, final Object object) {
    return create(webResource, object, true);
  }
  
  protected ClientResponse create(final WebTarget webResource, final Object object, final boolean checkResponse) {
    final ClientResponse response = webResource.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .post(Entity.entity(object, MediaType.APPLICATION_JSON), ClientResponse.class);
    if(checkResponse) {
      this.checkResponse(response);
    }
    return response;
  }

  protected <T> List<T> get(final WebTarget webResource, final TypeReference<List<T>> typeReference) {
    final String json = getJson(webResource);
    return readJson(json, typeReference);
  }

  protected <T> List<T> get(final TypeReference<List<T>> typeReference) {
    return get(getWebResource(), typeReference);
  }
  
  /**
   * Gets the response for a DELETE request.
   * @param webResource The {@link WebTarget} to send the request to.
   * @return The {@link ClientResponse} for this request.
   * @throws ResponseException If the response was not successful.
   */
  protected ClientResponse deleteResponse(final WebTarget webResource) {
    return deleteResponse(webResource, true);
  }

  /**
   * Gets the response for a DELETE request.
   * @param webResource The {@link WebTarget} to send the request to.
   * @param requestEntity The request entity.
   * @return The {@link ClientResponse} for this request.
   * @throws ResponseException If the response was not successful.
   */
  protected ClientResponse deleteResponse(final WebTarget webResource, java.lang.Object requestEntity) {
    return deleteResponse(webResource, requestEntity, true);
  }

  /**
   * Gets the response for a DELETE request.
   * @param webResource The {@link WebTarget} to send the request to.
   * @param requestEntity The request entity.
   * @param checkResponse True if an exception should be thrown on failure, false otherwise.
   * @return The {@link ClientResponse} for this request.
   * @throws ResponseException If the response was not successful.
   */
  protected ClientResponse deleteResponse(final WebTarget webResource, java.lang.Object requestEntity, final boolean checkResponse) {
    final ClientResponse response = webResource.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class, requestEntity);
    if(checkResponse) {
      this.checkResponse(response);
    }
    return response;
  }

  /**
   * Gets the response for a DELETE request.
   * @param webResource The {@link WebTarget} to send the request to.
   * @param checkResponse True if an exception should be thrown on failure, false otherwise.
   * @return The {@link ClientResponse} for this request.
   * @throws ResponseException If the response was not successful.
   */
  protected ClientResponse deleteResponse(final WebTarget webResource, final boolean checkResponse) {
    final ClientResponse response = webResource.request(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
    if(checkResponse) {
      this.checkResponse(response);
    }
    return response;
  }
  
  protected ClientResponse getResponse(final WebTarget webResource) {
    return getResponse(webResource, true);
  }
  
  protected ClientResponse getResponse(final WebTarget webResource, final boolean checkResponse) {
    final ClientResponse response = webResource.request(MediaType.APPLICATION_JSON).get(ClientResponse.class);
    if(checkResponse) {
      this.checkResponse(response);
    }
    return response;
  }
  
  protected String getJson(final WebTarget webResource) {
    return getJson(webResource, true);
  }

  protected String getJson(final WebTarget webResource, final boolean checkResponse) {
    final ClientResponse response = getResponse(webResource, checkResponse);
    final String json = response.readEntity(String.class);
    return json;
  }

  protected void checkResponse(final ClientResponse response) {
    final Family family = response.getClientResponseStatus().getFamily();
    if(family != Family.SUCCESSFUL) {
      final ResponseException exception = buildResponseException(response);
      throw exception;
    }
  }
  
  protected ResponseException buildResponseException(final ClientResponse clientResponse) {
    final ResponseException exception = new ResponseException(clientResponse);
    return exception;
  }
  
  protected WebTarget getWebResource() {
    return webResource;
  }

  protected WebTarget getWebResource(final String id) {
    return path(id);
  }

  protected WebTarget getWebResourceContents(final String id) {
    return getWebResource(id).path("contents");
  }
  
  protected WebTarget path(final String path) {
    return getWebResource().path(path);
  }

  protected <T> TypeReference<List<T>> listTypeReference(final Class<T> clazz) {
    return new TypeReference<List<T>>() {
    };
  }

  protected ClientResponse multipartPost(final WebTarget resource, final Map<String, Object> fields, final Iterable<BodyPart> bodyParts) {
    final WebTarget.Builder builder = resource.type(MediaType.MULTIPART_FORM_DATA_TYPE);
    final FormDataMultiPart multiPart = new FormDataMultiPart();
    for(final Map.Entry<String, Object> fieldEntry : fields.entrySet()) {
      final FormDataBodyPart bp = new FormDataBodyPart(fieldEntry.getKey(), fieldEntry.getValue(), MediaType.APPLICATION_JSON_TYPE);
      multiPart.bodyPart(bp);
    }
    for(final BodyPart bodyPart : bodyParts) {
      bodyPart.setMediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE);
      multiPart.bodyPart(bodyPart);
    }
    return builder.post(ClientResponse.class, multiPart);
  }

  protected Iterable<BodyPart> prepareUpload(final File file) {
    return prepareUploads(Arrays.asList(file));
  }

  protected Iterable<BodyPart> prepareUploads(final Iterable<File> files) {
    final List<BodyPart> bodyParts = new ArrayList<BodyPart>();
    for(final File file : files) {
      final String paramName = "files_0|file_data";
      final FileDataBodyPart fdbp = new FileDataBodyPart(paramName, file);
      bodyParts.add(fdbp);
    }
    return bodyParts;
  }

  protected <T> T readJson(final String json, final TypeReference<T> typeReference) {
    try {
      return mapper.readValue(json, typeReference);
    } catch(IOException e) {
      throw new SerializationException(e);
    }
  }
  
  protected <T> T readJson(final String json, final Class<T> clazz) {
    try {
      return mapper.readValue(json, clazz);
    } catch(IOException e) {
      throw new SerializationException(e);
    }
  }

  protected <T> T read(final ClientResponse response, final TypeReference<T> typeReference) {
    final String json = response.readEntity(String.class);
    return readJson(json, typeReference);
  }

  protected <T> T read(final ClientResponse response, final Class<T> clazz) {
    final String json = response.readEntity(String.class);
    return readJson(json, clazz);
  }

  protected <T> T show(final String id, Class<T> clazz) {
    return getWebResource(id).get(clazz);
  }

  protected String write(final Object object) {
    try {
      return mapper.writer().writeValueAsString(object);
    } catch(IOException e) {
      throw new SerializationException(e);
    }
  }

}
