package org.notificationengine.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.notificationengine.constants.Constants;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.spring.SpringUtils;

public class AdministratorSelector extends Selector {

	public AdministratorSelector(Topic topic, Map<String, String> options) {
		super(topic, options);
	}

	@Override
	protected Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic) {
		
		Properties localSettingsProperties = (Properties)SpringUtils.getBean(Constants.LOCAL_SETTINGS_PROPERTIES);
		
		String administratorAddress = localSettingsProperties.getProperty(Constants.ADMINISTRATOR_ADDRESS);
		
		Subscription subscription = new Subscription();
		subscription.setTopic(this.getTopic());
		subscription.setRecipient(new Recipient(administratorAddress));
		
		Collection<Subscription> result = new ArrayList<>();
		result.add(subscription);
		
		return result;
	}

}
