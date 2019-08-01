/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.connector;

import java.util.List;

import io.alanda.base.dto.Link;

/**
 *
 * @author developer
 */
public interface LinkConnector {
  
  public String getLinkType();
  
  public Link getLinkObjectById(Long id);
  
  public List<? extends Link> getLinks();
  
}
