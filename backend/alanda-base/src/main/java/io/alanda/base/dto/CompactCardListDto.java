/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dto;

/**
 *
 * @author developer
 */
public class CompactCardListDto {
  
  private Long guid;
  
  private String idName;

  public CompactCardListDto() {
  }
  
  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }
    
}
