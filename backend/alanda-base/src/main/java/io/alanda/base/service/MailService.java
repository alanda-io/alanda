package io.alanda.base.service;

import io.alanda.base.dto.EmailDto;

/**
 * Send e-mails with Osiris
 * 
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public interface MailService {

  /**
   * Send an e-mail
   * 
   * @param email
   */
  void sendEmail(EmailDto email);

}
