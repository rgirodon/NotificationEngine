package org.notificationengine.web.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.notificationengine.web.controller.ConfigurationController;
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
		
		SingleMultiTopicMailByRecipientNotificator singleMultiTopicMailByRecipientNotificator =
                (SingleMultiTopicMailByRecipientNotificator)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT_NOTIFICATOR);
		
		SubscriptionController subscriptionController = (SubscriptionController)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.SUBSCRIPTION_CONTROLLER);

		ConfigurationController configurationController = (ConfigurationController)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.CONFIGURATION_CONTROLLER);

		timer.schedule(new NotificatorTask(singleMultiTopicMailByRecipientNotificator), Constants.NOTIFICATOR_TASK_DELAY, Constants.NOTIFICATOR_TASK_PERIOD);
		
		int cptChannel = 2;
		
		for (Channel channel : configuration.getChannels()) {
			
			Topic topic = channel.getTopic();
			
			ISelector selector = null;
            String selectorName = null;
			
			switch(channel.getSelectorType()) {
			
			case Constants.SELECTOR_TYPE_MONGODB :
				
				LOGGER.debug("Detected Selector of type " + Constants.SELECTOR_TYPE_MONGODB);

                selectorName = Constants.SELECTOR_TYPE_MONGODB;
				
				selector = new MongoDbSelector(topic);
				
				break;
				
			case Constants.SELECTOR_TYPE_CUSTOM :
				
				LOGGER.debug("Detected Selector of type " + Constants.SELECTOR_TYPE_CUSTOM);
				
				// get selector class
				String selectorClass = channel.getOption(Constants.SELECTOR_CLASS);		
				
				LOGGER.debug("Detected Selector class " + selectorClass);
				
				// instantiate it
				try {
					Class clazz = Class.forName(selectorClass);
					
					Constructor constructor = clazz.getConstructor(Topic.class, Map.class);
					
					selector = (ISelector)constructor.newInstance(topic, channel.getOptions());

                    selectorName = selectorClass;
				}
				catch(InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {

					LOGGER.warn(ExceptionUtils.getFullStackTrace(e));
					
					LOGGER.warn("Unable to instantiate class " + selectorClass + ", channel will be ignored");
					
					continue;
				}
				
				break;	
			}
			
			Long selectorTaskPeriod = Constants.SELECTOR_TASK_PERIOD;
			
			String strOptionSelectorTaskPeriod = channel.getOption(Constants.OPTION_SELECTOR_TASK_PERIOD);
			
			if (strOptionSelectorTaskPeriod != null) {
				
				try {
					selectorTaskPeriod = Long.parseLong(strOptionSelectorTaskPeriod);
					
					LOGGER.debug("selectorTaskPeriod set to : " + selectorTaskPeriod);
				}
				catch(NumberFormatException nfe) {
					
					LOGGER.debug("selectorTaskPeriod invalid : " + strOptionSelectorTaskPeriod);
					LOGGER.debug("selectorTaskPeriod set to default");
					
					selectorTaskPeriod = Constants.SELECTOR_TASK_PERIOD;
				}
			}
			else {
				LOGGER.debug("selectorTaskPeriod set to default");
			}
			
			timer.schedule(new SelectorTask(selector), cptChannel * Constants.SELECTOR_TASK_DELAY, selectorTaskPeriod);
			
			subscriptionController.addSelector(selectorName, selector);
			configurationController.addSelector(selectorName, selector);

			INotificator notificator = null;
				
			String mailTemplate = channel.getOption(Constants.MAIL_TEMPLATE);

            Boolean isHtmlTemplate = Boolean.valueOf(channel.getOption(Constants.IS_HTML_TEMPLATE));
			
			switch(channel.getNotificatorType()) {
			
			case Constants.NOTIFICATOR_TYPE_MULTIPLE_MAIL_BY_RECIPIENT :
				
				notificator = new MultipleMailByRecipientNotificator(topic, mailTemplate, isHtmlTemplate);
				
				break;
				
			case Constants.NOTIFICATOR_TYPE_SINGLE_MAIL_BY_RECIPIENT :
				
				notificator = new SingleMailByRecipientNotificator(topic, mailTemplate, isHtmlTemplate);
				
				break;
				
			case Constants.NOTIFICATOR_TYPE_SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT :
				
				if (!singleMultiTopicMailByRecipientNotificator.isActivated()) {
					
					singleMultiTopicMailByRecipientNotificator.activate();
					
					LOGGER.debug("SingleMultiTopicMailByRecipientNotificator activated");
				}
				
				singleMultiTopicMailByRecipientNotificator.add(mailTemplate, isHtmlTemplate, topic);
				
				notificator = new SingleMultiTopicMailByRecipientFeederNotificator(topic);
				
				break;
				
			case Constants.NOTIFICATOR_TYPE_CUSTOM :
				
				LOGGER.debug("Detected Notificator of type " + Constants.NOTIFICATOR_TYPE_CUSTOM);
				
				// get notificator class
				String notificatorClass = channel.getOption(Constants.NOTIFICATOR_CLASS);		
				
				LOGGER.debug("Detected Notificator class " + notificatorClass);
				
				// instantiate it
				try {
					Class clazz = Class.forName(notificatorClass);
					
					Constructor constructor = clazz.getConstructor(Topic.class, Map.class);
					
					notificator = (INotificator)constructor.newInstance(topic, channel.getOptions());
				}
				catch(InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {

					LOGGER.warn(ExceptionUtils.getFullStackTrace(e));
					
					LOGGER.warn("Unable to instantiate class " + notificatorClass + ", channel will be ignored");
					
					continue;
				}
				
				break;	
			}
					
			Long notificatorTaskPeriod = Constants.NOTIFICATOR_TASK_PERIOD;
			
			String strOptionNotificatorTaskPeriod = channel.getOption(Constants.OPTION_NOTIFICATOR_TASK_PERIOD);
			
			if (strOptionNotificatorTaskPeriod != null) {
				
				try {
					notificatorTaskPeriod = Long.parseLong(strOptionNotificatorTaskPeriod);
					
					LOGGER.debug("notificatorTaskPeriod set to : " + notificatorTaskPeriod);
				}
				catch(NumberFormatException nfe) {
					
					LOGGER.debug("notificatorTaskPeriod invalid : " + strOptionNotificatorTaskPeriod);
					LOGGER.debug("notificatorTaskPeriod set to default");
					
					notificatorTaskPeriod = Constants.NOTIFICATOR_TASK_PERIOD;
				}
			}
			else {
				LOGGER.debug("notificatorTaskPeriod set to default");
			}
			
			timer.schedule(new NotificatorTask(notificator), cptChannel * Constants.NOTIFICATOR_TASK_DELAY, notificatorTaskPeriod);
						
			cptChannel++;
		}
	}

}
