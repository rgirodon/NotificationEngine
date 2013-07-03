package org.notificationengine.notificator.mail;

import java.util.Collection;

import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.notificator.Notificator;

public class MultipleMailByRecipientNotificator extends Notificator {

	public MultipleMailByRecipientNotificator(Topic topic) {
		super(topic);
	}

	@Override
	protected void processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {
		
		for (DecoratedNotification decoratedNotification : notSentDecoratedNotifications) {
			
			// TODO load the template
			
			// TODO merge the context and the template
			
			// TODO sent a mail to the recipient
		}
	}
}
