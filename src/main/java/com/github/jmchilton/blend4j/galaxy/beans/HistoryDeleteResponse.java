package com.github.jmchilton.blend4j.galaxy.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines the response from a delete request to Galaxy.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class HistoryDeleteResponse extends GalaxyObject {
  @JsonProperty("deleted")
  private boolean deleted;
  
  @JsonProperty("purged")
  private boolean purged;

  /**
   * Whether or not this object is deleted.
   * @return Whether or not this object is deleted.
   */
  public boolean getDeleted() {
    return deleted;
  }

  /**
   * Whether or not this object is purged.
   * @return Whether or not this object is purged.
   */
  public boolean getPurged() {
    return purged;
  }  
}
