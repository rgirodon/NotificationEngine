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

    private Date deletedAt;

	public Map<String, Object> getContext() {
		
		Map<String, Object> context = new HashMap<>();
		
		context.putAll(this.rawNotification.getContext());
		
		context.put(Constants.RECIPIENT, recipient.getAddress());

		context.put(Constants.DISPLAY_NAME, recipient.getDisplayName());

		return context;
	}
	
	public DecoratedNotification(RawNotification rawNotification,
			Recipient recipient) {
		super();
		this.rawNotification = rawNotification;
		this.recipient = recipient;
		this.sent = Boolean.FALSE;
        this.createdAt = new Date();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result
				+ ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result
				+ ((rawNotification == null) ? 0 : rawNotification.hashCode());
		result = prime * result
				+ ((recipient == null) ? 0 : recipient.hashCode());
		result = prime * result
				+ ((sendingAttempts == null) ? 0 : sendingAttempts.hashCode());
		result = prime * result + ((sent == null) ? 0 : sent.hashCode());
		result = prime * result + ((sentAt == null) ? 0 : sentAt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DecoratedNotification other = (DecoratedNotification) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (rawNotification == null) {
			if (other.rawNotification != null)
				return false;
		} else if (!rawNotification.equals(other.rawNotification))
			return false;
		if (recipient == null) {
			if (other.recipient != null)
				return false;
		} else if (!recipient.equals(other.recipient))
			return false;
		if (sendingAttempts == null) {
			if (other.sendingAttempts != null)
				return false;
		} else if (!sendingAttempts.equals(other.sendingAttempts))
			return false;
		if (sent == null) {
			if (other.sent != null)
				return false;
		} else if (!sent.equals(other.sent))
			return false;
		if (sentAt == null) {
			if (other.sentAt != null)
				return false;
		} else if (!sentAt.equals(other.sentAt))
			return false;
		return true;
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

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}
