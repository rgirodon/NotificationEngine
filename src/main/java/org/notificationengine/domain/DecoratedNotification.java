package org.notificationengine.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.notificationengine.constants.Constants;

public class DecoratedNotification {

	private ObjectId _id;
	
	private Recipient recipient;
	
	private RawNotification rawNotification;
	
	private Boolean sent;

    private Date createdAt;

    private Date sentAt;

    private Integer sendingAttempts;

	public Map<String, Object> getContext() {
		
		Map<String, Object> context = new HashMap<>();
		
		context.putAll(this.rawNotification.getContext());
		
		context.put(Constants.RECIPIENT, recipient.getAddress());
		
		return context;
	}
	
	public DecoratedNotification(RawNotification rawNotification,
			Recipient recipient) {
		super();
		this.rawNotification = rawNotification;
		this.recipient = recipient;
		this.sent = Boolean.FALSE;
        this.createdAt = new Date();
        this.sentAt = null;
        this.sendingAttempts = 0;
	}

    public DecoratedNotification() {
		super();
        this.createdAt = new Date();
        this.sendingAttempts = 0;
	}

	@Override
	public String toString() {
		return "DecoratedNotification ["+
                "_id=" + _id +
                ", recipient=" + recipient +
                ", rawNotification=" + rawNotification +
                ", sent=" + sent +
                ", createdAt=" + createdAt +
                ", sentAt=" + sentAt +
                ", sendingAttempts=" + sendingAttempts +
				"]";
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
    public Integer getSendingAttempts() {
        return sendingAttempts;
    }

    public void setSendingAttempts(Integer sendingAttempts) {
        this.sendingAttempts = sendingAttempts;
    }

	
}
