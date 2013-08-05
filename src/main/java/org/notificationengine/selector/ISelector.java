package org.notificationengine.selector;

import java.util.Collection;

import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;

public interface ISelector {

	public void process();
	
	public Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic);
	 
	public Collection<Subscription> retrieveSubscriptions();
}