package org.notificationengine.integration;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.MongoDbSettings;
import org.notificationengine.persistance.Persister;
import org.notificationengine.selector.mongodb.MongoDbSelector;

public class IntegrationTestData {

	private Persister persister;
	
	private MongoDbSelector mongoDbSelector;
	
	@Before
	public void init() {
		
		MongoDbSettings mongoDbSettings = new MongoDbSettings(Boolean.FALSE, Constants.DEFAULT_MONGODB_URL, Constants.DEFAULT_MONGODB_DATABASE);
		
		persister = new Persister(Boolean.FALSE, mongoDbSettings);
		
		mongoDbSelector = new MongoDbSelector(new Topic("facturation"), Boolean.FALSE, mongoDbSettings);
		
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
		context1.put("message", "Wir danken Ihnen, die Rechnung so bald wie möglich zu zahlen");
		context1.put("subject", "Eine Notification Service Nachricht für Sie");
		context1.put("from", "rgirodon2000@yahoo.fr");
		rawNotification1.setContext(context1);
		
		persister.createRawNotification(rawNotification1);
		
		
		RawNotification rawNotification2 = new RawNotification();
		rawNotification2.set_id(new ObjectId());
		rawNotification2.setProcessed(Boolean.FALSE);
		rawNotification2.setTopic(new Topic("facturation.societe2"));
		
		Map<String, Object> context2 = new HashMap<>();
		context2.put("message", "Hey chico, si no pagas tendras problemas.");
		context2.put("subject", "Un mensaje del servicio de las notificaciones.");
		context2.put("from", "rgirodon2000@yahoo.fr");
		rawNotification2.setContext(context2);
		
		persister.createRawNotification(rawNotification2);
		
		
		RawNotification rawNotification3 = new RawNotification();
		rawNotification3.set_id(new ObjectId());
		rawNotification3.setProcessed(Boolean.FALSE);
		rawNotification3.setTopic(new Topic("helpdesk.societe1"));
		
		Map<String, Object> context3 = new HashMap<>();
		context3.put("message", "Limpia las vitras");
		context3.put("subject", "Un mensaje del servicio de las notificaciones.");
		context3.put("from", "rgirodon2000@yahoo.fr");
		rawNotification3.setContext(context3);
		
		persister.createRawNotification(rawNotification3);
		
		
		RawNotification rawNotification4 = new RawNotification();
		rawNotification4.set_id(new ObjectId());
		rawNotification4.setProcessed(Boolean.FALSE);
		rawNotification4.setTopic(new Topic("helpdesk.societe2"));
		
		Map<String, Object> context4 = new HashMap<>();
		context4.put("message", "Limpia el coche");
		context4.put("subject", "Un mensaje del servicio de las notificaciones.");
		context4.put("from", "rgirodon2000@yahoo.fr");
		rawNotification4.setContext(context4);
		
		persister.createRawNotification(rawNotification4);

		
		Subscription subscription1 = new Subscription(new Topic("facturation.societe1"), new Recipient("nmoret@sqli.com", "Nicolas Moret"));

		Subscription subscription2 = new Subscription(new Topic("facturation.societe2"), new Recipient("mduclos@sqli.com", "Matthis Duclos"));
		
		Subscription subscription3 = new Subscription(new Topic("facturation"), new Recipient("rgirodon@sqli.com", "Remy Girodon"));
		
		
		Subscription subscription4 = new Subscription(new Topic("helpdesk.societe1"), new Recipient("nmoret", "Nicolas Moret"));
		
		Subscription subscription5 = new Subscription(new Topic("helpdesk.societe2"), new Recipient("mduclos@sqli.com", "Matthis Duclos"));
		
		Subscription subscription6 = new Subscription(new Topic("helpdesk"), new Recipient("rgirodon@sqli.com", "Remy Girodon"));
		
		
		mongoDbSelector.createSubscription(subscription1);
		mongoDbSelector.createSubscription(subscription2);
		mongoDbSelector.createSubscription(subscription3);
		mongoDbSelector.createSubscription(subscription4);
		mongoDbSelector.createSubscription(subscription5);
		mongoDbSelector.createSubscription(subscription6);
		
	}

}
