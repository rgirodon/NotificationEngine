package org.notificationengine.domain;

public class Channel {

	private String id;
	
	private Topic topic;
	
	private String selectorType;
	
	private String notificatorType;

	public Channel(String id) {
		super();
		
		this.id = id;
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
	
	
}
