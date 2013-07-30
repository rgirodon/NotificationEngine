package org.notificationengine.notificator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
		
		Boolean success = this.processNotSentDecoratedNotifications(notSentDecoratedNotifications);

        if(success){

            this.markDecoratedNotificationsAsSent(notSentDecoratedNotifications);
        }
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

	protected abstract Boolean processNotSentDecoratedNotifications(
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
