/**
 * 
 */
package io.alanda.base.connector;

import io.alanda.base.dto.PmcUserDto;

/**
 * @author jlo
 */
public interface PmcProjectBatchCreateDelegate {

  boolean isUserTransform();

  PmcUserDto tranformUser(String roleValue);

}
