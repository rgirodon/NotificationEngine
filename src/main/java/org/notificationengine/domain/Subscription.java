package org.notificationengine.domain;

public class Subscription {

	private Topic topic;
	
	private Recipient recipient;

	public Subscription() {
		super();
	}

	public Subscription(Topic topic, Recipient recipient) {
		super();
		this.topic = topic;
		this.recipient = recipient;
	}

	@Override
	public String toString() {
		return "Subscription [topic=" + topic + ", recipient=" + recipient
				+ "]";
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}
	
	
}
