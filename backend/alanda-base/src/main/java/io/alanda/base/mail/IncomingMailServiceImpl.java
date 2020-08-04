/**
 *
 */
package io.alanda.base.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.service.impl.PmcProjectServiceImpl;
import io.alanda.base.type.ProcessState;

/**
 * @author developer
 */
@ApplicationScoped
@Singleton
public class IncomingMailServiceImpl implements IncomingMailService {

  private static final Logger log = LoggerFactory.getLogger(PmcProjectServiceImpl.class);

  private Html2PlainTextConverter html2Plain = new Html2PlainTextConverter();

  class PrivateNotificationList {

    private List<IncomingMessageDto> messages;

    PrivateNotificationList(List<IncomingMessageDto> messages) {
      this.messages = messages;

    }

    public List<IncomingMessageDto> getMessages() {
      return messages;
    }

  }

  @Inject
  private Event<PrivateNotificationList> internalEvent;

  @Inject
  private Event<IncomingMessageEvent> messageEvent;

  @Inject
  private MailDao mailDao;

  @EJB
  private IncomingMailService incomingMailService;

  /**
   * Module can contain everything excepth the "]" or "-" characheters => [^\\]-] Module Id should have at lest one
   * alphanumeric charater [A-Z0-9]
   */
  private Pattern correlationPattern = Pattern.compile("\\[([^\\]\\[ -]+)-([^\\]\\[ ]+)\\]");

  private Pattern p = Pattern.compile("type=\"([^\"]+)\"");

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.base.mail.IncomingMailService#processMail(com.bpmasters.pmc.base.mail.MailServerConfiguration, java.util.List)
   */
  @Override
  public void processMail(MailServerConfiguration configuration) {

    Folder inbox = null;
    Session emailSession = null;
    Store store = null;

    log.info("Processing mail with config: {}", configuration);
    List<IncomingMessageDto> incomingMessages = new ArrayList<>();
    try {
      Properties properties = new Properties();
      properties.put("mail.pop3.host", configuration.getHost());
      properties.put("mail.pop3.connectiontimeout", 10000);
      properties.put("mail.pop3.timeout", 10000);
//      properties.put("mail.debug", "true");
      if (configuration.isSsl()) {
        properties.setProperty("mail.pop3.ssl.enable", "true");

        /*
         * TODO: remove this property so we don't trust each and every SSL certs
         *  here we risk man in the middle attack
         */
        properties.put("mail.pop3.ssl.trust", "*");
      }
      emailSession = Session.getDefaultInstance(properties);

      store = emailSession.getStore("pop3");
      store.connect(configuration.getHost(), configuration.getPort(), configuration.getUsername(), configuration.getPassword());


      inbox = store.getFolder("INBOX");

      /* Open the inbox using store. */

      inbox.open(Folder.READ_WRITE);

      /* Get the messages which is unread in the Inbox */

      Message messages[] = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
      if (messages.length < 1) {
        messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
      }
      log.info("Processing {} unread messages...", messages.length);

      for (Message message : messages) {
        Date dt = new Date();
        Mail mail = new Mail();
        List<MailAttachment> attachments = new ArrayList<MailAttachment>();
        String subject = message.getSubject();
        Matcher m = correlationPattern.matcher(subject);
        if (m.find()) {
          log.trace("{},{},{}", m.group(), m.group(1), m.group(2));
          mail.setModuleName(m.group(1));
          mail.setModuleId(m.group(2));
        }
        String from = parseAddress(message.getFrom());
        String to = parseAddress(message.getRecipients(RecipientType.TO));
        String cc = parseAddress(message.getRecipients(RecipientType.CC));
        log.debug("...processing message from {}: {}", from, subject);
        log.debug("...sent to {} (CC: {})", to, cc);
        String text = null;
        // log.info("Message: "+message.get);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        byte[] mailBlob = baos.toByteArray();

        if (message.getContent() instanceof MimeMultipart) {
          log.trace("...processing multipart");
          MimeMultipart mm = (MimeMultipart) message.getContent();
          for (int i = 0; i < mm.getCount(); i++ ) {
            BodyPart bp = mm.getBodyPart(i);
            log.trace("Part #{}: {}-{}", i, bp.getDisposition(), bp.getContentType());
            String cT = retrieveContentType(bp.getContentType());
            String name = bp.getFileName();
            if (name==null) {
              name = "inline";
            }
            if (cT.contains("text/plain") ||
              cT.contains("text/html") ||
              cT.contains("multipart/related") ||
              bp.getContent() instanceof MimeMultipart) {
              text = extractContent(retrieveContentBody(bp), bp.getContentType().contains("text/html"));
            } else {
              MailAttachment ma = new MailAttachment();

              ma.setMail(mail);
              ma.setMimeType(cT);
              ma.setName(name);
              ma.setBlob(IOUtils.toByteArray(bp.getInputStream()));
              Object attText = bp.getContent();
              log.trace("...body part content: {}", attText);
              attachments.add(ma);
            }
          }
        } else {
          log.trace("... processing singlepart");
          String msg = message.getContent().toString();
          boolean isHtml = msg.toLowerCase().contains("<html");
          text = extractContent(msg, isHtml);
        }
        log.info(text);
        mail.setState(ProcessState.NEW);

        mail.setBody(text);
        mail.setAddressCc(cc);
        mail.setAddressFrom(from);
        mail.setSubject(subject);
        mail.setAddressTo(to);
        mail.setAttachments(attachments);
        mail.setMail(new String(mailBlob));
        //Persist Email
        incomingMailService.storeMessage(mail);

        if (StringUtils.isNotEmpty(mail.getModuleName())) {
          incomingMessages.add(new IncomingMessageDto(mail.getSubject(), mail.getGuid(), mail.getModuleName(), mail.getModuleId()));
        }

        message.setFlag(Flag.DELETED, true);
        log.debug("...done processing message from {}: {}", from, subject);
      }

      log.debug("...done processing {} messages...", messages.length);
    } catch (Exception ex) {
      log.warn("Error reading mailbox {}: {}", configuration.getUsername(), ex.getMessage(), ex);
    } finally {
      try {
        if (inbox != null)
          inbox.close(true);
        if (store != null)
          store.close();
      } catch (Exception ex2) {
        log.warn("Error closing inbox/store mailbox {}: {}", configuration.getUsername(), ex2.getMessage(), ex2);
      }
    }

    log.info("MailAccount {} done..", configuration.getUsername());
    if ( !incomingMessages.isEmpty()) {
      this.internalEvent.fire(new PrivateNotificationList(incomingMessages));
    }
  }

  @Override
  @Transactional(value = TxType.REQUIRES_NEW)
  public void storeMessage(Mail mail) {
    log.info("Saving mail {}", mail);

    mailDao.create(mail);
  }

  /**
   * Gets called to put the processing of emails in a different transaction, after the mails have been persisted.
   *
   * @param internalNotification
   */
  @Override
  @Asynchronous()
  @Lock(LockType.READ)
  public void internalEventAfterSuccess(@Observes(during = TransactionPhase.AFTER_SUCCESS) PrivateNotificationList internalNotification) {
    log.trace("Sending event after success to {}", internalNotification);

    this.messageEvent.fire(new IncomingMessageEvent(internalNotification.getMessages()));
  }

  private String retrieveContentBody(BodyPart bp) throws MessagingException, IOException {

    Object content = bp.getContent();
    log.trace("Retrieved content body class {}: {}", content.getClass(), content);
    if (content instanceof MimeMultipart) {
      MimeMultipart mimu = (MimeMultipart) content;
      for (int i = 0; i < mimu.getCount(); i++ ) {
        BodyPart bp2 = mimu.getBodyPart(i);
        // System.out.println(bp2.getContentType() + "-"
        // + bp2.getFileName() + bp2.getContent().toString());
        if (bp2.getContentType().contains("text/html") || bp2.getContentType().contains("text/plain"))
          return bp2.getContent().toString();
      }
    }

    return content.toString();
  }

  private String retrieveContentType(String contentTypeString) throws MessagingException {
    String cT = contentTypeString;
    if (cT.indexOf(";") < 0)
      return cT;
    Matcher m = p.matcher(cT);
    if (m.find()) {
      String retVal = m.group(1);
      return retVal;
    }
    return cT.substring(0, cT.indexOf(";"));

  }

  private String parseAddress(Address[] adArray) {
    if (adArray == null)
      return "";
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Address a : adArray) {
      InternetAddress ia = (InternetAddress) a;
      if ( !first)
        sb.append(",");
      else
        first = false;
      sb.append(ia.getAddress());
    }
    return sb.toString();
  }

  /**
   * @param content
   * @param isHtml
   * @return
   */
  private String extractContent(String content, boolean isHtml) {
    if (isHtml) {
      Document doc = Jsoup.parse(content);
      return html2Plain.getPlainText(doc);
    }
    return content.trim();
  }

  public static void main(String[] args) {
    String s = "[[aABC-12A3] SDF-234 [BANF-2018-12-1234] [UR18.000065-164194], Debian Bookworkm [BANF  - 1234]";
    IncomingMailServiceImpl service = new IncomingMailServiceImpl();
    Matcher m = service.correlationPattern.matcher(s);
    while (m.find()) {
      System.out.println(m.group() + "," + m.group(1) + "," + m.group(2));
    }
    service.processMail(new MailServerConfiguration("localhost", 1995, "pmc-test", "***", true));

  }

}
