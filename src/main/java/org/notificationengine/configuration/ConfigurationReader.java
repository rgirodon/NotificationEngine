package org.notificationengine.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
			
			JSONArray channelsArray = (JSONArray)configurationJsonObj.get(Constants.CHANNELS);
			
			LOGGER.debug("Nbr of channels : " + channelsArray.size());
			
			for (int i = 0; i < channelsArray.size(); i++) {
				
				JSONObject channelJsonObj = (JSONObject)channelsArray.get(i);
				
				String id = (String)channelJsonObj.get(Constants.ID);
				
				// TODO handle null value
				
				String topic = (String)channelJsonObj.get(Constants.TOPIC);
				
				// TODO handle null value
				
				String selectorType = (String)channelJsonObj.get(Constants.SELECTOR_TYPE);
				
				// TODO handle null value
				
				String notificatorType = (String)channelJsonObj.get(Constants.NOTIFICATOR_TYPE);
				
				// TODO handle null value
				
				Channel channel = new Channel(id);
				channel.setTopic(new Topic(topic));
				channel.setSelectorType(selectorType);
				channel.setNotificatorType(notificatorType);
				
				// TODO all other properties (such as mailTemplate) should be considered as options and handled dynamically
				
				String mailTemplate = (String)channelJsonObj.get(Constants.MAIL_TEMPLATE);
				channel.addOption(Constants.MAIL_TEMPLATE, mailTemplate);
				
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

	public String getConfigDirectory() {
		return configDirectory;
	}

	public void setConfigDirectory(String configDirectory) {
		this.configDirectory = configDirectory;
	}
}
