package org.notificationengine.task;

import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.notificationengine.notificator.INotificator;
import org.notificationengine.selector.ISelector;

public class NotificatorTask extends TimerTask {

	private static Logger LOGGER = Logger.getLogger(NotificatorTask.class);
	
	private INotificator notificator;
	
	public NotificatorTask(INotificator notificator) {
		
		LOGGER.info("Instantiating Notificator Task : " + notificator.getClass().getName());
		
		this.notificator = notificator;
		
		LOGGER.info("Notificator Task instantiated : " + notificator.getClass().getName());
	}
	
	@Override
	public void run() {
		
		LOGGER.info("Launching Notificator Task : " + notificator.getClass().getName());
		
		this.notificator.process();
		
		LOGGER.info("Notificator Task has finished : " + notificator.getClass().getName());
	}

}
