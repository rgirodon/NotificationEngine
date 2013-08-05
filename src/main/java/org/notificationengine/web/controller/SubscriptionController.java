package org.notificationengine.web.controller;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.notificationengine.dto.SubscriptionDTO;
import org.notificationengine.selector.ISelector;
import org.notificationengine.selector.ISelectorWriteEnabled;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller(value=Constants.SUBSCRIPTION_CONTROLLER)
public class SubscriptionController {

	private static Logger LOGGER = Logger.getLogger(SubscriptionController.class);
	
	private ISelector selector;
	
	public SubscriptionController() {
		
		LOGGER.debug("SubscriptionController instantiated and listening");
	}

	@RequestMapping(value = "/subscription.do", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestBody SubscriptionDTO subscriptionDTO) {
		
		LOGGER.debug("SubscriptionController received : " + subscriptionDTO);
		
		if (this.selector instanceof ISelectorWriteEnabled) {
			
			Subscription subscription = new Subscription();
			subscription.set_id(new ObjectId());
			subscription.setTopic(new Topic(subscriptionDTO.getTopic()));
			subscription.setRecipient(new Recipient(subscriptionDTO.getRecipient()));
            subscription.setDisplayName(new String(subscriptionDTO.getDisplayName()));
			
			((ISelectorWriteEnabled)this.selector).createSubscription(subscription);
			
			LOGGER.debug("Subscription persisted : " + subscriptionDTO);
		}
    }

    @RequestMapping(value = "/countAllSubscriptions.do", method = RequestMethod.GET)
    @ResponseBody
    public Integer countAllSubscriptions() {

        Integer result = new Integer(0);

        LOGGER.debug("Subscription controller, countAllSubscriptions");

        Collection<Subscription> subscriptions = this.selector.retrieveSubscriptions();

        result = subscriptions.size();

        return result;
    }

    @RequestMapping(value = "/countAllSubscriptionsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public Integer countAllSubscriptionsForTopic(@RequestParam(value="topic") String topicName) {

        Integer result = new Integer(0);

        Topic topic = new Topic(topicName);

        LOGGER.debug("Subscription controller, countAllSubscriptions");

        Collection<Subscription> subscriptions = this.selector.retrieveSubscriptionsForTopic(topic);

        result = subscriptions.size();

        return result;

    }

	public ISelector getSelector() {
		return selector;
	}

	public void setSelector(ISelector selector) {
		this.selector = selector;
	}
}