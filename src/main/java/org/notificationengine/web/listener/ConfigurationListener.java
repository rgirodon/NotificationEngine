package org.notificationengine.web.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.notificationengine.authentication.Authenticator;
import org.notificationengine.authentication.TokenService;
import org.notificationengine.authentication.activedirectory.ActiveDirectoryAuthenticator;
import org.notificationengine.authentication.mongodb.MongoAuthenticator;
import org.notificationengine.authentication.mongodb.MongoTokenService;
import org.notificationengine.cleaner.TokenCleaner;
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
import org.notificationengine.selector.holdinnotification.HoldInNotificationSelector;
import org.notificationengine.selector.mongodb.MongoDbSelector;
import org.notificationengine.spring.SpringUtils;
import org.notificationengine.task.NotificatorTask;
import org.notificationengine.task.SelectorTask;
import org.notificationengine.task.TokenCleanerTask;
import org.notificationengine.web.controller.ConfigurationController;
import org.notificationengine.web.controller.SubscriptionController;
import org.notificationengine.web.controller.TokenController;
import org.notificationengine.web.controller.UserController;
import org.notificationengine.web.interceptor.TokenInterceptor;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
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

		UserController userController = (UserController)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.USER_CONTROLLER);

        TokenInterceptor tokenInterceptor = (TokenInterceptor)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.TOKEN_INTERCEPTOR);

        TokenController tokenController = (TokenController)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.TOKEN_CONTROLLER);

        TokenService tokenService = new MongoTokenService();

        tokenInterceptor.setTokenService(tokenService);

        userController.setTokenService(tokenService);

        tokenController.setTokenService(tokenService);

        TokenCleaner tokenCleaner = new TokenCleaner(tokenService);

		timer.schedule(new NotificatorTask(singleMultiTopicMailByRecipientNotificator), Constants.NOTIFICATOR_TASK_DELAY, Constants.NOTIFICATOR_TASK_PERIOD);

		timer.schedule(new TokenCleanerTask(tokenCleaner), Constants.TOKEN_CLEANER_TASK_DELAY, Constants.TOKEN_CLEANER_TASK_PERIOD);

		int cptChannel = 2;

        String authenticationType = configuration.getAuthenticationType();

        switch (authenticationType) {

            case Constants.MONGO_AUTHENTICATOR:

                LOGGER.debug("Detected authenticator of type " + Constants.MONGO_AUTHENTICATOR);

                userController.setAuthenticator(new MongoAuthenticator());

                break;

            case Constants.ACTIVE_DIRECTORY_AUTHENTICATOR:

                LOGGER.debug("Detected authenticator of type " + Constants.ACTIVE_DIRECTORY_AUTHENTICATOR);

                ContextSource contextSource = (ContextSource)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.CONTEXT_SOURCE);

                LdapTemplate ldapTemplate = (LdapTemplate)WebApplicationContextUtils.getWebApplicationContext(context.getServletContext()).getBean(Constants.LDAP_TEMPLATE);

                userController.setAuthenticator(new ActiveDirectoryAuthenticator(contextSource, ldapTemplate));

                break;

            case Constants.CUSTOM_AUTHENTICATOR:

                String customAuthenticatorClass = configuration.getCustomAuthenticatorClass();

                try {
                    Class clazz = Class.forName(customAuthenticatorClass);

                    Constructor constructor = clazz.getConstructor();

                    Authenticator authenticator = (Authenticator)constructor.newInstance();

                    userController.setAuthenticator(authenticator);

                }
                catch(InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {

                    LOGGER.warn(ExceptionUtils.getFullStackTrace(e));

                    LOGGER.warn("Unable to instantiate class " + customAuthenticatorClass + ", set to default authenticator");

                    userController.setAuthenticator(new MongoAuthenticator());
                }
                break;
        }
		
		for (Channel channel : configuration.getChannels()) {
			
			Topic topic = channel.getTopic();
			
			ISelector selector = null;
            ISelector urgentSelector = null;
            String selectorName = null;

            Boolean urgentEnabled = Boolean.FALSE;

            String strUrgentEnabled = channel.getOption(Constants.URGENT_ENABLED);

            if( strUrgentEnabled != null ) {
                urgentEnabled = Boolean.valueOf(strUrgentEnabled);
            }

            LOGGER.debug("This channel is urgent enabled: " + urgentEnabled);

			switch(channel.getSelectorType()) {
			
			case Constants.SELECTOR_TYPE_MONGODB :
				
				LOGGER.debug("Detected Selector of type " + Constants.SELECTOR_TYPE_MONGODB);

                selectorName = Constants.SELECTOR_TYPE_MONGODB;
				
				selector = new MongoDbSelector(topic);

                if(urgentEnabled) {

                    LOGGER.debug("An urgent " + Constants.SELECTOR_TYPE_MONGODB + " is instantiated");

                    urgentSelector = new MongoDbSelector(topic);
                    urgentSelector.setUrgentSelector(Boolean.TRUE);
                }
				
				break;

            case Constants.SELECTOR_TYPE_HOLD_IN_NOTIFICATION :

                LOGGER.debug("Detected Selector of type " + Constants.SELECTOR_TYPE_HOLD_IN_NOTIFICATION);

                selectorName = Constants.SELECTOR_TYPE_HOLD_IN_NOTIFICATION;

                selector = new HoldInNotificationSelector(topic);

                if(urgentEnabled) {

                    LOGGER.debug("An urgent " + Constants.SELECTOR_TYPE_HOLD_IN_NOTIFICATION + " is instantiated");

                    urgentSelector = new HoldInNotificationSelector(topic);
                    urgentSelector.setUrgentSelector(Boolean.TRUE);
                }

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

                    if(urgentEnabled) {

                        LOGGER.debug("An urgent " + Constants.SELECTOR_TYPE_CUSTOM + " is instantiated");

                        urgentSelector = (ISelector)constructor.newInstance(topic, channel.getOptions());
                        urgentSelector.setUrgentSelector(Boolean.TRUE);
                    }

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

            if(urgentEnabled) {

                LOGGER.debug("Urgent selection task is added to timer");

                timer.schedule(new SelectorTask(urgentSelector), cptChannel * Constants.URGENT_SELECTOR_TASK_DELAY, Constants.URGENT_SELECTOR_TASK_PERIOD);
            }
			
			subscriptionController.addSelector(selectorName, selector);
			configurationController.addSelector(selectorName, selector);

			INotificator notificator = null;
				
			String mailTemplate = channel.getOption(Constants.MAIL_TEMPLATE);

            String urgentMailTemplate = null;

            if(urgentEnabled) {
                urgentMailTemplate = channel.getOption(Constants.URGENT_MAIL_TEMPLATE);

                Boolean isUrgentHtmlTemplate = Boolean.valueOf(channel.getOption(Constants.IS_URGENT_MAIL_TEMPLATE));

                if(isUrgentHtmlTemplate == null) {

                    isUrgentHtmlTemplate = Boolean.FALSE;
                }

                if (urgentMailTemplate == null) {

                    LOGGER.error("No urgentMailTemplate defined, no notificator will be set!");
                }
                else {

                    INotificator urgentNotificator = new MultipleMailByRecipientNotificator(topic, urgentMailTemplate, isUrgentHtmlTemplate);

                    urgentNotificator.setUrgentEnabled(Boolean.TRUE);

                    LOGGER.debug("An urgent notification task is added to the timer");

                    timer.schedule(new NotificatorTask(urgentNotificator), cptChannel * Constants.URGENT_NOTIFICATOR_TASK_DELAY, Constants.URGENT_NOTIFICATOR_TASK_PERIOD);

                }
            }

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
