/**
 * 
 */
package io.alanda.base.mail;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.alanda.base.entity.AbstractAuditEntity;
import io.alanda.base.type.ProcessResolution;
import io.alanda.base.type.ProcessState;

/**
 * @author jlo
 */
@Entity()
@Table(name = "MAIL")
public class Mail extends AbstractAuditEntity {

  /**
   * name part of the mail correlation key eg. in a mail Subject like [XYZ-1234A3] its the XYZ
   */
  @Column(name = "MODULE_NAME")
  private String moduleName;

  /**
   * Id part of the mail correlation key eg. in a mail Subject like [XYZ-1234A3] its the 1234A3
   */
  @Column(name = "MODULE_ID")
  private String moduleId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ProcessState state;

  @Column(nullable = true)
  @Enumerated(EnumType.STRING)
  private ProcessResolution resolution;

  @Column(name = "ADDRESS_TO")
  private String addressTo;

  @Column(name = "ADDRESS_FROM")
  private String addressFrom;

  @Column(name = "ADDRESS_CC")
  private String addressCc;

  private String subject;

  @Lob()
  private String body;

  @Lob()
  private String mail;

  @Transient
  private String[] lines;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "mail")
  private List<MailAttachment> attachments;

  public ProcessState getState() {
    return state;
  }

  public void setState(ProcessState state) {
    this.state = state;
  }

  public ProcessResolution getResolution() {
    return resolution;
  }

  public void setResolution(ProcessResolution resolution) {
    this.resolution = resolution;
  }

  public String getAddressTo() {
    return addressTo;
  }

  public void setAddressTo(String addressTo) {
    this.addressTo = addressTo;
  }

  public String getAddressFrom() {
    return addressFrom;
  }

  public void setAddressFrom(String addressFrom) {
    this.addressFrom = addressFrom;
  }

  public String getAddressCc() {
    return addressCc;
  }

  public void setAddressCc(String addressCc) {
    this.addressCc = addressCc;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String[] getLines() {
    return lines;
  }

  public void setLines(String[] lines) {
    this.lines = lines;
  }

  public List<MailAttachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<MailAttachment> attachments) {
    this.attachments = attachments;
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
