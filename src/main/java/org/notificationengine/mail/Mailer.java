package org.notificationengine.mail;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.notificationengine.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Component(value=Constants.MAILER)
public class Mailer {

	private static Logger LOGGER = Logger.getLogger(Mailer.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private SimpleMailMessage templateMessage;

	public Boolean sendMail(String recipientAddress, String text, Boolean isHtmlTemplate, Map<String, String> options) {

        Boolean result = Boolean.FALSE;

        try {

            MimeMessage message = this.mailSender.createMimeMessage();

            // use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, isHtmlTemplate, "UTF-8");

            helper.setTo(recipientAddress);

            if (options != null) {

                String subject = options.get(Constants.SUBJECT);

                if (!StringUtils.isEmpty(subject)) {

                    helper.setSubject(subject);
                }


                String from = options.get(Constants.FROM);

                if (!StringUtils.isEmpty(from)) {

                    helper.setFrom(from);
                }
            }

            // use the true flag to indicate the text included is HTML
            helper.setText(text, true);

            this.mailSender.send(message);

            result = Boolean.TRUE;

        } catch (MailException me) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(me));

            LOGGER.error("Unable to send mail");

        } catch (MessagingException messagingExc) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(messagingExc));

            LOGGER.error("Unable to send mail");

        }

        return result;
    }
	
	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public SimpleMailMessage getTemplateMessage() {
		return templateMessage;
	}

	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}
	
	
}
