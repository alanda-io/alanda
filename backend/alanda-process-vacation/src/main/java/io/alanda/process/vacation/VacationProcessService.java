/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.process.vacation;

import javax.inject.Named;

import io.alanda.base.process.BaseProcessService;

/**
 *
 * @author developer
 */
@Named("vacationPS")
public class VacationProcessService extends BaseProcessService {
  
  public void informTeamMembers() {

  }

  public String getProjectCreator() {
    return pmcProjectData.getPmcProject().getCreateUser().toString();
  }

  public void processVariableCheck(String pvName, boolean defaultValue) {
    Boolean value = pmcProjectData.getVariable(pvName);
    if (value == null) {
      pmcProjectData.setVariable(pvName, defaultValue);
    }
  }
}
