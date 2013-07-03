package org.notificationengine.domain;

import org.bson.types.ObjectId;

public class DecoratedNotification {

	private ObjectId _id;
	
	private Recipient recipient;
	
	private RawNotification rawNotification;
	
	private Boolean sent;

	public DecoratedNotification(RawNotification rawNotification,
			Recipient recipient) {
		super();
		this.rawNotification = rawNotification;
		this.recipient = recipient;
		this.sent = Boolean.FALSE;
	}

	public DecoratedNotification() {
		super();
	}

	@Override
	public String toString() {
		return "DecoratedNotification [_id=" + _id + ", recipient=" + recipient
				+ ", rawNotification=" + rawNotification + ", sent=" + sent
				+ "]";
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

	public Boolean getSent() {
		return sent;
	}

	public void setSent(Boolean sent) {
		this.sent = sent;
	}
}
