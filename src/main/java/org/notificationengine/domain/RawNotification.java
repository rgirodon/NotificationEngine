package org.notificationengine.domain;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

public class RawNotification {

    private ObjectId _id;
	
	private Boolean processed;

	private Topic topic;
	
	private Map<String, Object> context;

	public RawNotification() {
		super();
		
		this.context = new HashMap<>();
	}

	public RawNotification(Topic topic) {
		this();
		
		this.topic = topic;
	}

	@Override
	public String toString() {
		return "RawNotification [_id=" + _id + ", processed=" + processed
				+ ", topic=" + topic + ", context=" + context + "]";
	}

	public void put(String key, String value) {
		
		this.context.put(key, value);
	}
	
	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public Boolean getProcessed() {
		return processed;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}
	
	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	
	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}
}