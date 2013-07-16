package org.notificationengine.configuration;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.notificationengine.domain.Channel;
import org.notificationengine.domain.Topic;

public class TestConfiguration {

	private Configuration configuration;
	
	@Before
	public void init() {
		
		this.configuration = new Configuration();
		
		Channel channel1 = new Channel("id1");
		channel1.setTopic(new Topic("topic1"));
		configuration.addChannel(channel1);
		
		Channel channel2 = new Channel("id2");
		channel2.setTopic(new Topic("topic2"));
		configuration.addChannel(channel2);
	}
	
	@Test
	public void testHasChannelWithId() {
		
		assertTrue(configuration.hasChannelWithId("id1"));
		
		assertFalse(configuration.hasChannelWithId("id3"));
	}

	@Test
	public void testFindChannelById() {
		
		Channel channel = configuration.findChannelById("id1");
		assertEquals("id1", channel.getId());
		
		channel = configuration.findChannelById("id3");
		assertNull(channel);
	}

	@Test
	public void testHasChannelWithTopic() {
		
		assertTrue(configuration.hasChannelWithTopic("topic1"));
		
		assertFalse(configuration.hasChannelWithTopic("topic3"));
	}

	@Test
	public void testFindChannelByTopic() {
		
		Channel channel = configuration.findChannelByTopic("topic1");
		assertEquals("id1", channel.getId());
		
		channel = configuration.findChannelByTopic("topic3");
		assertNull(channel);
	}
}
