package org.notificationengine.notificator.mail;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.PhysicalNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Topic;
import org.notificationengine.mail.MailOptionsUtils;
import org.notificationengine.mail.Mailer;
import org.notificationengine.notificator.Notificator;
import org.notificationengine.persistance.Persister;
import org.notificationengine.spring.SpringUtils;
import org.notificationengine.templating.TemplateEngine;

public class MultipleMailByRecipientNotificator extends Notificator {

	private static Logger LOGGER = Logger.getLogger(MultipleMailByRecipientNotificator.class);
	
	private String mailTemplate;

    private Boolean isHtmlTemplate;
	
	public MultipleMailByRecipientNotificator(Topic topic, String mailTemplate, Boolean isHtmlTemplate) {
		
		super(topic);
		
		this.mailTemplate = mailTemplate;

        this.isHtmlTemplate = isHtmlTemplate;
	}

	@Override
	protected Map<DecoratedNotification, Boolean> processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {
		
		TemplateEngine templateEngine = (TemplateEngine)SpringUtils.getBean(Constants.TEMPLATE_ENGINE);
		Mailer mailer = (Mailer)SpringUtils.getBean(Constants.MAILER);
		
		templateEngine.loadTemplate(mailTemplate);

        Map<DecoratedNotification, Boolean> result = new HashMap<>();

        Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);
		
		for (DecoratedNotification decoratedNotification : notSentDecoratedNotifications) {

            Map<String, Object> context = decoratedNotification.getRawNotification().getContext();

            Collection<ObjectId> fileIds = (Collection<ObjectId>) context.get("fileIds");

            Collection<File> filesToAttach = new HashSet<>();

            Recipient recipient = decoratedNotification.getRecipient();

            if(fileIds != null) {

                for(ObjectId fileId : fileIds) {
                    File file = persister.retrieveFileFromId(fileId);

                    filesToAttach.add(file);
                }
            }

			// merge the context and the template
			String notificationText = templateEngine.processTemplate(this.mailTemplate, decoratedNotification.getContext());
			
			LOGGER.debug("Notification text after merge : " + notificationText);
			
			Map<String, String> options = MailOptionsUtils.buildMailOptionsFromContext(decoratedNotification.getContext());
			
			// sent a mail to the recipient
			Boolean sentCorrectly = mailer.sendMail(recipient.getAddress(), notificationText, this.isHtmlTemplate, filesToAttach, options);

            LOGGER.debug("Mail sent? " + sentCorrectly);

            result.put(decoratedNotification, sentCorrectly);

            if(sentCorrectly) {

                this.savePhysicalNotification(recipient, mailer.getSubject(options), notificationText, fileIds);
            }

            //delete files created after sending (or not)
            //even if it has not been sent, the file will be created next time
            for(File file : filesToAttach) {
                file.delete();
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
