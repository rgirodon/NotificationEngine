package org.notificationengine.domain;

import java.util.HashMap;
import java.util.Map;

public class Channel {

	private String id;
	
	private Topic topic;
	
	private String selectorType;
	
	private String notificatorType;
	
	private Map<String, String> options;

	public Channel(String id) {
		super();
		
		this.id = id;
		
		this.options = new HashMap<>();
	}

	public void addOption(String option, String value) {
		
		this.options.put(option, value);
	}
	
	public String getOption(String option) {
		
		return this.options.get(option);
	}
	
	@Override
	public String toString() {
		return "Channel [id=" + id + ", topic=" + topic + ", selectorType="
				+ selectorType + ", notificatorType=" + notificatorType + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public String getSelectorType() {
		return selectorType;
	}

	public void setSelectorType(String selectorType) {
		this.selectorType = selectorType;
	}

	public String getNotificatorType() {
		return notificatorType;
	}

	public void setNotificatorType(String notificatorType) {
		this.notificatorType = notificatorType;
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}
	
	
}
