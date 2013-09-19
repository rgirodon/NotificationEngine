package org.notificationengine.notificator.mail;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Topic;
import org.notificationengine.mail.MailOptionsUtils;
import org.notificationengine.mail.Mailer;
import org.notificationengine.notificator.Notificator;
import org.notificationengine.persistance.Persister;
import org.notificationengine.spring.SpringUtils;
import org.notificationengine.templating.TemplateEngine;

public class SingleMailByRecipientNotificator extends Notificator {

	private static Logger LOGGER = Logger.getLogger(SingleMailByRecipientNotificator.class);
	
	private String mailTemplate;

    private Boolean isHtmlTemplate;
	
	public SingleMailByRecipientNotificator(Topic topic, String mailTemplate, Boolean isHtmlTemplate) {
		
		super(topic);
		
		this.mailTemplate = mailTemplate;

        this.isHtmlTemplate = isHtmlTemplate;
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

        Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);
		
		// then, for each recipient, send a mail
		for (Recipient recipient : notificationsByRecipient.keySet()) {
			
			LOGGER.debug("Processing Notifications for Recipient : " + recipient);
			
			Collection<DecoratedNotification> notificationsForThisRecipient = notificationsByRecipient.get(recipient);

            Collection<File> filesToAttach = new HashSet<>();

            Collection<ObjectId> allFileIds = new HashSet<>();
			
			Collection<Map<String, Object>> contexts = new ArrayList<>();
			for (DecoratedNotification notificationForThisRecipient : notificationsForThisRecipient) {
				contexts.add(notificationForThisRecipient.getRawNotification().getContext());

                Map<String, Object> context = notificationForThisRecipient.getRawNotification().getContext();

                Collection<ObjectId> fileIds = (Collection<ObjectId>) context.get("fileIds");

                if(fileIds != null) {

                    for(ObjectId fileId : fileIds) {

                        allFileIds.add(fileId);

                        File file = persister.retrieveFileFromId(fileId);

                        filesToAttach.add(file);
                    }

                }

            }

			LOGGER.debug("This Recipient has notifications : " + notificationsForThisRecipient);
			
			// merge the context and the template
			Map<String, Object> globalContext = new HashMap<>();
			globalContext.put(Constants.RECIPIENT, recipient.getAddress());
			globalContext.put(Constants.DISPLAY_NAME, recipient.getDisplayName());
			globalContext.put(Constants.NOTIFICATIONS_BY_RECIPIENT, contexts);
			
			String notificationText = templateEngine.processTemplate(this.mailTemplate, globalContext);
			
			LOGGER.debug("Notification text after merge : " + notificationText);
			
			Map<String, String> options = MailOptionsUtils.buildMailOptionsFromContexts(contexts);
			
			// sent a mail to the recipient
			Boolean sentCorrectly = mailer.sendMail(recipient.getAddress(), notificationText, this.isHtmlTemplate, filesToAttach, options);

            LOGGER.debug("Mail sent? " + sentCorrectly);

            for (DecoratedNotification notificationForThisRecipient : notificationsForThisRecipient) {

                result.put(notificationForThisRecipient, sentCorrectly);
            }

            //delete files created after sending (or not)
            //even if it has not been sent, the file will be created next time
            if(filesToAttach != null) {

                for(File file : filesToAttach) {
                    file.delete();
                }
            }

            if(sentCorrectly) {

                this.savePhysicalNotification(recipient, mailer.getSubject(options),notificationText, allFileIds);

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

    public Boolean getHtmlTemplate() {
        return isHtmlTemplate;
    }

    public void setHtmlTemplate(Boolean htmlTemplate) {
        isHtmlTemplate = htmlTemplate;
    }
}
