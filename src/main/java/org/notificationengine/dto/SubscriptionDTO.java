package org.notificationengine.dto;

public class SubscriptionDTO {

	private String topic;
	
	private String recipient;

	@Override
	public String toString() {
		return "SubscriptionDTO [topic=" + topic + ", recipient=" + recipient
				+ "]";
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
}
