package io.alanda.base.service.impl;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import io.alanda.base.dto.EmailDto;
import io.alanda.base.service.AbstractLocalMailService;
import io.alanda.base.service.ConfigService;


/**
 * @author eka
 */
@ApplicationScoped
@Alternative
@Priority(20)
public class AsyncMailServiceImpl extends AbstractLocalMailService {

  @Inject
  private Event<EmailDto> event;

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
    event.fire(email);
  }

  @SuppressWarnings("unused") // this methods is only called when an event is fired
  private void onMailJob(@Observes(during = TransactionPhase.AFTER_SUCCESS) EmailDto email) {
    this.smtpSend(email);
  }


}
