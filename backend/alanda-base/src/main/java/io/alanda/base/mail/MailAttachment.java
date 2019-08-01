/**
 * 
 */
package io.alanda.base.mail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.alanda.base.entity.AbstractAuditEntity;


/**
 * @author developer
 *
 */
@Entity
@Table(name = "MAIL_ATTACHMENT")
public class MailAttachment extends AbstractAuditEntity {

  @ManyToOne()
  @JoinColumn(name = "REF_MAIL")
  private Mail mail;

  @Lob
  private byte[] blob;

  private String name;

  @Column(name = "MIME_TYPE")
  private String mimeType;

  @Transient
  private boolean mainMessage;

  public Mail getMail() {
    return mail;
  }

  public void setMail(Mail mail) {
    this.mail = mail;
  }

  public byte[] getBlob() {
    return blob;
  }

  public void setBlob(byte[] blob) {
    this.blob = blob;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public boolean isMainMessage() {
    return mainMessage;
  }

  public void setMainMessage(boolean mainMessage) {
    this.mainMessage = mainMessage;
  }


}
