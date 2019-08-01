/**
 * 
 */
package io.alanda.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcUserDto;

// TODO: adapt to user concept of camunda
/**
 * @author jlo
 */
public class UserContext {

  private final static Logger log = LoggerFactory.getLogger(UserContext.class);

  private static InheritableThreadLocal<PmcUserDto> tl = new InheritableThreadLocal<PmcUserDto>();

  public static void setUser(PmcUserDto user) {
    //    log.info("Setting TL for user: " + user != null ? user.toString() : "[NULL]");
    tl.set(user);
  }

  public static PmcUserDto getUser() {
    PmcUserDto user = tl.get();
    if (user == null) {
      user = new PmcUserDto();
      user.setGuid(1L);
    }
    return user;
  }

  public static void remove() {
    PmcUserDto user = tl.get();
    //    log.info("Removing TL for user: " + user != null ? user.toString() : "[NULL]");
    tl.remove();
  }

}
