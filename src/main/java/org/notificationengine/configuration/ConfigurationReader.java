package org.notificationengine.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.Channel;
import org.notificationengine.domain.Topic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value=Constants.CONFIGURATION_READER)
public class ConfigurationReader {

	private static Logger LOGGER = Logger.getLogger(ConfigurationReader.class);
	
	private static String[] KNOWN_SELECTOR_TYPES = {Constants.SELECTOR_TYPE_CUSTOM,
                                                    Constants.SELECTOR_TYPE_MONGODB,
                                                    Constants.SELECTOR_TYPE_HOLD_IN_NOTIFICATION};
	
	private static String[] KNOWN_NOTIFICATOR_TYPES = {Constants.NOTIFICATOR_TYPE_CUSTOM, 
														Constants.NOTIFICATOR_TYPE_MULTIPLE_MAIL_BY_RECIPIENT,
														Constants.NOTIFICATOR_TYPE_SINGLE_MAIL_BY_RECIPIENT,
														Constants.NOTIFICATOR_TYPE_SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT};
	
	private static String[] KNOWN_CHANNEL_KEYS = {Constants.ID,
												  Constants.TOPIC,
												  Constants.NOTIFICATOR_TYPE,
												  Constants.SELECTOR_TYPE};

    private static String[] KNOWN_AUTHENTICATION_TYPES = {Constants.MONGO_AUTHENTICATOR,
                                                          Constants.ACTIVE_DIRECTORY_AUTHENTICATOR,
                                                          Constants.CUSTOM_AUTHENTICATOR};
		
	@Value("${config.directory}")
	private String configDirectory;
	
	public ConfigurationReader() {		
		
		LOGGER.debug("ConfigurationReader instantiated");
	}
	
	public Configuration readConfiguration() {
		
		Configuration result = new Configuration();
		
		try {		
			LOGGER.info("Reading configuration file...");
		
			String configurationString = FileUtils.readFileToString(new File(this.configDirectory + System.getProperty("file.separator") + Constants.CONFIGURATION_FILE_NAME));
			
			LOGGER.debug("Configuration : " + configurationString);
			
			Object configurationObj = JSONValue.parse(configurationString);
			
			JSONObject configurationJsonObj = (JSONObject)configurationObj;

            String authenticationType = (String) configurationJsonObj.get(Constants.AUTHENTICATION_TYPE);

            if (!this.isKnownAuthenticationType(authenticationType)) {

                LOGGER.warn("Authentication type is not defined or isn't know, set to default (MongoAuthenticator)");

                authenticationType = Constants.MONGO_AUTHENTICATOR;
            }

            if (authenticationType.equalsIgnoreCase(Constants.CUSTOM_AUTHENTICATOR)) {

                String customAuthenticatorClass = (String) configurationJsonObj.get(Constants.CUSTOM_AUTHENTICATOR_CLASS);

                result.setCustomAuthenticatorClass(customAuthenticatorClass);

                if (StringUtils.isEmpty(customAuthenticatorClass)) {

                    LOGGER.warn("No class was defined for the custom authenticator, set to default (MongoAuthenticator)");

                    authenticationType = Constants.MONGO_AUTHENTICATOR;
                }
            }

            result.setAuthenticationType(authenticationType);
			
			JSONArray channelsArray = (JSONArray)configurationJsonObj.get(Constants.CHANNELS);
			
			LOGGER.debug("Nbr of channels : " + channelsArray.size());
			
			for (int i = 0; i < channelsArray.size(); i++) {
				
				JSONObject channelJsonObj = (JSONObject)channelsArray.get(i);
				
				String id = (String)channelJsonObj.get(Constants.ID);
				
				if (StringUtils.isEmpty(id)) {
					
					LOGGER.warn("Found a channel without id, it will be ignored");
					continue;
				}
				
				if (result.hasChannelWithId(id)) {
					
					LOGGER.warn("Found duplicate channel id [" + id + "], it will be ignored");
					continue;
				}
				
				String topic = (String)channelJsonObj.get(Constants.TOPIC);
				
				if (StringUtils.isEmpty(topic)) {
					
					LOGGER.warn("Found a channel without topic, it will be ignored");
					continue;
				}
				
				if (result.hasChannelWithTopic(topic)) {
					
					LOGGER.warn("Found multiple channels for same topic [" + topic + "], it will be ignored");
					continue;
				}
				
				String selectorType = (String)channelJsonObj.get(Constants.SELECTOR_TYPE);
				
				if (StringUtils.isEmpty(selectorType)) {
					
					// this is the default selector type
					selectorType = Constants.SELECTOR_TYPE_MONGODB;
				}
				
				// if selectorType is not of a known type or custom, ignore channel
				if (!this.isKnownSelectorType(selectorType)) {
					
					LOGGER.warn("Found a channel with an unknown selectorType [" + selectorType + "], it will be ignored");
					continue;
				}
				
				String notificatorType = (String)channelJsonObj.get(Constants.NOTIFICATOR_TYPE);
				
				if (StringUtils.isEmpty(notificatorType)) {
					
					// this is the default notificator type
					notificatorType = Constants.NOTIFICATOR_TYPE_SINGLE_MAIL_BY_RECIPIENT;
				}
				
				// if notificatorType is not of a known type or custom, ignore channel
				if (!this.isKnownNotificatorType(notificatorType)) {
					
					LOGGER.warn("Found a channel with an unknown notificatorType [" + notificatorType + "], it will be ignored");
					continue;
				}
				
				Channel channel = new Channel(id);
				channel.setTopic(new Topic(topic));
				channel.setSelectorType(selectorType);
				channel.setNotificatorType(notificatorType);
				
				// all other properties (such as mailTemplate) should be considered as options and handled dynamically
				Set<String> keys = channelJsonObj.keySet();
				for (String key : keys) {
					
					if (this.isChannelOption(key)) {
						
						String value = (String)channelJsonObj.get(key);
						
						LOGGER.debug("Found option " + key + " : " + value);
						
						channel.addOption(key, value);
					}
				}
				
				LOGGER.debug("Found channel : " + channel);
				
				result.addChannel(channel);
			}
			
			LOGGER.info("Configuration file read.");
		} 
		catch (IOException e) {
			
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			
			LOGGER.info("Configuration file read.");
		}
		
		return result;
	}

	private boolean isChannelOption(String key) {

		return (!ArrayUtils.contains(KNOWN_CHANNEL_KEYS, key));
	}

	private boolean isKnownSelectorType(String selectorType) {

		return ArrayUtils.contains(KNOWN_SELECTOR_TYPES, selectorType);
	}

	private boolean isKnownNotificatorType(String notificatorType) {
		
		return ArrayUtils.contains(KNOWN_NOTIFICATOR_TYPES, notificatorType);
	}

    private boolean isKnownAuthenticationType(String authenticationType) {

        if (!StringUtils.isEmpty(authenticationType)) {
            return ArrayUtils.contains(KNOWN_AUTHENTICATION_TYPES, authenticationType);
        }

        return false;
    }

	public String getConfigDirectory() {
		return configDirectory;
	}

	public void setConfigDirectory(String configDirectory) {
		this.configDirectory = configDirectory;
	}
}
