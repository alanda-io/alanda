/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.security;

import io.alanda.base.dto.PmcProjectDto;

/**
 * @author developer
 */
public interface ProjectTypeAuthLoader {
  
  public final String ROLE_PREFIX = "role_";

  public String[] getTypeNames();

  public String getKeyForType(PmcProjectDto project);

}
