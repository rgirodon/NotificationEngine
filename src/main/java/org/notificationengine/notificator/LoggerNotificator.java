package org.notificationengine.notificator;

import java.util.Collection;
import java.util.HashMap;
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
	protected Map<DecoratedNotification, Boolean> processNotSentDecoratedNotifications(
			Collection<DecoratedNotification> notSentDecoratedNotifications) {

        Map<DecoratedNotification, Boolean> result = new HashMap<>();
		
		for (DecoratedNotification notSentDecoratedNotification : notSentDecoratedNotifications) {
		
			LOGGER.info("Sent notification : " + notSentDecoratedNotification.toString());

            result.put(notSentDecoratedNotification, Boolean.TRUE);
		}

        return result;
	}

}
