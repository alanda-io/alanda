/**
 * 
 */
package io.alanda.base.mail;

import io.alanda.base.mail.IncomingMailServiceImpl.PrivateNotificationList;

/**
 * @author jlo
 */
public interface IncomingMailService {

  void processMail(MailServerConfiguration config);
  void internalEventAfterSuccess(PrivateNotificationList internalNotification);

  public void storeMessage(Mail mail);
}
