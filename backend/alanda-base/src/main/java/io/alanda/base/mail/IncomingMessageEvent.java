/**
 * 
 */
package io.alanda.base.mail;

import java.util.List;

/**
 * @author jlo
 */
public class IncomingMessageEvent {

  private List<IncomingMessageDto> incomingMessages;

  /**
   * 
   */
  public IncomingMessageEvent(List<IncomingMessageDto> incomingMessages) {
    this.incomingMessages = incomingMessages;
  }

  public List<IncomingMessageDto> getIncomingMessages() {
    return incomingMessages;
  }

  public void setIncomingMessages(List<IncomingMessageDto> incomingMessages) {
    this.incomingMessages = incomingMessages;
  }

}
