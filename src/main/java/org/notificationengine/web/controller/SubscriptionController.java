package org.notificationengine.web.controller;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
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

            Recipient recipient = new Recipient(subscriptionDTO.getRecipient(), subscriptionDTO.getDisplayName());
			
			Subscription subscription = new Subscription();
			subscription.set_id(new ObjectId());
			subscription.setTopic(new Topic(subscriptionDTO.getTopic()));
			subscription.setRecipient(recipient);
			
			((ISelectorWriteEnabled)this.selector).createSubscription(subscription);
			
			LOGGER.debug("Subscription persisted : " + subscriptionDTO);
		}
    }

    @RequestMapping(value = "/subscription.do", method = RequestMethod.DELETE, params = {"email", "topic"})
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam("email") String email, @RequestParam("topic") String topic) {

        LOGGER.debug("SubscriptionController delete subscription with topic " + topic + " and email " + email);

        if (this.selector instanceof ISelectorWriteEnabled) {

            ((ISelectorWriteEnabled)this.selector).deleteSubscription(email, topic);

        }

    }

    @RequestMapping(value = "/countAllSubscriptions.do", method = RequestMethod.GET)
    @ResponseBody
    public String countAllSubscriptions() {

        LOGGER.debug("Subscription controller, countAllSubscriptions");

        Collection<Subscription> subscriptions = this.selector.retrieveSubscriptions();

        Integer countAllSubscriptions = subscriptions.size();

        JSONObject response = new JSONObject();

        response.put(Constants.COUNT, countAllSubscriptions);

        return response.toString();

    }

    @RequestMapping(value = "/countAllSubscriptionsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public String countAllSubscriptionsForTopic(@RequestParam(value="topic") String topicName) {

        Topic topic = new Topic(topicName);

        LOGGER.debug("Subscription controller, countAllSubscriptionsForTopic " + topicName);

        Collection<Subscription> subscriptions = this.selector.retrieveSubscriptionsForTopic(topic);

        Integer countSubscriptionsForTopic = subscriptions.size();

        JSONObject response = new JSONObject();

        response.put(Constants.COUNT, countSubscriptionsForTopic);

        response.put(Constants.TOPIC, topic);

        return response.toString();

    }

	public ISelector getSelector() {
		return selector;
	}

	public void setSelector(ISelector selector) {
		this.selector = selector;
	}
}