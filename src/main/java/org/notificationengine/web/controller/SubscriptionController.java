package org.notificationengine.web.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
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
	
	private Map<String, ISelector> selectors;
	
	public SubscriptionController() {
		
		LOGGER.debug("SubscriptionController instantiated and listening");

        this.selectors = new HashMap<>();
	}

	@RequestMapping(value = "/subscription.do", method = RequestMethod.PUT, consumes = "application/json", params = {"selector"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestBody SubscriptionDTO subscriptionDTO, @RequestParam("selector") String selectorName) {
		
		LOGGER.debug("SubscriptionController received : " + subscriptionDTO);

        ISelector selector = this.getSelectorByName(selectorName);
		
		if (selector != null && selector instanceof ISelectorWriteEnabled) {

            Recipient recipient = new Recipient(subscriptionDTO.getRecipient(), subscriptionDTO.getDisplayName());
			
			Subscription subscription = new Subscription();
			subscription.set_id(new ObjectId());
			subscription.setTopic(new Topic(subscriptionDTO.getTopic()));
			subscription.setRecipient(recipient);
			
			((ISelectorWriteEnabled)selector).createSubscription(subscription);
			
			LOGGER.debug("Subscription persisted : " + subscriptionDTO);
		}
    }

    @RequestMapping(value = "/subscription.do", method = RequestMethod.DELETE, params = {"email", "topic", "selector"})
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam("email") String email, @RequestParam("topic") String topic, @RequestParam("selector") String selectorName) {

        LOGGER.debug("SubscriptionController delete subscription with topic " + topic + " and email " + email);

        ISelector selector = this.getSelectorByName(selectorName);

        if (selector != null && selector instanceof ISelectorWriteEnabled) {

            ((ISelectorWriteEnabled)selector).deleteSubscription(email, topic);

        }

    }

    @RequestMapping(value = "/getSubscriptions.do", method = RequestMethod.GET, params = {"selector"})
    @ResponseBody
    public String getAllSubscriptions(@RequestParam("selector") String selectorName) {

        LOGGER.debug("SubscriptionController get subscriptions for selector " + selectorName);

        ISelector selector = this.getSelectorByName(selectorName);

        String result = "";

        if(selector != null) {

            JSONArray subscriptionsJsonArray = new JSONArray();

            Collection<Subscription> subscriptions = selector.retrieveSubscriptions();

            for(Subscription subscription : subscriptions) {

                JSONObject subscriptionJsonObject = new JSONObject();

                subscriptionJsonObject.put(Constants.DISPLAY_NAME, subscription.getRecipient().getDisplayName());

                subscriptionJsonObject.put(Constants.EMAIL, subscription.getRecipient().getAddress());

                subscriptionJsonObject.put(Constants.TOPIC, subscription.getTopic().getName());

                subscriptionJsonObject.put(Constants.SELECTOR_TYPE, selectorName);

                subscriptionsJsonArray.add(subscriptionJsonObject);
            }

            result = subscriptionsJsonArray.toString();

        }

        return result;

    }

    @RequestMapping(value = "/countAllSubscriptions.do", method = RequestMethod.GET, params = {"selector"})
    @ResponseBody
    public String countAllSubscriptions(@RequestParam("selector") String selectorName) {

        LOGGER.debug("Subscription controller, countAllSubscriptions");

        ISelector selector = this.getSelectorByName(selectorName);

        String result = "";

        if(selector != null) {

            Collection<Subscription> subscriptions = selector.retrieveSubscriptions();

            Integer countAllSubscriptions = subscriptions.size();

            JSONObject response = new JSONObject();

            response.put(Constants.COUNT, countAllSubscriptions);

            result = response.toString();
        }
        else {

            JSONObject response = new JSONObject();

            response.put(Constants.COUNT, 0);

            result = response.toString();

        }

        return result;
    }

    @RequestMapping(value = "/countAllSubscriptionsForTopic.do", method = RequestMethod.GET, params = {"topic", "selector"})
    @ResponseBody
    public String countAllSubscriptionsForTopic(@RequestParam(value="topic") String topicName, @RequestParam("selector") String selectorName) {

        String result = "";

        Topic topic = new Topic(topicName);

        LOGGER.debug("Subscription controller, countAllSubscriptionsForTopic " + topicName);

        ISelector selector = this.getSelectorByName(selectorName);

        if(selector != null) {

            Collection<Subscription> subscriptions = selector.retrieveSubscriptionsForTopic(topic);

            Integer countSubscriptionsForTopic = subscriptions.size();

            JSONObject response = new JSONObject();

            response.put(Constants.COUNT, countSubscriptionsForTopic);

            JSONObject topicObject = new JSONObject();

            topicObject.put(Constants.NAME, topic.getName());

            response.put(Constants.TOPIC, topicObject);

            result = response.toString();
        }
        else {

            JSONObject response = new JSONObject();

            response.put(Constants.COUNT, 0);

            JSONObject topicObject = new JSONObject();

            topicObject.put(Constants.NAME, topic.getName());

            response.put(Constants.TOPIC, topicObject);

            result = response.toString();


        }

        return result;
    }

    public Map<String, ISelector> getSelectors() {
        return selectors;
    }

    public void setSelectors(Map<String, ISelector> selectors) {
        this.selectors = selectors;
    }

    public void addSelector(String selectorClassName, ISelector selector) {
        this.selectors.put(selectorClassName, selector);
    }

    public ISelector getSelectorByName(String selectorName) {
        return  this.selectors.get(selectorName);
    }
}