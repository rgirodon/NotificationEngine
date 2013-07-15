package org.notificationengine.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.notificationengine.constants.Constants;

public class Topic {

	private String name;

	public Topic(String name) {
		super();
		this.name = name;
	}

	public Topic() {
		super();
	}
	
	public Collection<Topic> getFathers() {
		
		Collection<Topic> result = new ArrayList<>();
		
		StringBuilder topicStringBuilder = new StringBuilder();
		
		String[] topicParts = StringUtils.split(this.name, Constants.DOT);
		
		for (int i = 0; i < topicParts.length; i++) {
			
			String topicPart = topicParts[i];
			
			if (i > 0) {
				topicStringBuilder.append(Constants.DOT);
			}
			
			topicStringBuilder.append(topicPart);
			
			Topic fatherTopic = new Topic(topicStringBuilder.toString());
			
			result.add(fatherTopic);
		}
		
		return result;
	}
	
	public boolean isSonOfTopic(Topic topic) {
		
		Collection<Topic> fathers = this.getFathers();
		
		return fathers.contains(topic);
	}

	@Override
	public String toString() {
		return "Topic [name=" + name + "]";
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Topic other = (Topic) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
