/**
 * 
 */
package io.alanda.base.dto;

/**
 * @author jlo
 */
public interface RefObject {

  public String getIdName();

  public Long getRefObjectId();

  public String getRefObjectType();

  default public String getObjectName() {
    return getIdName();
  }

}
