package org.notificationengine.domain;

import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class PhysicalNotification {

    private Recipient recipient;

    private Date sentAt;

    private String subject;

    private String notificationContent;

    private Collection<ObjectId> filesAttachedIds;

    public PhysicalNotification() {

        this.sentAt = new Date();
        this.filesAttachedIds = new HashSet<>();
    }

    public PhysicalNotification(Recipient recipient, String subject, String notificationContent, Collection<ObjectId> filesAttachedIds) {
        this.recipient = recipient;
        this.sentAt = new Date();
        this.subject = subject;
        this.notificationContent = notificationContent;
        this.filesAttachedIds = filesAttachedIds;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public Collection<ObjectId> getFilesAttachedIds() {
        return filesAttachedIds;
    }

    public void setFilesAttachedIds(Collection<ObjectId> filesAttachedIds) {
        this.filesAttachedIds = filesAttachedIds;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PhysicalNotification{");
        sb.append("recipient=").append(recipient);
        sb.append(", sentAt=").append(sentAt);
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", notificationContent='").append(notificationContent).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
