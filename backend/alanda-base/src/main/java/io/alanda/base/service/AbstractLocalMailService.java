package io.alanda.base.service;

import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import io.alanda.base.dto.EmailDto;
import io.alanda.base.listener.OnChangeListener;
import io.alanda.base.util.MailUtil;

public abstract class AbstractLocalMailService implements MailService, OnChangeListener {

  private static final Logger logger = LoggerFactory.getLogger(AbstractLocalMailService.class);

  @Inject
  private ConfigService configService;

  protected JavaMailSenderImpl mailSender;
  private MailUtil mailUtil;

  public void smtpSend(EmailDto email) {
    logger.info("About to send message: " + email);
    if (mailUtil != null) {
      mailUtil.sendMimeMailFile(email, true);
    } else {
      logger.info("Send mail was called but no mail will be sent! Mail server not configured!");
    }
  }

  @Override
  public void onChange() {
    String host = configService.getProperty("mail.host");
    if (host != null) {
      mailSender.setHost(host);
      mailSender.setPort(Integer.parseInt(configService.getProperty("mail.port")));
      mailSender.setUsername(configService.getProperty("mail.username"));
      mailSender.setPassword(configService.getProperty("mail.password"));
      
      // don't error if one of the email recipients is invalid
      Properties prop = new Properties();
      prop.setProperty("mail.smtp.sendpartial", "true");
      mailSender.setJavaMailProperties(prop);
      
      mailUtil = new MailUtil();
      mailUtil.setDefaultFromAddress(
        configService.getProperty("mail.defaultSender") != null ? configService.getProperty("mail.defaultSender") : "pmc@bpmasters.com");
      mailUtil.setMailSender(mailSender);
      mailUtil.setTestRecipient(configService.getProperty("mail.testRecipient"));

      logger.info(
        "Configured Mail Server to " +
          mailSender.getHost() +
          ":" +
          mailSender.getPort() +
          ", User:" +
          mailSender.getUsername() +
          ", sender:" +
          mailUtil.getDefaultFromAddress() +
          ", testRecipient:" +
          mailUtil.getTestRecipient());
    } else {
      logger.info("No mail sender configured for pmc. No mails will be sent using the PMC backend!");
    }
  }


}
