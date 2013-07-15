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
import org.notificationengine.notificator.mail.SingleMailByRecipientNotificator;
import org.notificationengine.notificator.mail.SingleMultiTopicMailByRecipientFeederNotificator;
import org.notificationengine.notificator.mail.SingleMultiTopicMailByRecipientNotificator;
import org.notificationengine.selector.ISelector;
import org.notificationengine.selector.mongodb.MongoDbSelector;
import org.notificationengine.spring.SpringUtils;
import org.notificationengine.task.NotificatorTask;
import org.notificationengine.task.SelectorTask;
import org.notificationengine.web.controller.SubscriptionController;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
		
		SingleMultiTopicMailByRecipientNotificator singleMultiTopicMailByRecipientNotificator = (SingleMultiTopicMailByRecipientNotificator)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT_NOTIFICATOR);
		
		// TODO set period configurable
		timer.schedule(new NotificatorTask(singleMultiTopicMailByRecipientNotificator), Constants.NOTIFICATOR_TASK_DELAY, Constants.NOTIFICATOR_TASK_PERIOD);
		
		int cptChannel = 1;
		
		for (Channel channel : configuration.getChannels()) {
			
			Topic topic = channel.getTopic();
			
			ISelector selector = null;
			
			switch(channel.getSelectorType()) {
			
			case Constants.SELECTOR_TYPE_MONGODB :
				
				LOGGER.debug("Detected Selector of type " + Constants.SELECTOR_TYPE_MONGODB);
				
				selector = new MongoDbSelector(topic);
				
				SubscriptionController subscriptionController = (SubscriptionController)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.SUBSCRIPTION_CONTROLLER);
				
				if (!subscriptionController.isActivated()) {
					
					subscriptionController.activate((MongoDbSelector)selector);
					
					LOGGER.debug("SubscriptionController activated");
				}
				
				break;
			}
			
			// TODO set period configurable
			timer.schedule(new SelectorTask(selector), cptChannel * Constants.SELECTOR_TASK_DELAY, Constants.SELECTOR_TASK_PERIOD);
			
			
			INotificator notificator = null;
				
			String mailTemplate = channel.getOption(Constants.MAIL_TEMPLATE);
			
			switch(channel.getNotificatorType()) {
			
			case Constants.NOTIFICATOR_TYPE_MULTIPLE_MAIL_BY_RECIPIENT :
				
				notificator = new MultipleMailByRecipientNotificator(topic, mailTemplate);
				
				break;
				
			case Constants.NOTIFICATOR_TYPE_SINGLE_MAIL_BY_RECIPIENT :
				
				notificator = new SingleMailByRecipientNotificator(topic, mailTemplate);
				
				break;
				
			case Constants.NOTIFICATOR_TYPE_SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT :
				
				if (!singleMultiTopicMailByRecipientNotificator.isActivated()) {
					
					singleMultiTopicMailByRecipientNotificator.activate();
					
					LOGGER.debug("SingleMultiTopicMailByRecipientNotificator activated");
				}
				
				singleMultiTopicMailByRecipientNotificator.add(mailTemplate, topic);
				
				notificator = new SingleMultiTopicMailByRecipientFeederNotificator(topic);
				
				break;
			}
					
			// TODO set period configurable
			timer.schedule(new NotificatorTask(notificator), cptChannel * Constants.NOTIFICATOR_TASK_DELAY, Constants.NOTIFICATOR_TASK_PERIOD);
						
			cptChannel++;
		}
	}
	
}
