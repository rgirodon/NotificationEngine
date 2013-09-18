package org.notificationengine.notificator;

import java.util.*;

import org.bson.types.ObjectId;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.PhysicalNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;
import org.notificationengine.spring.SpringUtils;

public abstract class Notificator implements INotificator {

	private Topic topic;
	
	private Map<String, String> options;

    private Boolean urgentEnabled;
	
	public Notificator(Topic topic) {
		
		this.topic = topic;

        this.urgentEnabled = Boolean.FALSE;
		
		this.options = new HashMap<>();
	}
	
	public Notificator(Topic topic, Map<String, String> options) {
		
		this.topic = topic;

        this.urgentEnabled = Boolean.FALSE;
		
		this.options = options;
	}

	@Override
	public void process() {
		
		Collection<DecoratedNotification> notSentDecoratedNotifications = this.retrieveNotSentDecoratedNotifications();

        Map<DecoratedNotification, Boolean> decoratedNotificationSentMap= this.processNotSentDecoratedNotifications(notSentDecoratedNotifications);

        Collection<DecoratedNotification> decoratedNotificationToMarkAsSent = new ArrayList<>();

        for(Map.Entry<DecoratedNotification, Boolean> decoratedNotificationSent : decoratedNotificationSentMap.entrySet()) {

            if(decoratedNotificationSent.getValue()) {

                decoratedNotificationToMarkAsSent.add(decoratedNotificationSent.getKey());
            } 
            else {
                Integer attempts = decoratedNotificationSent.getKey().getSendingAttempts();

                attempts = attempts + 1;

                decoratedNotificationSent.getKey().setSendingAttempts(attempts);

                if(attempts >= Constants.MAX_ATTEMPTS) {

                    this.moveFailedDecoratedNotification(decoratedNotificationSent.getKey());
                }
                else {
                	this.saveDecoratedNotification(decoratedNotificationSent.getKey());
                }
            }
        }

        this.markDecoratedNotificationsAsSent(decoratedNotificationToMarkAsSent);
	}

    @Override
    public void setUrgentEnabled(Boolean urgentEnabled) {
        this.urgentEnabled = urgentEnabled;
    }

    public Boolean getUrgentEnabled() {
        return this.urgentEnabled;
    }

    protected void moveFailedDecoratedNotification(DecoratedNotification decoratedNotificationToDelete) {

        Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);

        persister.moveFailedDecoratedNotification(decoratedNotificationToDelete);

    }
    
    protected void saveDecoratedNotification(DecoratedNotification decoratedNotificationToSave) {

        Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);

        persister.saveDecoratedNotification(decoratedNotificationToSave);

    }

    private void markDecoratedNotificationsAsSent(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {
		
		Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);
		
		for (DecoratedNotification decoratedNotification : notSentDecoratedNotifications) {
			
			persister.markDecoratedNotificationAsSent(decoratedNotification);
		}
	}

	private Collection<DecoratedNotification> retrieveNotSentDecoratedNotifications() {
		
		Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);

        if(this.urgentEnabled) {
            return persister.retrieveUrgentAndNotSentDecoratedNotificationsForTopic(this.topic);
        }
        else {

            return persister.retrieveNotSentDecoratedNotificationsForTopic(this.topic);
        }

	}

    public void savePhysicalNotification(
            Recipient recipient, String subject, String notificationContent, Collection<ObjectId> filesAttachedIds) {

        PhysicalNotification physicalNotification =
                new PhysicalNotification(recipient, subject, notificationContent, filesAttachedIds);

        Persister persister = (Persister) SpringUtils.getBean(Constants.PERSISTER);

        persister.savePhysicalNotification(physicalNotification);

    }

	protected abstract Map<DecoratedNotification, Boolean> processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications);

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}

}
