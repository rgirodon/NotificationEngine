package org.notificationengine.notificator.mail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.notificator.Notificator;
import org.notificationengine.spring.SpringUtils;

public class SingleMultiTopicMailByRecipientFeederNotificator extends Notificator {

	public SingleMultiTopicMailByRecipientFeederNotificator(Topic topic) {
		
		super(topic);
	}

	@Override
	protected Map<DecoratedNotification, Boolean> processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {

        Map<DecoratedNotification, Boolean> result = new HashMap<>();
		
		SingleMultiTopicMailByRecipientNotificator singleMultiTopicMailByRecipientNotificator = (SingleMultiTopicMailByRecipientNotificator)SpringUtils.getBean(Constants.SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT_NOTIFICATOR);
		
		singleMultiTopicMailByRecipientNotificator.addDecoratedNotificationsToProcess(notSentDecoratedNotifications);

        for(DecoratedNotification notSentDecoratedNotification: notSentDecoratedNotifications) {

            result.put(notSentDecoratedNotification, Boolean.TRUE);
        }

        return result;
	}


}
