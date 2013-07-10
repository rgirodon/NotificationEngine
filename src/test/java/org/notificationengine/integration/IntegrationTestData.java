package org.notificationengine.integration;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;
import org.notificationengine.selector.mongodb.MongoDbSelector;

public class IntegrationTestData {

	private Persister persister;
	
	private MongoDbSelector mongoDbSelector;
	
	@Before
	public void init() {
		
		persister = new Persister();
		
		mongoDbSelector = new MongoDbSelector(new Topic("facturation"));
		
		persister.cleanRawNotifications();
		
		persister.cleanDecoratedNotifications();
		
		mongoDbSelector.cleanSubscriptions();
	}
	
	@Test
	public void loadData() {
		
		RawNotification rawNotification1 = new RawNotification();
		rawNotification1.set_id(new ObjectId());
		rawNotification1.setProcessed(Boolean.FALSE);
		rawNotification1.setTopic(new Topic("facturation.societe1"));
		
		Map<String, Object> context1 = new HashMap<>();
		context1.put("message", "You must pay now, please.");
		rawNotification1.setContext(context1);
		
		persister.createRawNotification(rawNotification1);
		
		
		RawNotification rawNotification2 = new RawNotification();
		rawNotification2.set_id(new ObjectId());
		rawNotification2.setProcessed(Boolean.FALSE);
		rawNotification2.setTopic(new Topic("facturation.societe2"));
		
		Map<String, Object> context2 = new HashMap<>();
		context2.put("message", "You really must pay now, please.");
		rawNotification2.setContext(context2);
		
		persister.createRawNotification(rawNotification2);
		
		
		Subscription subscription1 = new Subscription(new Topic("facturation.societe1"), new Recipient("gestionnaire1"));
		
		Subscription subscription2 = new Subscription(new Topic("facturation.societe2"), new Recipient("gestionnaire2"));
		
		Subscription subscription = new Subscription(new Topic("facturation"), new Recipient("gestionnaire"));
		
		mongoDbSelector.createSubscription(subscription1);
		mongoDbSelector.createSubscription(subscription2);
		mongoDbSelector.createSubscription(subscription);		
	}

}
