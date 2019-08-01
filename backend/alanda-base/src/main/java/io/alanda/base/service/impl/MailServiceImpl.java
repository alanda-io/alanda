/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.service.impl;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import io.alanda.base.dto.EmailDto;
import io.alanda.base.service.AbstractLocalMailService;
import io.alanda.base.service.ConfigService;

/**
 * @author developer
 */
@ApplicationScoped
@Alternative
@Priority(10)
public class MailServiceImpl extends AbstractLocalMailService {

  @Inject
  private ConfigService configService;

  @PostConstruct
  public void init() {
    mailSender = new JavaMailSenderImpl();
    onChange();
    configService.addOnChangeListener(this);
  }

  @Override
  public void sendEmail(EmailDto email) {
    this.smtpSend(email);
  }

}
