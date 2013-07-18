package org.notificationengine.persistance;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.mongodb.ServerAddress;

public class MongoDbUtils {

	private static Logger LOGGER = Logger.getLogger(MongoDbUtils.class);
	
	public static String getHostFromSingleServerUrl(String url) {
		
		String[] splittedUrl = StringUtils.split(url, ":");

		return splittedUrl[0];
	}

	public static int getPortFromSingleServerUrl(String url) {
		
		String[] splittedUrl = StringUtils.split(url, ":");

		return Integer.parseInt(splittedUrl[1]);
	}

	public static List<ServerAddress> getServerAddressListFromMultipleServerUrl(
			String url) {
		
		List<ServerAddress> addrs = new ArrayList<>();
		
		String[] splittedUrl = StringUtils.split(url, ",");
		
		for (String singleServerUrl : splittedUrl) {
			try {
				ServerAddress addr = new ServerAddress(getHostFromSingleServerUrl(singleServerUrl),
												   	   getPortFromSingleServerUrl(singleServerUrl));
				
				addrs.add(addr);
			}
			catch(UnknownHostException uhe) {
			
				LOGGER.error(ExceptionUtils.getFullStackTrace(uhe));
				
				LOGGER.error("Unable to build server address list");
			}
		}
		
		return addrs;
	}

}
