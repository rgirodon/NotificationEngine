package org.notificationengine.selector;

import java.util.Collection;

import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;

public interface ISelector {

	public void process();
	
	public Collection<Subscription> retrieveSubscriptionsForRawNotification(RawNotification rawNotification);

	public Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic);

	public Collection<Subscription> retrieveSubscriptions();

    public void setUrgentSelector(Boolean urgentSelector);
}