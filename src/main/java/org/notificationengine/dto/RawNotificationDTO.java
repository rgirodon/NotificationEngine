package org.notificationengine.dto;

import java.util.HashMap;
import java.util.Map;

public class RawNotificationDTO {

	private String topic;

	private Map<String, Object> context;

	public RawNotificationDTO() {
		
		super();
		
		this.context = new HashMap<>();
	}

	@Override
	public String toString() {
		return "RawNotificationDTO [topic=" + topic + ", context=" + context
				+ "]";
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}
}
