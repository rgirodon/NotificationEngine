package org.notificationengine.selector.mongodb;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;

public class TestMongoDbSelector {

	private MongoDbSelector mongoDbSelector;
	
	@Before
	public void init() {
		
		mongoDbSelector = new MongoDbSelector(new Topic("facturation"), Boolean.TRUE, null);
		
		mongoDbSelector.cleanSubscriptions();
	}
	
	@Test
	public void testRetrieveSubscriptionsForTopic() {
		
		Subscription subscription1 = new Subscription(new Topic("facturation"), new Recipient("boss@societe.com", "Customer"));
		mongoDbSelector.createSubscription(subscription1);
		
		Subscription subscription2 = new Subscription(new Topic("facturation.client1"), new Recipient("accountmanager1@societe.com", "Customer"));
		mongoDbSelector.createSubscription(subscription2);
		
		Subscription subscription3 = new Subscription(new Topic("facturation.client1.agence1"), new Recipient("dptresp1@societe.com", "Customer"));
		mongoDbSelector.createSubscription(subscription3);
		
		Subscription subscription4 = new Subscription(new Topic("facturation.client2"), new Recipient("accountmanager2@societe.com", "Customer"));
		mongoDbSelector.createSubscription(subscription4);
		
		Collection<Subscription> subscriptions = mongoDbSelector.retrieveSubscriptionsForTopic(new Topic("facturation.client1"));
		
		assertEquals(2, subscriptions.size());
	}

    @Test
    public void testRetrieveAllSubscriptions() {

        Subscription subscription1 = new Subscription(new Topic("facturation"), new Recipient("boss@societe.com", "Customer"));
        mongoDbSelector.createSubscription(subscription1);

        Subscription subscription2 = new Subscription(new Topic("facturation.client1"), new Recipient("accountmanager1@societe.com", "Customer"));
        mongoDbSelector.createSubscription(subscription2);

        Subscription subscription3 = new Subscription(new Topic("facturation.client1.agence1"), new Recipient("dptresp1@societe.com", "Customer"));
        mongoDbSelector.createSubscription(subscription3);

        Subscription subscription4 = new Subscription(new Topic("facturation.client2"), new Recipient("accountmanager2@societe.com", "Customer"));
        mongoDbSelector.createSubscription(subscription4);

        Collection<Subscription> subscriptions = mongoDbSelector.retrieveSubscriptions();

        assertEquals(4, subscriptions.size());

    }

    @Test
    public void testDeleteSubscription() {

        Subscription subscription1 = new Subscription(new Topic("facturation"), new Recipient("boss@societe.com", "Customer"));
        mongoDbSelector.createSubscription(subscription1);

        Subscription subscription2 = new Subscription(new Topic("facturation.client1"), new Recipient("accountmanager1@societe.com", "Customer"));
        mongoDbSelector.createSubscription(subscription2);

        this.mongoDbSelector.deleteSubscription("boss@societe.com", "facturation.client1");

        this.mongoDbSelector.deleteSubscription("boss@societe.com", "facturation");

        Collection<Subscription> subscriptions = mongoDbSelector.retrieveSubscriptions();

        assertEquals(1, subscriptions.size());

    }
}
