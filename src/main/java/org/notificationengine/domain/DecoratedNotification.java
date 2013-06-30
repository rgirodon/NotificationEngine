package org.notificationengine.domain;

import org.bson.types.ObjectId;

public class DecoratedNotification {

	private ObjectId _id;
	
	private Recipient recipient;
	
	private RawNotification rawNotification;

	public DecoratedNotification(RawNotification rawNotification,
			Recipient recipient) {
		super();
		this.rawNotification = rawNotification;
		this.recipient = recipient;		
	}

	public DecoratedNotification() {
		super();
	}

	@Override
	public String toString() {
		return "DecoratedNotification [_id=" + _id + ", recipient=" + recipient
				+ ", rawNotification=" + rawNotification + "]";
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	
	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}

	public RawNotification getRawNotification() {
		return rawNotification;
	}

	public void setRawNotification(RawNotification rawNotification) {
		this.rawNotification = rawNotification;
	}
	
	
}
