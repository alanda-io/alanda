package io.alanda.base.util;

import java.util.Arrays;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import io.alanda.base.dto.AttachmentDto;
import io.alanda.base.dto.EmailDto;

/**
 * The Class MailUtil
 *
 * @author stephan
 */
public class MailUtil {

  private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

  /**
   * The mail sender.
   */
  private JavaMailSender mailSender;

  /**
   * The default from address.
   */
  private String defaultFromAddress;

  /**
   * the test recipient (if set gets all the Mails)
   */
  private String testRecipient;

  /**
   * The log.
   */
  private Log log = LogFactory.getLog(this.getClass());

  /**
   * Instantiates a new mail util
   */
  public MailUtil() {
    super();
  }

  /**
   * Gets the mail sender.
   *
   * @return Returns the mailSender.
   */
  public JavaMailSender getMailSender() {
    return mailSender;
  }

  /**
   * Sets the mail sender.
   *
   * @param mailSender The mailSender to set.
   */
  public void setMailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendMimeMailFile(final EmailDto dto, boolean background) {

    Runnable r = new Runnable() {

      @Override
      public void run() {
        send();
      }

      public MimeMessage send() {
        String subject = dto.getSubject();
        String from = dto.getFrom();
        String[] to = dto.getReceiver();
        String cc = dto.getCc();
        String bcc = dto.getBcc();
        String content = dto.getMessage();
        String contentType = dto.getMessageHeader();
        AttachmentDto[] attachments = (dto.getAttachment() != null) ? new AttachmentDto[] {dto.getAttachment()} : null;

        MimeMessage message = mailSender.createMimeMessage();
        try {

          if (StringUtils.isNotBlank(contentType)) {
            message.addHeaderLine(contentType);
          }

          boolean multi = attachments != null;
          MimeMessageHelper helper = new MimeMessageHelper(message, multi, "UTF-8");

          String senderEmailAddress = StringUtils.isNotEmpty(from) ? from : defaultFromAddress;
          helper.setFrom(senderEmailAddress);
          logger.info("Mail sent from :" + senderEmailAddress);

          String[] toFiltered = Arrays.stream(to).filter(s -> StringUtils.isNotBlank(s)).toArray(String[]::new);

          if (StringUtils.isNotBlank(testRecipient)) {
            logger.info("mail.testRecipient Found: about to send email to " + testRecipient);
            helper.setTo(testRecipient.split(";"));
            String skippedTo = "<p>Skipped recipients: " + StringUtils.join(toFiltered, " ") + "</p>\n";

            String skippedCC = StringUtils.isNotEmpty(cc) ? "<p>Skipped cc recipients: " + cc + "</p>\n" : "";
            String skippedBCC = StringUtils.isNotEmpty(bcc) ? "<p>Skipped bcc recipients: " + bcc + "</p>\n" : "";
            helper.setText(skippedTo + skippedCC + skippedBCC + content, true);
          } else {
            helper.setTo(toFiltered);
            if (StringUtils.isNotEmpty(cc)) {
              helper.setCc(cc);
            }
            if (StringUtils.isNotEmpty(bcc)) {
              helper.setBcc(bcc);
            }
            helper.setText(content, true);
          }

          helper.setSubject(subject);

          if (attachments != null) {
            for (int i = 0; i < attachments.length; i++ ) {
              String name = attachments[i].getName();
              if (name.indexOf("\\") >= 0) {
                name = name.substring(name.lastIndexOf("\\") + 1);
              }
              DataSource att = new ByteArrayDataSource(attachments[i].getContent(), attachments[i].getContentType());
              helper.addAttachment(name, att);
            }
          }
          mailSender.send(message);
          String realRecipient = !StringUtils.isBlank(testRecipient) ? testRecipient : StringUtils.join(toFiltered, " ");
          log.info("Mail sent to " + realRecipient);
          return message;

        } catch (SendFailedException e) {
          log.warn("Error sending mail", e);
        } catch (MessagingException e) {
          //			e.printStackTrace();
          log.error("Error sending mail", e);
          //		} catch (UnsupportedEncodingException e) {
          //		    log.error("Error constructing mail", e);
          //			e.printStackTrace();
        } catch (RuntimeException ex) {
          log.error("Error sending mail", ex);
          //			ex.printStackTrace();
        } catch (Exception e) {
          log.error("Error sending mail", e);
          //
          //			e.printStackTrace();
        }
        return null;
      }
    };

    if (background) {
      Thread t = new Thread(r);
      t.setDaemon(false);
      t.start();

    } else {
      r.run();
    }
  }

  /**
   * Gets the default from address.
   *
   * @return the default from address
   */
  public String getDefaultFromAddress() {
    return defaultFromAddress;
  }

  /**
   * Sets the default from address.
   *
   * @param defaultFromAddress the new default from address
   */
  public void setDefaultFromAddress(String defaultFromAddress) {
    this.defaultFromAddress = defaultFromAddress;
  }

  public String getTestRecipient() {
    return testRecipient;
  }

  public void setTestRecipient(String testRecipient) {
    this.testRecipient = testRecipient;
  }
}
