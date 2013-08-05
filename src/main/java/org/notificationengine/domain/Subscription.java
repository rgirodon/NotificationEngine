package org.notificationengine.domain;

import org.bson.types.ObjectId;

public class Subscription {

	private ObjectId _id;
	
	private Topic topic;
	
	private Recipient recipient;

    private String displayName;

	public Subscription() {
		super();
	}

	public Subscription(Topic topic, Recipient recipient, String displayName) {
		super();
		this.topic = topic;
		this.recipient = recipient;
        this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "Subscription [topic=" + topic + ", recipient=" + recipient + ", displayName=" + displayName
				+ "]";
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
	
	
}
