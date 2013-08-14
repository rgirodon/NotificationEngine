package org.notificationengine.notificator.mail;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Topic;

public class TestSingleMultiTopicMailByRecipientNotificator {

	private SingleMultiTopicMailByRecipientNotificator singleMultiTopicMailByRecipientNotificator;
	
	@Before
	public void init() {
		
		this.singleMultiTopicMailByRecipientNotificator = new SingleMultiTopicMailByRecipientNotificator();
	}
	
	@Test
	public void testRetrieveRecipientsForTopics() {
		
		this.singleMultiTopicMailByRecipientNotificator.add("sportMailTemplate", Boolean.FALSE, new Topic("football"));
		this.singleMultiTopicMailByRecipientNotificator.add("sportMailTemplate", Boolean.FALSE,new Topic("cycling"));
		
		this.singleMultiTopicMailByRecipientNotificator.add("newsMailTemplate", Boolean.FALSE,new Topic("politics"));
		this.singleMultiTopicMailByRecipientNotificator.add("newsMailTemplate", Boolean.FALSE,new Topic("economics"));
		
		Collection<DecoratedNotification> decoratedNotifications = new ArrayList<>();

		RawNotification rawNotification1 = new RawNotification();
		rawNotification1.setTopic(new Topic("football"));		
		Recipient recipient1 = new Recipient("rgirodon@sqli.com", "Remy Girodon");
		DecoratedNotification decoratedNotification1 = new DecoratedNotification(rawNotification1, recipient1);
		decoratedNotifications.add(decoratedNotification1);
		
		RawNotification rawNotification2 = new RawNotification();
		rawNotification2.setTopic(new Topic("cycling"));		
		Recipient recipient2 = new Recipient("rgirodon@sqli.com", "Remy Girodon");
		DecoratedNotification decoratedNotification2 = new DecoratedNotification(rawNotification2, recipient2);
		decoratedNotifications.add(decoratedNotification2);
		
		RawNotification rawNotification3 = new RawNotification();
		rawNotification3.setTopic(new Topic("football.asse"));		
		Recipient recipient3 = new Recipient("mduclos@sqli.com", "Matthis Duclos");
		DecoratedNotification decoratedNotification3 = new DecoratedNotification(rawNotification3, recipient3);
		decoratedNotifications.add(decoratedNotification3);
		
		RawNotification rawNotification4 = new RawNotification();
		rawNotification4.setTopic(new Topic("politics"));		
		Recipient recipient4 = new Recipient("nmoret@sqli.com", "Nicolas Moret");
		DecoratedNotification decoratedNotification4 = new DecoratedNotification(rawNotification4, recipient4);
		decoratedNotifications.add(decoratedNotification4);
				
		this.singleMultiTopicMailByRecipientNotificator.addDecoratedNotificationsToProcess(decoratedNotifications);
		
		Collection<Topic> topics = new ArrayList<>();
		topics.add(new Topic("football"));
		
		assertEquals(2, this.singleMultiTopicMailByRecipientNotificator.retrieveRecipientsForTopics(topics).size());
	}

	@Test
	public void testRetrieveDecoratedNotificationsToProcessForTopicAndRecipient() {
		
		Collection<DecoratedNotification> decoratedNotifications = new ArrayList<>();

		RawNotification rawNotification1 = new RawNotification();
		rawNotification1.setTopic(new Topic("football"));		
		Recipient recipient1 = new Recipient("rgirodon@sqli.com", "Remy Girodon");
		DecoratedNotification decoratedNotification1 = new DecoratedNotification(rawNotification1, recipient1);
		decoratedNotifications.add(decoratedNotification1);
		
		RawNotification rawNotification2 = new RawNotification();
		rawNotification2.setTopic(new Topic("cycling"));		
		Recipient recipient2 = new Recipient("rgirodon@sqli.com", "Remy Girodon");
		DecoratedNotification decoratedNotification2 = new DecoratedNotification(rawNotification2, recipient2);
		decoratedNotifications.add(decoratedNotification2);
		
		RawNotification rawNotification3 = new RawNotification();
		rawNotification3.setTopic(new Topic("football.asse"));		
		Recipient recipient3 = new Recipient("mduclos@sqli.com", "Matthis Duclos");
		DecoratedNotification decoratedNotification3 = new DecoratedNotification(rawNotification3, recipient3);
		decoratedNotifications.add(decoratedNotification3);
		
		RawNotification rawNotification4 = new RawNotification();
		rawNotification4.setTopic(new Topic("politics"));		
		Recipient recipient4 = new Recipient("nmoret@sqli.com", "Nicolas Moret");
		DecoratedNotification decoratedNotification4 = new DecoratedNotification(rawNotification4, recipient4);
		decoratedNotifications.add(decoratedNotification4);
				
		this.singleMultiTopicMailByRecipientNotificator.addDecoratedNotificationsToProcess(decoratedNotifications);
		
		assertEquals(1, this.singleMultiTopicMailByRecipientNotificator.retrieveDecoratedNotificationsToProcessForTopicAndRecipient(new Topic("football"), new Recipient("rgirodon@sqli.com", "Remy Girodon")).size());
		
		assertEquals(1, this.singleMultiTopicMailByRecipientNotificator.retrieveDecoratedNotificationsToProcessForTopicAndRecipient(new Topic("cycling"), new Recipient("rgirodon@sqli.com", "Remy Girodon")).size());
		
		assertEquals(0, this.singleMultiTopicMailByRecipientNotificator.retrieveDecoratedNotificationsToProcessForTopicAndRecipient(new Topic("economics"), new Recipient("rgirodon@sqli.com", "Remy Girodon")).size());
	}

}
