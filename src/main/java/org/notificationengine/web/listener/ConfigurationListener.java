package org.notificationengine.web.listener;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.notificationengine.configuration.Configuration;
import org.notificationengine.configuration.ConfigurationReader;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.Channel;
import org.notificationengine.domain.Topic;
import org.notificationengine.notificator.INotificator;
import org.notificationengine.notificator.mail.MultipleMailByRecipientNotificator;
import org.notificationengine.selector.ISelector;
import org.notificationengine.selector.mongodb.MongoDbSelector;
import org.notificationengine.spring.SpringUtils;
import org.notificationengine.task.NotificatorTask;
import org.notificationengine.task.SelectorTask;

/**
 * Application Lifecycle Listener implementation class ConfigurationListener
 *
 */
public class ConfigurationListener implements ServletContextListener {

	private static Logger LOGGER = Logger.getLogger(ConfigurationListener.class);
	
    /**
     * Default constructor. 
     */
    public ConfigurationListener() {

    }

	@Override
	public void contextDestroyed(ServletContextEvent context) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		
		ConfigurationReader configurationReader = (ConfigurationReader)SpringUtils.getBean(Constants.CONFIGURATION_READER);
		
		Configuration configuration = configurationReader.readConfiguration();
		
		Timer timer = new Timer();
		
		int cptChannel = 1;
		
		for (Channel channel : configuration.getChannels()) {
			
			Topic topic = channel.getTopic();
			
			ISelector selector = null;
			
			switch(channel.getSelectorType()) {
			
			case Constants.SELECTOR_TYPE_MONGODB :
				
				selector = new MongoDbSelector(topic);
				
				// TODO set period configurable
				timer.schedule(new SelectorTask(selector), cptChannel * Constants.SELECTOR_TASK_DELAY, Constants.SELECTOR_TASK_PERIOD);
				
				break;
			}
			
			INotificator notificator = null;
				
			switch(channel.getNotificatorType()) {
			
			case Constants.NOTIFICATOR_TYPE_MULTIPLE_MAIL_BY_RECIPIENT :
				
				notificator = new MultipleMailByRecipientNotificator(topic, channel.getOption(Constants.MAIL_TEMPLATE));
				
				// TODO set period configurable
				timer.schedule(new NotificatorTask(notificator), cptChannel * Constants.NOTIFICATOR_TASK_DELAY, Constants.NOTIFICATOR_TASK_PERIOD);
				
				break;
			}
			
			cptChannel++;
		}
	}
	
}
