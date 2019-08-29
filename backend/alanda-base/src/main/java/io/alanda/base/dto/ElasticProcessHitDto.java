package io.alanda.base.dto;

public class ElasticProcessHitDto {

  private String processInstanceId;

  private String processDefinitionId;

  private String processDefinitionKey;

  private String startTime;

  private String businessKey;

  //TODO: add missing properties

  private PmcProjectDto project;

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

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getBusinessKey() {
    return businessKey;
  }

  public void setBusinessKey(String businessKey) {
    this.businessKey = businessKey;
  }

  public PmcProjectDto getProject() {
    return project;
  }

  public void setProject(PmcProjectDto project) {
    this.project = project;
  }
}
