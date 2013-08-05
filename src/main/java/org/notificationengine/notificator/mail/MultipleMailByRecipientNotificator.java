package org.notificationengine.notificator.mail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.mail.MailOptionsUtils;
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
	protected Map<DecoratedNotification, Boolean> processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {
		
		TemplateEngine templateEngine = (TemplateEngine)SpringUtils.getBean(Constants.TEMPLATE_ENGINE);
		Mailer mailer = (Mailer)SpringUtils.getBean(Constants.MAILER);
		
		templateEngine.loadTemplate(mailTemplate);

        Map<DecoratedNotification, Boolean> result = new HashMap<>();
		
		for (DecoratedNotification decoratedNotification : notSentDecoratedNotifications) {
			
			// merge the context and the template
			String notificationText = templateEngine.processTemplate(this.mailTemplate, decoratedNotification.getContext());
			
			LOGGER.debug("Notification text after merge : " + notificationText);
			
			Map<String, String> options = MailOptionsUtils.buildMailOptionsFromContext(decoratedNotification.getContext());
			
			// sent a mail to the recipient
			Boolean sentCorrectly = mailer.sendMail(decoratedNotification.getRecipient().getAddress(), notificationText, options);

            LOGGER.debug("Mail sent? " + sentCorrectly);

            result.put(decoratedNotification, sentCorrectly);
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
