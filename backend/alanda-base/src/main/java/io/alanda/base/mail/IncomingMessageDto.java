/**
 * 
 */
package io.alanda.base.mail;


/**
 * @author jlo
 */
public class IncomingMessageDto {

  private String mailSubject;

  private Long mailGuid;

  private String moduleName;

  private String moduleId;

  public IncomingMessageDto(String mailSubject, Long mailGuid, String moduleName, String moduleId) {
    super();
    this.mailSubject = mailSubject;
    this.mailGuid = mailGuid;
    this.moduleName = moduleName;
    this.moduleId = moduleId;
  }

  /**
   * 
   */
  public IncomingMessageDto() {
    // TODO Auto-generated constructor stub
  }

  public String getMailSubject() {
    return mailSubject;
  }

  public void setMailSubject(String mailSubject) {
    this.mailSubject = mailSubject;
  }

  public Long getMailGuid() {
    return mailGuid;
  }

  public void setMailGuid(Long mailGuid) {
    this.mailGuid = mailGuid;
  }

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getModuleId() {
    return moduleId;
  }

  public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
  }

}
