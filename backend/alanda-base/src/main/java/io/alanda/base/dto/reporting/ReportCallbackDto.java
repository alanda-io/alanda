/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dto.reporting;

import java.util.Map;

/**
 *
 * @author developer
 */
public class ReportCallbackDto {
  
  String name;
  
  Map<String, Object> params;

  public ReportCallbackDto() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }
  
}
