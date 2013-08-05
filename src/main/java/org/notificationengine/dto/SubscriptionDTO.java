package org.notificationengine.dto;

public class SubscriptionDTO {

	private String topic;
	
	private String recipient;

    private String displayName;

	@Override
	public String toString() {
		return "SubscriptionDTO [topic=" + topic + ", recipient=" + recipient + ", displayName=" + displayName
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
