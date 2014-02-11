package org.notificationengine.notificator.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.PhysicalNotification;
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
	
	private Map<String, MailTemplateConfiguration> mailTemplateAndTopics;
	
	private Collection<DecoratedNotification> decoratedNotificationsToProcess;

    private Boolean urgentEnabled;
	
	public SingleMultiTopicMailByRecipientNotificator() {
		
		this.activated = Boolean.FALSE;

        this.urgentEnabled = Boolean.FALSE;
		
		this.mailTemplateAndTopics = new HashMap<>();
		
		this.decoratedNotificationsToProcess = new HashSet<>();
	}

    @Override
    public void setUrgentEnabled(Boolean urgentEnabled) {
        this.urgentEnabled = urgentEnabled;
    }

    public Boolean getUrgentEnabled() {
        return urgentEnabled;
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

        // Get the actual date and the date x days before to add it to the context
        // This part is specific for our customer
        DateTime now = new DateTime();
        String today = now.toString("dd/MM/yyyy");

        DateTime lastTime = now.minusDays(Constants.DAYS_BETWEEN_NOTIFICATION);
        String lastTimeString = lastTime.toString("dd/MM/yyyy");
		
		// for each mailTemplate, get decoratedNotifications to process and send them in a single multi topic mail
		for (String mailTemplate : this.mailTemplateAndTopics.keySet()) {
			
			templateEngine.loadTemplate(mailTemplate);
			
			MailTemplateConfiguration topicsForThisMailTemplate = this.mailTemplateAndTopics.get(mailTemplate);
			
			LOGGER.debug("Topics for this MailTemplate : " + topicsForThisMailTemplate);
			
			Collection<Recipient> recipientsForTheseTopics = this.retrieveRecipientsForTopics(topicsForThisMailTemplate.getTopics());
			
			LOGGER.debug("Recipients for these topics : " + recipientsForTheseTopics);

            Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);
			
			for (Recipient recipient : recipientsForTheseTopics) {
			
				LOGGER.debug("Recipient : " + recipient);
				
				Collection<DecoratedNotification> decoratedNotificationsForThisRecipient = new HashSet<>();

                Collection<File> filesToAttach = new HashSet<>();

                Collection<ObjectId> allFileIds = new HashSet<>();
				
				Collection<Map<String, Object>> rawNotificationContexts = new ArrayList<>();
				
				// build and send mail for these notifications
				Map<String, Object> globalContext = new HashMap<>();
				
				globalContext.put(Constants.RECIPIENT, recipient.getAddress());
				globalContext.put(Constants.DISPLAY_NAME, recipient.getDisplayName());
				globalContext.put(Constants.BEGIN_DATE, lastTimeString);
				globalContext.put(Constants.END_DATE, today);

				Collection<Map<String, Object>> topicContexts = new HashSet<>();
				
				for (Topic topicForThisMailTemplate : topicsForThisMailTemplate.getTopics()) {
					
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

                        Map<String, Object> context = decoratedNotificationForThisTopicAndRecipient.getRawNotification().getContext();

                        Collection<ObjectId> fileIds = (Collection<ObjectId>) context.get("fileIds");

                        if(fileIds != null) {

                            for(ObjectId fileId : fileIds) {

                                allFileIds.add(fileId);

                                File file = persister.retrieveFileFromId(fileId);

                                filesToAttach.add(file);
                            }
                        }
					}
					
					topicContext.put(Constants.NOTIFICATIONS_FOR_TOPIC, contexts);
					
					topicContexts.add(topicContext);
				}
				
				globalContext.put(Constants.TOPICS, topicContexts);
				
				String notificationText = templateEngine.processTemplate(mailTemplate, globalContext);
				
				LOGGER.debug("Notification text after merge : " + notificationText);
				
				Map<String, String> options = MailOptionsUtils.buildMailOptionsFromContexts(rawNotificationContexts);
				
				// sent a mail to the recipient
				Boolean sentCorrectly = mailer.sendMail(recipient.getAddress(), notificationText, topicsForThisMailTemplate.getHtmlTemplate(), filesToAttach, options);

				LOGGER.debug("Mail sent? " + sentCorrectly);
				
				if (sentCorrectly) {
					
					this.markAsSent(decoratedNotificationsForThisRecipient);

                    this.savePhysicalNotification(recipient, mailer.getSubject(options), notificationText, allFileIds);

				}
				else {					
					this.markAsNotSent(decoratedNotificationsForThisRecipient);
				}

                //delete files created after sending (or not)
                //even if it has not been sent, the file will be created next time
                for(File file : filesToAttach) {
                    file.delete();
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

        	persister.moveFailedDecoratedNotification(decoratedNotification);
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

    public void savePhysicalNotification(
            Recipient recipient, String subject, String notificationContent, Collection<ObjectId> filesAttachedIds) {

        PhysicalNotification physicalNotification =
                new PhysicalNotification(recipient, subject, notificationContent, filesAttachedIds);

        Persister persister = (Persister) SpringUtils.getBean(Constants.PERSISTER);

        persister.savePhysicalNotification(physicalNotification);

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

	public void add(String mailTemplate, Boolean isHtmlTemplate, Topic topic) {
		
		MailTemplateConfiguration topicsForMailTemplate = this.mailTemplateAndTopics.get(mailTemplate);
		
		if (topicsForMailTemplate == null) {
			
			topicsForMailTemplate = new MailTemplateConfiguration();
			
			this.mailTemplateAndTopics.put(mailTemplate, topicsForMailTemplate);
		}
		
		topicsForMailTemplate.add(isHtmlTemplate, topic);
	}

	public void addDecoratedNotificationsToProcess(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {
		
		this.decoratedNotificationsToProcess.addAll(notSentDecoratedNotifications);
	}
	
	public Map<String, MailTemplateConfiguration> getMailTemplateAndTopics() {
		return mailTemplateAndTopics;
	}

	public void setMailTemplateAndTopics(Map<String, MailTemplateConfiguration> mailTemplateAndTopics) {
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
