package org.notificationengine.notificator.mail;

import java.util.Collection;

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
	protected Boolean processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {

        Boolean result = Boolean.TRUE;
		
		SingleMultiTopicMailByRecipientNotificator singleMultiTopicMailByRecipientNotificator = (SingleMultiTopicMailByRecipientNotificator)SpringUtils.getBean(Constants.SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT_NOTIFICATOR);
		
		singleMultiTopicMailByRecipientNotificator.addDecoratedNotificationsToProcess(notSentDecoratedNotifications);

        return result;
	}


}
