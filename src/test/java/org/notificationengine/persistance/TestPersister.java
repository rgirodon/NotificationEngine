package org.notificationengine.persistance;

import static org.junit.Assert.*;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Topic;

public class TestPersister {

	private Persister persister;
	
	@Before
	public void init() {
		
		persister = new Persister(Boolean.TRUE);
		
		persister.cleanRawNotifications();
		
		persister.cleanDecoratedNotifications();
	}
	
	@Test
	public void testCreateDecoratedNotification() {
		
		RawNotification rawNotification = new RawNotification();
		rawNotification.set_id(new ObjectId());
		rawNotification.setProcessed(Boolean.FALSE);
		rawNotification.setTopic(new Topic("facturation.societe1"));
		
		Recipient recipient = new Recipient("email1@societe1.com");
		
		DecoratedNotification decoratedNotification = new DecoratedNotification(rawNotification, recipient);
		
		persister.createDecoratedNotification(decoratedNotification);
		
		assertEquals(1, persister.getDecoratedNotifications().count());
	}
	
	@Test
	public void testCreateRawNotification() {
		
		RawNotification rawNotification1 = new RawNotification();
		rawNotification1.set_id(new ObjectId());
		rawNotification1.setProcessed(Boolean.FALSE);
		rawNotification1.setTopic(new Topic("facturation.societe1"));
		
		persister.createRawNotification(rawNotification1);
		
		
		RawNotification rawNotification2 = new RawNotification();
		rawNotification2.set_id(new ObjectId());
		rawNotification2.setProcessed(Boolean.FALSE);
		rawNotification2.setTopic(new Topic("facturation.societe2"));
		
		persister.createRawNotification(rawNotification2);
		
		
		assertEquals(2, persister.getRawNotifications().count());
	}

	@Test
	public void testRetrieveNotProcessedRawNotificationsForTopic() {
		
		RawNotification rawNotification1 = new RawNotification();
		rawNotification1.set_id(new ObjectId());
		rawNotification1.setProcessed(Boolean.FALSE);
		rawNotification1.setTopic(new Topic("facturation.societe1"));
		
		persister.createRawNotification(rawNotification1);
		
		
		RawNotification rawNotification2 = new RawNotification();
		rawNotification2.set_id(new ObjectId());
		rawNotification2.setProcessed(Boolean.FALSE);
		rawNotification2.setTopic(new Topic("facturation.societe2"));
		
		persister.createRawNotification(rawNotification2);
		
		
		RawNotification rawNotification3 = new RawNotification();
		rawNotification3.set_id(new ObjectId());
		rawNotification3.setProcessed(Boolean.TRUE);
		rawNotification3.setTopic(new Topic("facturation.societe1"));
		
		persister.createRawNotification(rawNotification3);
		
		
		RawNotification rawNotification4 = new RawNotification();
		rawNotification4.set_id(new ObjectId());
		rawNotification4.setProcessed(Boolean.FALSE);
		rawNotification4.setTopic(new Topic("facturation"));
		
		persister.createRawNotification(rawNotification4);
		
		

		RawNotification rawNotification5 = new RawNotification();
		rawNotification5.set_id(new ObjectId());
		rawNotification5.setProcessed(Boolean.FALSE);
		rawNotification5.setTopic(new Topic("facturationDifferente"));
		
		persister.createRawNotification(rawNotification5);
		
		
		Collection<RawNotification> rawNotifications = persister.retrieveNotProcessedRawNotificationsForTopic(new Topic("facturation"));
		
		assertEquals(3, rawNotifications.size());
	}
	
	@Test
	public void testRetrieveRawNotificationById() {
		
		RawNotification rawNotification = new RawNotification();
		rawNotification.set_id(new ObjectId());
		rawNotification.setProcessed(Boolean.FALSE);
		rawNotification.setTopic(new Topic("facturation.societe1"));
		
		persister.createRawNotification(rawNotification);
		
		rawNotification = persister.retrieveRawNotificationById(rawNotification.get_id());
		
		assertEquals("facturation.societe1", rawNotification.getTopic().getName());
	}

	@Test
	public void testMarkRawNotificationAsProcessed() {
		
		RawNotification rawNotification = new RawNotification();
		rawNotification.set_id(new ObjectId());
		rawNotification.setProcessed(Boolean.FALSE);
		rawNotification.setTopic(new Topic("facturation.societe1"));
		
		persister.createRawNotification(rawNotification);
		
		persister.markRawNotificationAsProcessed(rawNotification);
		
		rawNotification = persister.retrieveRawNotificationById(rawNotification.get_id());
		
		assertTrue(rawNotification.getProcessed());
	}

}
