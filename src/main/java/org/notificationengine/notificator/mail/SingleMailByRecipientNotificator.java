package org.notificationengine.notificator.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Topic;
import org.notificationengine.mail.MailOptionsUtils;
import org.notificationengine.mail.Mailer;
import org.notificationengine.notificator.Notificator;
import org.notificationengine.spring.SpringUtils;
import org.notificationengine.templating.TemplateEngine;

public class SingleMailByRecipientNotificator extends Notificator {

	private static Logger LOGGER = Logger.getLogger(SingleMailByRecipientNotificator.class);
	
	private String mailTemplate;
	
	public SingleMailByRecipientNotificator(Topic topic, String mailTemplate) {
		
		super(topic);
		
		this.mailTemplate = mailTemplate;
	}

	@Override
	protected Map<DecoratedNotification, Boolean> processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {

        Map<DecoratedNotification, Boolean> result = new HashMap<>();
		
		TemplateEngine templateEngine = (TemplateEngine)SpringUtils.getBean(Constants.TEMPLATE_ENGINE);
		Mailer mailer = (Mailer)SpringUtils.getBean(Constants.MAILER);
		
		templateEngine.loadTemplate(mailTemplate);
		
		Map<Recipient, Collection<DecoratedNotification>> notificationsByRecipient = new HashMap<>();
		
		// first, regroup notifications by recipient
		for (DecoratedNotification decoratedNotification : notSentDecoratedNotifications) {
		
			Recipient recipient = decoratedNotification.getRecipient();
			
			Collection<DecoratedNotification> notificationsForThisRecipient = null;
			
			if (notificationsByRecipient.containsKey(recipient)) {
				
				notificationsForThisRecipient = notificationsByRecipient.get(recipient);
			}
			else {
				notificationsForThisRecipient = new ArrayList<>();
				
				notificationsByRecipient.put(recipient, notificationsForThisRecipient);
			}
			
			notificationsForThisRecipient.add(decoratedNotification);
		}
		
		LOGGER.debug("NotificationsByRecipient map : " + notificationsByRecipient);
		
		// then, for each recipient, send a mail
		for (Recipient recipient : notificationsByRecipient.keySet()) {
			
			LOGGER.debug("Processing Notifications for Recipient : " + recipient);
			
			Collection<DecoratedNotification> notificationsForThisRecipient = notificationsByRecipient.get(recipient);
			
			Collection<Map<String, Object>> contexts = new ArrayList<>();
			for (DecoratedNotification notificationForThisRecipient : notificationsForThisRecipient) {
				contexts.add(notificationForThisRecipient.getRawNotification().getContext());
			}
			
			LOGGER.debug("This Recipient has notifications : " + notificationsForThisRecipient);
			
			// merge the context and the template
			Map<String, Object> globalContext = new HashMap<>();
			globalContext.put(Constants.RECIPIENT, recipient.getAddress());
			globalContext.put(Constants.NOTIFICATIONS_BY_RECIPIENT, contexts);
			
			String notificationText = templateEngine.processTemplate(this.mailTemplate, globalContext);
			
			LOGGER.debug("Notification text after merge : " + notificationText);
			
			Map<String, String> options = MailOptionsUtils.buildMailOptionsFromContexts(contexts);
			
			// sent a mail to the recipient
			Boolean sentCorrectly = mailer.sendMail(recipient.getAddress(), notificationText, options);

            LOGGER.debug("Mail sent? " + sentCorrectly);

            for (DecoratedNotification notificationForThisRecipient : notificationsForThisRecipient) {

                result.put(notificationForThisRecipient, sentCorrectly);
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
