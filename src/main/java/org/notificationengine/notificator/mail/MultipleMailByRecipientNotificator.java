package org.notificationengine.notificator.mail;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;
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
	protected void processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {
		
		TemplateEngine templateEngine = (TemplateEngine)SpringUtils.getBean(Constants.TEMPLATE_ENGINE);
		
		templateEngine.loadTemplate(mailTemplate);
		
		for (DecoratedNotification decoratedNotification : notSentDecoratedNotifications) {
			
			// merge the context and the template
			String notificationText = templateEngine.processTemplate(this.mailTemplate, decoratedNotification.getContext());
			
			LOGGER.debug("Notification text after merge : " + notificationText);
			
			// TODO sent a mail to the recipient
		}
	}
	
	public String getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(String mailTemplate) {
		this.mailTemplate = mailTemplate;
	}
}
