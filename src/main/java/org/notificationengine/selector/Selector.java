package org.notificationengine.selector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;
import org.notificationengine.spring.SpringUtils;

public abstract class Selector implements ISelector {

	private Topic topic;
	
	private Map<String, String> options;
	
	public Selector(Topic topic) {
		
		this.topic = topic;
		
		this.options = new HashMap<>();
	}
	
	public Selector(Topic topic, Map<String, String> options) {
		
		this.topic = topic;
		
		this.options = options;
	}

	public void process() {
		
		Collection<RawNotification> rawNotifications = this.retrieveRawNotifications();
		
		for (RawNotification rawNotification : rawNotifications) {
			
			Collection<Subscription> subscriptions = this.retrieveSubscriptionsForTopic(rawNotification.getTopic());
			
			for (Subscription subscription : subscriptions) {
				
				DecoratedNotification decoratedNotification = new DecoratedNotification(rawNotification, subscription.getRecipient());
				
				this.createDecoratedNotification(decoratedNotification);
			}
			
			this.markRawNotificationAsProcessed(rawNotification);
		}
	}

	private void markRawNotificationAsProcessed(RawNotification rawNotification) {
		
		Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);
		
		persister.markRawNotificationAsProcessed(rawNotification);
	}

	abstract protected Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic);

	private void createDecoratedNotification(
			DecoratedNotification decoratedNotification) {
		
		Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);
		
		persister.createDecoratedNotification(decoratedNotification);
	}

	private Collection<RawNotification> retrieveRawNotifications() {
		
		Persister persister = (Persister)SpringUtils.getBean(Constants.PERSISTER);
		
		return persister.retrieveNotProcessedRawNotificationsForTopic(this.topic);
	}
	
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
