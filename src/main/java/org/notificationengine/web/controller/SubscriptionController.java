package org.notificationengine.web.controller;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.dto.SubscriptionDTO;
import org.notificationengine.selector.mongodb.MongoDbSelector;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller(value=Constants.SUBSCRIPTION_CONTROLLER)
public class SubscriptionController {

	private static Logger LOGGER = Logger.getLogger(SubscriptionController.class);
	
	private MongoDbSelector mongoDbSelector;
	
	public SubscriptionController() {
		
		LOGGER.debug("SubscriptionController instantiated and listening");
	}
	
	public void activate(MongoDbSelector mongoDbSelector) {
		
		this.setMongoDbSelector(mongoDbSelector);
	}
	
	public Boolean isActivated() {
		
		return (this.mongoDbSelector != null);
	}
    
	@RequestMapping(value = "/subscription.do", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestBody SubscriptionDTO subscriptionDTO) {
		
		LOGGER.debug("SubscriptionController received : " + subscriptionDTO);
		
		if (this.mongoDbSelector != null) {
			
			Subscription subscription = new Subscription();
			subscription.set_id(new ObjectId());
			subscription.setTopic(new Topic(subscriptionDTO.getTopic()));
			subscription.setRecipient(new Recipient(subscriptionDTO.getRecipient()));
			
			this.mongoDbSelector.createSubscription(subscription);
			
			LOGGER.debug("Subscription persisted : " + subscriptionDTO);
		}
    }

    @RequestMapping(value = "/countAllSubscriptions.do", method = RequestMethod.GET)
    @ResponseBody
    public Integer countAllSubscription() {

        Integer result = new Integer(0);

        LOGGER.debug("Subscription controller, countAllSubscription");

        Collection<Subscription> subscriptions = this.mongoDbSelector.retrieveSubscriptions();

        result = subscriptions.size();

        return result;

    }

    @RequestMapping(value = "/countAllSubscriptionsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public Integer countAllSubscriptionForTopic(@RequestParam(value="topic") String topicName) {

        Integer result = new Integer(0);

        Topic topic = new Topic(topicName);

        LOGGER.debug("Subscription controller, countAllSubscription");

        Collection<Subscription> subscriptions = this.mongoDbSelector.retrieveSubscriptionsForTopic(topic);

        result = subscriptions.size();

        return result;

    }

	private void setMongoDbSelector(MongoDbSelector mongoDbSelector) {
		this.mongoDbSelector = mongoDbSelector;
	}
}