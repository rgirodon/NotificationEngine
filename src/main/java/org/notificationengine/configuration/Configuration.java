package org.notificationengine.configuration;

import java.util.ArrayList;
import java.util.Collection;

import org.notificationengine.domain.Channel;

public class Configuration {

	private Collection<Channel> channels;

	public Configuration() {
		super();
		
		this.channels = new ArrayList<>();
	}

	public void addChannel(Channel channel) {
		
		this.channels.add(channel);
	}
	
	public Collection<Channel> getChannels() {
		return channels;
	}

	public void setChannels(Collection<Channel> channels) {
		this.channels = channels;
	}
	
	
}
