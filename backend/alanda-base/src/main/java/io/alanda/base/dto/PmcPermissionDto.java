/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dto;

import java.util.Objects;

/**
 *
 * @author developer
 */
public class PmcPermissionDto {
  
  private Long guid;
  
  private String key;
  
  private String source;

  public PmcPermissionDto() {
  }

  public PmcPermissionDto(Long guid, String key) {
    this.guid = guid;
    this.key = key;
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getSource() {
    if (source == null)
      return "";
    else 
      return source;
  }

  public void setSource(String source) {
    this.source = source;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PmcPermissionDto other = (PmcPermissionDto) obj;
    if (!Objects.equals(this.key, other.key)) {
      return false;
    }
    return Objects.equals(this.guid, other.guid);
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 89 * hash + Objects.hashCode(this.guid);
    hash = 89 * hash + Objects.hashCode(this.key);
    return hash;
  }
  
}
