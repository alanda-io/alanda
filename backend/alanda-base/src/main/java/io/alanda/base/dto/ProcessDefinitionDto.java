/**
 * 
 */
package io.alanda.base.dto;

/**
 * @author jlo
 */
public class ProcessDefinitionDto {

  String processName;

  String processDefinitionKey;
  

  public ProcessDefinitionDto() {
    super();
  }

  public String getProcessName() {
    return processName;
  }

  public void setProcessName(String processName) {
    this.processName = processName;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public ProcessDefinitionDto(String processName, String processDefinitionKey) {
    super();
    this.processName = processName;
    this.processDefinitionKey = processDefinitionKey;
  }

}
