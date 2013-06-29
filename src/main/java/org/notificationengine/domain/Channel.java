package org.notificationengine.domain;

public class Channel {

	private String id;
	
	private Topic topic;

	public Channel(String id) {
		super();
		
		this.id = id;
	}

	@Override
	public String toString() {
		return "Channel [id=" + id + ", topic=" + topic + "]";
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
	
	
}
