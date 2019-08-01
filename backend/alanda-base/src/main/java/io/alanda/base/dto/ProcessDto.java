/**
 * 
 */
package io.alanda.base.dto;

import java.util.List;


/**
 * @author jlo
 *
 */
public class ProcessDto {

  private String processInstanceId;
  private String processDefinitionId;
  private String processName;
  private String businessKey;
  
  private List<PmcTaskDto> tasks;
  
  public String getProcessInstanceId() {
    return processInstanceId;
  }
  
  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }
  
  public String getProcessDefinitionId() {
    return processDefinitionId;
  }
  
  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }
  
  public String getProcessName() {
    return processName;
  }
  
  public void setProcessName(String processName) {
    this.processName = processName;
  }
  
  public String getBusinessKey() {
    return businessKey;
  }
  
  public void setBusinessKey(String businessKey) {
    this.businessKey = businessKey;
  }

  public List<PmcTaskDto> getTasks() {
    return tasks;
  }

  public void setTasks(List<PmcTaskDto> tasks) {
    this.tasks = tasks;
  }
  
  

}
