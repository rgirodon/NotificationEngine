package org.notificationengine.selector.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.json.simple.JSONObject;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;
import org.notificationengine.selector.Selector;
import org.notificationengine.spring.SpringUtils;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDbSelector extends Selector {

	final public static String DATABASE = "notificationengine";
	
	final public static String DATABASE_TEST = "notificationengine_test";
	
	final public static String SUBSCRIPTIONS_COLLECTION = "subscriptions";
	
	private static Logger LOGGER = Logger.getLogger(MongoDbSelector.class);
	
	private MongoCollection subscriptions;
	
	public MongoDbSelector(Topic topic) {
		
		this(topic, Boolean.FALSE);
	}
	
	public MongoDbSelector(Topic topic, Boolean modeTest) {
		
		super(topic);
		
		try {
			// TODO set mongodb params configurable
			
			DB db = null;
			
			if (modeTest) {
				db = new MongoClient().getDB(DATABASE_TEST);
			}
			else {
				db = new MongoClient().getDB(DATABASE);
			}
			
			Jongo jongo = new Jongo(db);
			
			this.subscriptions = jongo.getCollection(SUBSCRIPTIONS_COLLECTION);
		}
		catch (UnknownHostException e) {

			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			
			LOGGER.error("Unable to build MongoDbSelector");
		}
	}

	@Override
	protected Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic) {
		
		Collection<Subscription> result = new ArrayList<>();
		
		for (Topic fatherTopic : topic.getFathers()) {
		
			LOGGER.debug("Searching Subscription for topic : " + fatherTopic);
			
			JSONObject exactQueryJsonObject = new JSONObject();
			exactQueryJsonObject.put(Constants.TOPIC_NAME, fatherTopic.getName());
			
			String exactQuery = exactQueryJsonObject.toString();
			
			Iterable<Subscription> subscriptionsForExactQuery = this.subscriptions.find(exactQuery).as(Subscription.class);
			
			for(Subscription subscription : subscriptionsForExactQuery) {
				
				LOGGER.debug("Found Subscription : " + subscription);
				
				result.add(subscription);
			}
		}
		
		LOGGER.debug("Nbr of subscriptions retrieved : " + result.size());
		
		return result;
	}

	public void cleanSubscriptions() {
		
		this.subscriptions.remove();
	}

	public void createSubscription(Subscription subscription) {
		
		this.subscriptions.save(subscription);
		
		LOGGER.debug("Inserted Subscription : " + subscription);
	}

}
