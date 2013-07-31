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

	public Boolean sendMail(String recipientAddress, String text, String... otherParams) {

        Boolean result = Boolean.FALSE;
		
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        
        msg.setTo(recipientAddress);
        
        msg.setText(text);

        // TODO enable customization of subject, and maybe from field

        if(otherParams.length > 0) {

            String subject = otherParams[0];

            msg.setSubject(subject);
        }

        if(otherParams.length > 1) {

            String fromField = otherParams[1];

            msg.setFrom(fromField);
        }
        
        try{
            this.mailSender.send(msg);

            result = Boolean.TRUE;
        }
        catch(MailException ex) {

        	LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
			
			LOGGER.error("Unable to send mail");
        }

        return result;
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
