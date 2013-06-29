package org.notificationengine.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

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

public class ConfigurationReader {

	private static Logger LOGGER = Logger.getLogger(ConfigurationReader.class);
	
	private static ConfigurationReader instance;
	
	private ConfigurationReader() {		
	}
	
	public static ConfigurationReader getInstance() {
		if (null == instance) { 
            instance = new ConfigurationReader();
        }
        return instance;
	}
	
	public Configuration readConfiguration() {
		
		Configuration result = new Configuration();
		
		try {		
			LOGGER.info("Reading configuration file...");
			
			// TODO handle read this configuration file outside of app 
			
			InputStream configurationInputStream = this.getClass().getClassLoader().getResourceAsStream(Constants.CONFIGURATION_FILE_NAME);
			
			StringWriter configurationStringWriter = new StringWriter();		
		
			IOUtils.copy(configurationInputStream, configurationStringWriter);
			
			String configurationString = configurationStringWriter.toString();
			
			LOGGER.debug("Configuration : " + configurationString);
			
			Object configurationObj = JSONValue.parse(configurationString);
			
			JSONObject configurationJsonObj = (JSONObject)configurationObj;
			
			JSONArray channelsArray = (JSONArray)configurationJsonObj.get(Constants.CHANNELS);
			
			LOGGER.debug("Nbr of channels : " + channelsArray.size());
			
			for (int i = 0; i < channelsArray.size(); i++) {
				
				JSONObject channelJsonObj = (JSONObject)channelsArray.get(i);
				
				String id = (String)channelJsonObj.get("id");
				
				// TODO handle null value
				
				String topic = (String)channelJsonObj.get("topic");
				
				// TODO handle null value
				
				Channel channel = new Channel(id);
				channel.setTopic(new Topic(topic));
				
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
}
