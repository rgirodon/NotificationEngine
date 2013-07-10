package org.notificationengine.mail;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.notificationengine.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component(value=Constants.MAILER)
public class Mailer {

	private static Logger LOGGER = Logger.getLogger(Mailer.class);
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private SimpleMailMessage templateMessage;

	public void sendMail(String recipientAddress, String text) {

        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        
        msg.setTo(recipientAddress);
        
        msg.setText(text);
        
        try{
            this.mailSender.send(msg);
        }
        catch(MailException ex) {
        	LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
			
			LOGGER.error("Unable to send mail");
        }
    }
	
	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public SimpleMailMessage getTemplateMessage() {
		return templateMessage;
	}

	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}
	
	
}
