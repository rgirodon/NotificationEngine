package org.notificationengine.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.notificationengine.constants.Constants;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.spring.SpringUtils;

public class AdministratorSelector extends Selector {

	public AdministratorSelector(Topic topic, Map<String, String> options) {
		super(topic, options);
	}

    @Override
	public Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic) {
		
		Properties localSettingsProperties = (Properties)SpringUtils.getBean(Constants.LOCAL_SETTINGS_PROPERTIES);
		
		String administratorAddress = localSettingsProperties.getProperty(Constants.ADMINISTRATOR_ADDRESS);

        String administratorDisplayName = localSettingsProperties.getProperty(Constants.ADMINISTRATOR_DISPLAY_NAME);
		
		Subscription subscription = new Subscription();
		subscription.setTopic(this.getTopic());
		subscription.setRecipient(new Recipient(administratorAddress, administratorDisplayName));
		
		Collection<Subscription> result = new ArrayList<>();
		result.add(subscription);
		
		return result;
	}

    @Override
    public Collection<Subscription> retrieveSubscriptionsForRawNotification(RawNotification rawNotification) {
        Topic topic = rawNotification.getTopic();

        return this.retrieveSubscriptionsForTopic(topic);
    }

    @Override
    public Collection<Subscription> retrieveSubscriptions() {

        Properties localSettingsProperties = (Properties)SpringUtils.getBean(Constants.LOCAL_SETTINGS_PROPERTIES);

        String administratorAddress = localSettingsProperties.getProperty(Constants.ADMINISTRATOR_ADDRESS);

        String administratorDisplayName = localSettingsProperties.getProperty(Constants.ADMINISTRATOR_DISPLAY_NAME);

        Subscription subscription = new Subscription();
        subscription.setTopic(this.getTopic());
        subscription.setRecipient(new Recipient(administratorAddress, administratorDisplayName));

        Collection<Subscription> result = new ArrayList<>();
        result.add(subscription);

        return result;

    }
}
