package org.notificationengine.persistance;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.json.simple.JSONObject;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Topic;
import org.springframework.stereotype.Component;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

@Component(value=Constants.PERSISTER)
public class Persister {

	private static Logger LOGGER = Logger.getLogger(Persister.class);
	
	private MongoCollection rawNotifications;
	
	private MongoCollection decoratedNotifications;
	
	public Persister() {
		
		this(Boolean.FALSE);
	}
	
	public Persister(Boolean modeTest) {
		
		try {
			DB db = null;
			
			if (modeTest) {
				db = new MongoClient().getDB(Constants.DATABASE_TEST);
			}
			else {
				db = new MongoClient().getDB(Constants.DATABASE);
			}
			
			Jongo jongo = new Jongo(db);
			
			this.rawNotifications = jongo.getCollection(Constants.RAW_NOTIFICATIONS_COLLECTION);
			
			this.decoratedNotifications = jongo.getCollection(Constants.DECORATED_NOTIFICATIONS_COLLECTION);
		} 
		catch (UnknownHostException e) {

			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			
			LOGGER.error("Unable to build Persister");
		}
	}

	public MongoCollection getRawNotifications() {
		return rawNotifications;
	}

	public void setRawNotifications(MongoCollection rawNotifications) {
		this.rawNotifications = rawNotifications;
	}
	
	public MongoCollection getDecoratedNotifications() {
		return decoratedNotifications;
	}

	public void setDecoratedNotifications(MongoCollection decoratedNotifications) {
		this.decoratedNotifications = decoratedNotifications;
	}

	public void createDecoratedNotification(
			DecoratedNotification decoratedNotification) {
		
		this.decoratedNotifications.save(decoratedNotification);

		LOGGER.debug("Inserted DecoratedNotification : " + decoratedNotification);
	}
	
	public void createRawNotification(
			RawNotification rawNotification) {
		
		this.rawNotifications.save(rawNotification);
		
		LOGGER.debug("Inserted RawNotification : " + rawNotification);
	}

	public Collection<RawNotification> retrieveNotProcessedRawNotificationsForTopic(Topic topic) {
		
		LOGGER.debug("Retrieve not processed RawNotifications for Topic : " + topic);
		
		Collection<RawNotification> result = new ArrayList<>();
		
		JSONObject exactQueryJsonObject = new JSONObject();		
		exactQueryJsonObject.put(Constants.PROCESSED, Boolean.FALSE);		
		exactQueryJsonObject.put(Constants.TOPIC_NAME, topic.getName());
		
		String exactQuery = exactQueryJsonObject.toString();
		
		Iterable<RawNotification> rawNotificationsForExactQuery = this.rawNotifications.find(exactQuery).as(RawNotification.class);
		
		for(RawNotification rawNotification : rawNotificationsForExactQuery) {
			
			LOGGER.debug("Found RawNotification (exact query) : " + rawNotification);
			
			result.add(rawNotification);
		}
		
		
		JSONObject likeQueryJsonObject = new JSONObject();		
		likeQueryJsonObject.put(Constants.PROCESSED, Boolean.FALSE);
		
		JSONObject regularExpressionJsonObject = new JSONObject();
		regularExpressionJsonObject.put(Constants.REGEX, topic.getName() + "\\..*");
		
		likeQueryJsonObject.put(Constants.TOPIC_NAME, regularExpressionJsonObject);
		
		String likeQuery = likeQueryJsonObject.toString();
		
		Iterable<RawNotification> rawNotificationsForLikeQuery = this.rawNotifications.find(likeQuery).as(RawNotification.class);
		
		for(RawNotification rawNotification : rawNotificationsForLikeQuery) {
			
			LOGGER.debug("Found RawNotification (like query) : " + rawNotification);
			
			result.add(rawNotification);
		}
		
		return result;
	}

	public void markRawNotificationAsProcessed(RawNotification rawNotification) {
		
		rawNotification.setProcessed(Boolean.TRUE);
		
		this.rawNotifications.save(rawNotification);
	}

	public void cleanRawNotifications() {
		
		this.rawNotifications.remove();
	}
	
	public void cleanDecoratedNotifications() {
		
		this.decoratedNotifications.remove();
	}

	public RawNotification retrieveRawNotificationById(ObjectId id) {

		return this.rawNotifications.findOne(id).as(RawNotification.class);
	}

	public Collection<DecoratedNotification> retrieveNotSentDecoratedNotificationsForTopic(
			Topic topic) {
		
		LOGGER.debug("Retrieve not sent DecoratedNotifications for Topic : " + topic);
		
		Collection<DecoratedNotification> result = new ArrayList<>();
		
		JSONObject exactQueryJsonObject = new JSONObject();		
		exactQueryJsonObject.put(Constants.SENT, Boolean.FALSE);		
		exactQueryJsonObject.put(Constants.RAW_NOTIFICATION_TOPIC_NAME, topic.getName());
		
		String exactQuery = exactQueryJsonObject.toString();
		
		Iterable<DecoratedNotification> decoratedNotificationsForExactQuery = this.decoratedNotifications.find(exactQuery).as(DecoratedNotification.class);
		
		for(DecoratedNotification decoratedNotification : decoratedNotificationsForExactQuery) {
			
			LOGGER.debug("Found DecoratedNotification (exact query) : " + decoratedNotification);
			
			result.add(decoratedNotification);
		}
		
		
		JSONObject likeQueryJsonObject = new JSONObject();		
		likeQueryJsonObject.put(Constants.SENT, Boolean.FALSE);
		
		JSONObject regularExpressionJsonObject = new JSONObject();
		regularExpressionJsonObject.put(Constants.REGEX, topic.getName() + "\\..*");
		
		likeQueryJsonObject.put(Constants.RAW_NOTIFICATION_TOPIC_NAME, regularExpressionJsonObject);
		
		String likeQuery = likeQueryJsonObject.toString();
		
		Iterable<DecoratedNotification> decoratedNotificationsForLikeQuery = this.decoratedNotifications.find(likeQuery).as(DecoratedNotification.class);
		
		for(DecoratedNotification decoratedNotification : decoratedNotificationsForLikeQuery) {
			
			LOGGER.debug("Found DecoratedNotification (like query) : " + decoratedNotification);
			
			result.add(decoratedNotification);
		}
		
		return result;
	}

	public void markDecoratedNotificationAsSent(
			DecoratedNotification decoratedNotification) {
		
		decoratedNotification.setSent(Boolean.TRUE);
		
		this.decoratedNotifications.save(decoratedNotification);
	}

	public DecoratedNotification retrieveDecoratedNotificationById(
			ObjectId id) {
		
		return this.decoratedNotifications.findOne(id).as(DecoratedNotification.class);
	}
}
