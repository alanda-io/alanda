package io.alanda.base.dto;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public class EmailDto implements Serializable {

  private static final long serialVersionUID = -420263997865123551L;
  
  private String from;

  private String subject;

  private String message;

  private String messageHeader;

  private String[] receiver;

  private String cc;
  
  private String bcc;

  private AttachmentDto attachment;

  public EmailDto() {
  }
  
  public EmailDto(String subject, String message, String[] receiver) {
    this.subject = subject;
    this.message = message;
    this.receiver = receiver;
  }
  
  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessageHeader() {
    return messageHeader;
  }

  public void setMessageHeader(String messageHeader) {
    this.messageHeader = messageHeader;
  }

  public String[] getReceiver() {
    return receiver;
  }

  public void setReceiver(String... receiver) {
    this.receiver = receiver;
  }

  public String getCc() {
    return cc;
  }

  public void setCc(String cc) {
    this.cc = cc;
  }

  public AttachmentDto getAttachment() {
    return attachment;
  }

  public void setAttachment(AttachmentDto attachment) {
    this.attachment = attachment;
  }
  
  
  public String getFrom() {
    return from;
  }

  
  public void setFrom(String from) {
    this.from = from;
  }

  
  public String getBcc() {
    return bcc;
  }

  
  public void setBcc(String bcc) {
    this.bcc = bcc;
  }

  @Override
  public String toString() {
    return "EmailDto [subject=" +
      subject +
      ", message=" +
      message +
      ", messageHeader=" +
      messageHeader +
      ", receiver=" +
      Arrays.toString(receiver) +
      ", cc=" +
      cc +
      ", attachment=" +
      attachment +
      "]";
  }

}
