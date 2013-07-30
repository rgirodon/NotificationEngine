package org.notificationengine.notificator;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;

public class LoggerNotificator extends Notificator {

	private static Logger LOGGER = Logger.getLogger(LoggerNotificator.class);
	
	public LoggerNotificator(Topic topic, Map<String, String> options) {
		super(topic, options);
	}

	@Override
	protected Boolean processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {
		
		for (DecoratedNotification notSentDecoratedNotification : notSentDecoratedNotifications) {
		
			LOGGER.info("Sent notification : " + notSentDecoratedNotification.toString());
		}

        return Boolean.TRUE;
	}

}
