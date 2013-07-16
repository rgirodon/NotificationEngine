package org.notificationengine.domain;

import static org.junit.Assert.*;

import java.util.Collection;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;

public class TestTopic {

	@Test
	public void testGetFathersWithSimpleTopic() {
		
		Topic topic = new Topic("facturation");
		
		Collection<Topic> fathers = topic.getFathers();
		
		assertEquals(1, fathers.size());
		
		assertTrue(fathers.contains(new Topic("facturation")));
	}

	@Test
	public void testGetFathersWithComplexTopic() {
		
		Topic topic = new Topic("facturation.client1.agence1");
		
		Collection<Topic> fathers = topic.getFathers();
		
		assertEquals(3, fathers.size());
		
		assertTrue(fathers.contains(new Topic("facturation")));
		
		assertTrue(fathers.contains(new Topic("facturation.client1")));
		
		assertTrue(fathers.contains(new Topic("facturation.client1.agence1")));
	}
	
	@Test
	public void testIsSonOf() {
		
		Topic topic = new Topic("facturation.client1.agence1");
		
		Topic topicFather = new Topic("facturation.client1");
		
		Topic topicGrandFather = new Topic("facturation");
		
		assertTrue(topic.isSonOfTopic(topic));
		
		assertTrue(topic.isSonOfTopic(topicFather));
		
		assertTrue(topic.isSonOfTopic(topicGrandFather));
		
		assertFalse(topicFather.isSonOfTopic(topic));		
	}
}
