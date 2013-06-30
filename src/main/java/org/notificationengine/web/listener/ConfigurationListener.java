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
import org.notificationengine.selector.ISelector;
import org.notificationengine.selector.mongodb.MongoDbSelector;
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
		
		Configuration configuration = ConfigurationReader.getInstance().readConfiguration();
		
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
			
			cptChannel++;
		}
	}
	
}
