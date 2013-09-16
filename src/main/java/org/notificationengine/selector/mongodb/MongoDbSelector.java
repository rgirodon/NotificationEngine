package org.notificationengine.selector.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.json.simple.JSONObject;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.MongoDbSettings;
import org.notificationengine.persistance.MongoDbUtils;
import org.notificationengine.selector.ISelectorWriteEnabled;
import org.notificationengine.selector.Selector;
import org.notificationengine.selector.SelectorWriteEnabled;
import org.notificationengine.spring.SpringUtils;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class MongoDbSelector extends SelectorWriteEnabled {

	private static Logger LOGGER = Logger.getLogger(MongoDbSelector.class);
	
	private Boolean modeTest;
	
	private MongoDbSettings mongoDbSettings;
	
	private MongoCollection subscriptions;
	
	public MongoDbSelector(Topic topic) {
		
		this(topic, Boolean.FALSE);
	}
	
	// take care, only to be called by test class
	public MongoDbSelector(Topic topic, Boolean modeTest, MongoDbSettings mongoDbSettings) {
		
		super(topic);
		
		this.modeTest = modeTest;
		
		this.mongoDbSettings = mongoDbSettings;
		
		this.init();
	}
	
	public MongoDbSelector(Topic topic, Boolean modeTest) {
		
		super(topic);
		
		this.modeTest = modeTest;
		
		this.mongoDbSettings = (MongoDbSettings)SpringUtils.getBean(Constants.MONGODB_SETTINGS);
		
		this.init();
	}
	
	private void init() {
		try {
			DB db = null;
			
			if (this.modeTest) {
				db = new MongoClient().getDB(Constants.DATABASE_TEST);
			}
			else {
				MongoClient mongoClient = null;
				
				if (!this.mongoDbSettings.getReplicaMode()) {
					
					String host = MongoDbUtils.getHostFromSingleServerUrl(this.mongoDbSettings.getUrl());
					
					int port = MongoDbUtils.getPortFromSingleServerUrl(this.mongoDbSettings.getUrl());
					
					ServerAddress addr = new ServerAddress(host, port);	
					
					mongoClient = new MongoClient(addr);
				}
				else {
					List<ServerAddress> addrs = MongoDbUtils.getServerAddressListFromMultipleServerUrl(this.mongoDbSettings.getUrl());
					
					mongoClient = new MongoClient(addrs);
				}
				
				db = mongoClient.getDB(this.mongoDbSettings.getDatabase());
			}
			
			Jongo jongo = new Jongo(db);
			
			this.subscriptions = jongo.getCollection(Constants.SUBSCRIPTIONS_COLLECTION);
		}
		catch (UnknownHostException e) {

			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			
			LOGGER.error("Unable to build MongoDbSelector");
		}
	}

    @Override
	public Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic) {
		
		Collection<Subscription> result = new ArrayList<>();
		
		for (Topic fatherTopic : topic.getFathers()) {
		
			LOGGER.debug("Searching Subscriptions for topic : " + fatherTopic);
			
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

    @Override
    public Collection<Subscription> retrieveSubscriptionsForRawNotification(RawNotification rawNotification) {

        Topic topic = rawNotification.getTopic();

        return this.retrieveSubscriptionsForTopic(topic);
    }

    @Override
    public Collection<Subscription> retrieveSubscriptions() {

        Collection<Subscription> result = new ArrayList<>();

        Iterable<Subscription> subscriptionsForExactQuery = this.subscriptions.find("{}").as(Subscription.class);

        for(Subscription subscription : subscriptionsForExactQuery) {

            LOGGER.debug("Found Subscription : " + subscription);

            result.add(subscription);
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

    public void deleteSubscription(String email, String topic) {

        JSONObject query = new JSONObject();

        query.put(Constants.TOPIC_NAME, topic);

        query.put(Constants.RECIPIENT_ADDRESS, email);

        Iterable<Subscription> subscriptionsForExactQuery = this.subscriptions.find(query.toString()).as(Subscription.class);

        for(Subscription subscription : subscriptionsForExactQuery) {

            LOGGER.info(subscription + " has been deleted");

            this.subscriptions.remove(subscription.get_id());

        }

    }
}
