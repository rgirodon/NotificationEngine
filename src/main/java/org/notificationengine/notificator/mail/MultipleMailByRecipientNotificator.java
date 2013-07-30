package org.notificationengine.notificator.mail;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.mail.Mailer;
import org.notificationengine.notificator.Notificator;
import org.notificationengine.spring.SpringUtils;
import org.notificationengine.templating.TemplateEngine;

public class MultipleMailByRecipientNotificator extends Notificator {

	private static Logger LOGGER = Logger.getLogger(MultipleMailByRecipientNotificator.class);
	
	private String mailTemplate;
	
	public MultipleMailByRecipientNotificator(Topic topic, String mailTemplate) {
		
		super(topic);
		
		this.mailTemplate = mailTemplate;
	}

	@Override
	protected Boolean processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {
		
		TemplateEngine templateEngine = (TemplateEngine)SpringUtils.getBean(Constants.TEMPLATE_ENGINE);
		Mailer mailer = (Mailer)SpringUtils.getBean(Constants.MAILER);
		
		templateEngine.loadTemplate(mailTemplate);

        Boolean result = Boolean.TRUE;
		
		for (DecoratedNotification decoratedNotification : notSentDecoratedNotifications) {
			
			// merge the context and the template
			String notificationText = templateEngine.processTemplate(this.mailTemplate, decoratedNotification.getContext());
			
			LOGGER.debug("Notification text after merge : " + notificationText);
			
			// sent a mail to the recipient
			Boolean sentCorrectly = mailer.sendMail(decoratedNotification.getRecipient().getAddress(), notificationText);

            if(!sentCorrectly) {
                result = Boolean.FALSE;

                LOGGER.warn("Mail not sent");
            }

            else {
			
			    LOGGER.debug("Mail sent");
            }
		}

        return result;
	}
	
	public String getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(String mailTemplate) {
		this.mailTemplate = mailTemplate;
	}
}
