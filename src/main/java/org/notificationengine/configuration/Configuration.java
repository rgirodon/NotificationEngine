package org.notificationengine.configuration;

import java.util.ArrayList;
import java.util.Collection;

import org.notificationengine.domain.Channel;
import org.notificationengine.domain.Topic;

public class Configuration {

	private Collection<Channel> channels;

    private String authenticationType;

    private String customAuthenticatorClass;

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

	public boolean hasChannelWithId(String id) {
		
		return (this.findChannelById(id) != null);
	}
	
	public Channel findChannelById(String id) {
		
		Channel result = null;
		
		for (Channel channel : channels) {
			
			if (channel.getId().equals(id)) {
				
				result = channel;
				
				break;
			}
		}
		
		return result;
	}

	public boolean hasChannelWithTopic(String topic) {

		return (this.findChannelByTopic(topic) != null);
	}
	
	public Channel findChannelByTopic(String topic) {
		
		Channel result = null;
		
		for (Channel channel : channels) {
			
			if (channel.getTopic().equals(new Topic(topic))) {
				
				result = channel;
				
				break;
			}
		}
		
		return result;
	}

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getCustomAuthenticatorClass() {
        return customAuthenticatorClass;
    }

    public void setCustomAuthenticatorClass(String customAuthenticatorClass) {
        this.customAuthenticatorClass = customAuthenticatorClass;
    }
}
