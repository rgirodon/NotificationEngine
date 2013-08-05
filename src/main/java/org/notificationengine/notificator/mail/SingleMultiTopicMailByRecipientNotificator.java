package org.notificationengine.notificator.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Topic;
import org.notificationengine.mail.MailOptionsUtils;
import org.notificationengine.mail.Mailer;
import org.notificationengine.notificator.INotificator;
import org.notificationengine.persistance.Persister;
import org.notificationengine.spring.SpringUtils;
import org.notificationengine.templating.TemplateEngine;
import org.springframework.stereotype.Component;

@Component(value=Constants.SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT_NOTIFICATOR)
public class SingleMultiTopicMailByRecipientNotificator implements INotificator {

	private static Logger LOGGER = Logger.getLogger(SingleMultiTopicMailByRecipientNotificator.class);
	
	private Boolean activated;
	
	private Map<String, Collection<Topic>> mailTemplateAndTopics; 
	
	private Collection<DecoratedNotification> decoratedNotificationsToProcess;
	
	public SingleMultiTopicMailByRecipientNotificator() {
		
		this.activated = Boolean.FALSE;
		
		this.mailTemplateAndTopics = new HashMap<>();
		
		this.decoratedNotificationsToProcess = new HashSet<>();
	}
	
	public Boolean isActivated() {

		return this.activated;
	}

	public void activate() {
		
		this.activated = Boolean.TRUE;
	}

	@Override
	public void process() {
		
		TemplateEngine templateEngine = (TemplateEngine)SpringUtils.getBean(Constants.TEMPLATE_ENGINE);
		
		Mailer mailer = (Mailer)SpringUtils.getBean(Constants.MAILER);
		
		// for each mailTemplate, get decoratedNotifications to process and send them in a single multi topic mail
		for (String mailTemplate : this.mailTemplateAndTopics.keySet()) {
			
			templateEngine.loadTemplate(mailTemplate);
			
			Collection<Topic> topicsForThisMailTemplate = this.mailTemplateAndTopics.get(mailTemplate);
			
			LOGGER.debug("Topics for this MailTemplate : " + topicsForThisMailTemplate);
			
			Collection<Recipient> recipientsForTheseTopics = this.retrieveRecipientsForTopics(topicsForThisMailTemplate);
			
			LOGGER.debug("Recipients for these topics : " + recipientsForTheseTopics);
			
			for (Recipient recipient : recipientsForTheseTopics) {
			
				LOGGER.debug("Recipient : " + recipient);
				
				Collection<DecoratedNotification> decoratedNotificationsForThisRecipient = new HashSet<>();
				
				Collection<Map<String, Object>> rawNotificationContexts = new ArrayList<>();
				
				// build and send mail for these notifications
				Map<String, Object> globalContext = new HashMap<>();
				
				globalContext.put(Constants.RECIPIENT, recipient.getAddress());
				globalContext.put(Constants.DISPLAY_NAME, recipient.getDisplayName());

				Collection<Map<String, Object>> topicContexts = new HashSet<>();
				
				for (Topic topicForThisMailTemplate : topicsForThisMailTemplate) {
					
					LOGGER.debug("Topic : " + topicForThisMailTemplate);
					
					Map<String, Object> topicContext = new HashMap<>();
					
					topicContext.put(Constants.TOPIC, topicForThisMailTemplate.getName());
					
					Collection<DecoratedNotification> decoratedNotificationsForThisTopicAndRecipient = this.retrieveDecoratedNotificationsToProcessForTopicAndRecipient(topicForThisMailTemplate,
																																							recipient);
					
					decoratedNotificationsForThisRecipient.addAll(decoratedNotificationsForThisTopicAndRecipient);
					
					Collection<Map<String, Object>> contexts = new ArrayList<>();
					for (DecoratedNotification decoratedNotificationForThisTopicAndRecipient : decoratedNotificationsForThisTopicAndRecipient) {
						
						contexts.add(decoratedNotificationForThisTopicAndRecipient.getRawNotification().getContext());
						
						rawNotificationContexts.add(decoratedNotificationForThisTopicAndRecipient.getRawNotification().getContext());
					}
					
					topicContext.put(Constants.NOTIFICATIONS_FOR_TOPIC, contexts);
					
					topicContexts.add(topicContext);
				}
				
				globalContext.put(Constants.TOPICS, topicContexts);
				
				String notificationText = templateEngine.processTemplate(mailTemplate, globalContext);
				
				LOGGER.debug("Notification text after merge : " + notificationText);
				
				Map<String, String> options = MailOptionsUtils.buildMailOptionsFromContexts(rawNotificationContexts);
				
				// sent a mail to the recipient
				Boolean sentCorrectly = mailer.sendMail(recipient.getAddress(), notificationText, options);

				LOGGER.debug("Mail sent? " + sentCorrectly);
				
				if (sentCorrectly) {
					
					this.markAsSent(decoratedNotificationsForThisRecipient);
				}
				else {					
					this.markAsNotSent(decoratedNotificationsForThisRecipient);
				}
			}
		}
	}
	
	private void markAsNotSent(
			Collection<DecoratedNotification> decoratedNotifications) {
		
		for (DecoratedNotification decoratedNotification : decoratedNotifications) {
			
			this.markAsNotSent(decoratedNotification);
		}
	}

	private void markAsNotSent(DecoratedNotification decoratedNotification) {
		
		Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);
		
		Integer attempts = decoratedNotification.getSendingAttempts();

        attempts = attempts + 1;

        decoratedNotification.setSendingAttempts(attempts);

        if(attempts >= Constants.MAX_ATTEMPTS) {

        	persister.deleteDecoratedNotification(decoratedNotification);
        }
        else {
        	persister.saveDecoratedNotification(decoratedNotification);
        }
	}

	private void markAsSent(
			Collection<DecoratedNotification> decoratedNotifications) {
		
		for (DecoratedNotification decoratedNotification : decoratedNotifications) {
			
			this.markAsSent(decoratedNotification);
		}
	}

	private void markAsSent(DecoratedNotification decoratedNotification) {
		
		Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);
		
		persister.markDecoratedNotificationAsSent(decoratedNotification);
	}

	public Collection<Recipient> retrieveRecipientsForTopics(
			Collection<Topic> topics) {
		
		Collection<Recipient> result = new HashSet<>();
		
		for (DecoratedNotification decoratedNotification : this.decoratedNotificationsToProcess) {
			
			Topic decoratedNotificationTopic = decoratedNotification.getRawNotification().getTopic();
		
			for (Topic topic : topics) {
				
				if (decoratedNotificationTopic.isSonOfTopic(topic)) {
					
					result.add(decoratedNotification.getRecipient());
					
					break;
				}
			}
		}
		
		return result;
	}

	public Collection<DecoratedNotification> retrieveDecoratedNotificationsToProcessForTopicAndRecipient(Topic topic, Recipient recipient) {
		
		Collection<DecoratedNotification> result = new HashSet<>();
		
		Collection<DecoratedNotification> decoratedNotificationsToRemove = new HashSet<>();
		
		for (DecoratedNotification decoratedNotification : this.decoratedNotificationsToProcess) {
			
			Topic decoratedNotificationTopic = decoratedNotification.getRawNotification().getTopic();
			
			Recipient decoratedNotificationRecipient = decoratedNotification.getRecipient();
			
			if (decoratedNotificationTopic.isSonOfTopic(topic)
					&& decoratedNotificationRecipient.equals(recipient)) {
				
				result.add(decoratedNotification);
				
				decoratedNotificationsToRemove.add(decoratedNotification);
			}			
		}
		
		for (DecoratedNotification decoratedNotification : decoratedNotificationsToRemove) {
			
			this.decoratedNotificationsToProcess.remove(decoratedNotification);
		}
		
		return result;
	}

	public void add(String mailTemplate, Topic topic) {
		
		Collection<Topic> topicsForMailTemplate = this.mailTemplateAndTopics.get(mailTemplate);
		
		if (topicsForMailTemplate == null) {
			
			topicsForMailTemplate = new HashSet<>();
			
			this.mailTemplateAndTopics.put(mailTemplate, topicsForMailTemplate);
		}
		
		topicsForMailTemplate.add(topic);
	}

	public void addDecoratedNotificationsToProcess(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {
		
		this.decoratedNotificationsToProcess.addAll(notSentDecoratedNotifications);
	}
	
	public Map<String, Collection<Topic>> getMailTemplateAndTopics() {
		return mailTemplateAndTopics;
	}

	public void setMailTemplateAndTopics(Map<String, Collection<Topic>> mailTemplateAndTopics) {
		this.mailTemplateAndTopics = mailTemplateAndTopics;
	}

	public Collection<DecoratedNotification> getDecoratedNotificationsToProcess() {
		return decoratedNotificationsToProcess;
	}

	public void setDecoratedNotificationsToProcess(
			Collection<DecoratedNotification> decoratedNotificationsToProcess) {
		this.decoratedNotificationsToProcess = decoratedNotificationsToProcess;
	}
}
