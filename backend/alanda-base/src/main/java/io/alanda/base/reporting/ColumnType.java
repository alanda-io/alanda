/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.reporting;

/**
 *
 * @author developer
 */
public enum ColumnType {
  
  GROUP(""), 
  PROJECT("project"), 
  ACTIVITY("sortedActivities"), 
  REFOBJECT("refObject"), 
  MILESTONE("milestone"),
  PMCMILESTONE("pmcmilestone"),
  PROCESS("subProcess"), 
  VARIABLE("sortedVariables"),
  CONTACT("contacts"),
  FIELD(""),
  CONTEXT("context");
  
  private String label;

  private ColumnType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
  
}
