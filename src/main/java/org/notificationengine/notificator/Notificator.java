package org.notificationengine.notificator;

import java.util.Collection;

import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;
import org.notificationengine.spring.SpringUtils;

public abstract class Notificator implements INotificator {

	private Topic topic;
	
	public Notificator(Topic topic) {
		
		super();
		
		this.topic = topic;
	}

	@Override
	public void process() {
		
		Collection<DecoratedNotification> notSentDecoratedNotifications = this.retrieveNotSentDecoratedNotifications();
		
		this.processNotSentDecoratedNotifications(notSentDecoratedNotifications);
		
		this.markDecoratedNotificationsAsSent(notSentDecoratedNotifications);
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

	protected abstract void processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications);

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

}
