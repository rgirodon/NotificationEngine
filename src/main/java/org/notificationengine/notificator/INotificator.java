package org.notificationengine.notificator;

public interface INotificator {

	public void process();

    public void setUrgentEnabled(Boolean urgentEnabled);
}
