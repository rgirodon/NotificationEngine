package org.notificationengine.selector;

import java.util.Map;

import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;

public abstract class SelectorWriteEnabled extends Selector implements
		ISelectorWriteEnabled {

	public SelectorWriteEnabled(Topic topic) {
		super(topic);
    }

    public SelectorWriteEnabled(Topic topic, Map<String, String> options) {
        super(topic, options);
    }

    @Override
    abstract public void createSubscription(Subscription subscription);

    @Override
    abstract public void deleteSubscription(String email, String topic);
}
