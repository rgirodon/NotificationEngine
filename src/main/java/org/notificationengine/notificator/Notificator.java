package org.notificationengine.notificator;

import java.util.*;

import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;
import org.notificationengine.spring.SpringUtils;

public abstract class Notificator implements INotificator {

	private Topic topic;
	
	private Map<String, String> options;
	
	public Notificator(Topic topic) {
		
		this.topic = topic;
		
		this.options = new HashMap<>();
	}
	
	public Notificator(Topic topic, Map<String, String> options) {
		
		this.topic = topic;
		
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

        }

        this.markDecoratedNotificationsAsSent(decoratedNotificationToMarkAsSent);
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
		
		return persister.retrieveNotSentDecoratedNotificationsForTopic(this.topic);
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
