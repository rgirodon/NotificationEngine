package org.notificationengine.selector;

import org.notificationengine.domain.Subscription;

public interface ISelectorWriteEnabled extends ISelector {

	public void createSubscription(Subscription subscription);

    public void deleteSubscription(String email, String topic);
}
