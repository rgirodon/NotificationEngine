package org.notificationengine.selector.holdinnotification;

import org.apache.log4j.Logger;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.selector.Selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class HoldInNotificationSelector extends Selector {

    private static Logger LOGGER = Logger.getLogger(HoldInNotificationSelector.class);

    public HoldInNotificationSelector(Topic topic) {
        super(topic);
    }

    public HoldInNotificationSelector(Topic topic, Map<String, String> options) {
        super(topic, options);
    }

    @Override
    public Collection<Subscription> retrieveSubscriptionsForRawNotification(RawNotification rawNotification) {
        Collection<Subscription> result = new ArrayList<>();

        Topic topic = rawNotification.getTopic();

        Map<String, Object> context = rawNotification.getContext();

        ArrayList<LinkedHashMap<String, String>> recipients = (ArrayList<LinkedHashMap<String, String>>) context.get(Constants.RECIPIENTS);

        LOGGER.info("Recipients found in raw Notification: " + recipients.toString());

        for(LinkedHashMap<String, String> jsonRecipient : recipients) {

            String email = jsonRecipient.get(Constants.EMAIL);
            String displayName = jsonRecipient.get(Constants.DISPLAY_NAME);

            Recipient recipient = new Recipient(email, displayName);

            Subscription subscription = new Subscription(topic, recipient);

            LOGGER.info("Subscription found in rawNotification: " + subscription.toString());

            result.add(subscription);
        }

        return result;

    }

    @Override
    public Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic) {
        return new ArrayList<Subscription>();
    }

    @Override
    public Collection<Subscription> retrieveSubscriptions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
