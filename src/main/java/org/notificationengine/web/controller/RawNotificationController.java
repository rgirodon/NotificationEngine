package org.notificationengine.web.controller;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jongo.MongoCollection;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.dto.RawNotificationDTO;
import org.notificationengine.persistance.Persister;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class RawNotificationController {
	
	private static Logger LOGGER = Logger.getLogger(RawNotificationController.class);
	
	@Autowired
	private Persister persister;
		
	public RawNotificationController() {
		
		LOGGER.debug("RawNotificationController instantiated and listening");
	}

	@RequestMapping(value = "/rawNotification.do", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestBody RawNotificationDTO rawNotificationDTO) {
		
		LOGGER.debug("RawNotificationController received : " + rawNotificationDTO);
		
		RawNotification rawNotification = new RawNotification();
		rawNotification.set_id(new ObjectId());
		rawNotification.setProcessed(Boolean.FALSE);
		rawNotification.setTopic(new Topic(rawNotificationDTO.getTopic()));
		rawNotification.setContext(rawNotificationDTO.getContext());
		
		persister.createRawNotification(rawNotification);
		
		LOGGER.debug("RawNotification persisted : " + rawNotificationDTO);
    }

    @RequestMapping(value = "/allRawNotifications.do", method = RequestMethod.GET)
    @ResponseBody
    public String listAllRawNotifications() {

        LOGGER.debug("RawNotificationController GET listAllRawNotifications");

        Collection<RawNotification> rawNotifications =  this.persister.retrieveAllRawNotifications();

        Gson gson = new Gson();

        String result = gson.toJson(rawNotifications);

        return result;

    }

    @RequestMapping(value = "/allRowNotificationsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public String listRowNotificationsForTopic(@RequestParam(value="topic") String topicName) {

        LOGGER.debug("RawNotificationsController GET listRowNotificationsForTopic, topic:" + topicName);

        Topic topic = new Topic(topicName);

        Collection<RawNotification> rawNotifications = this.persister.retrieveAllRawNotificationsForTopic(topic);

        Gson gson = new Gson();

        String result = gson.toJson(rawNotifications);

        return result;

    }

    @RequestMapping(value = "/notProceededRawNotificationsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public String notProceededRawNotifications(@RequestParam(value="topic") String topicName) {

        LOGGER.debug("RawNotificationController GET notProceededRawNotifications, topic: " + topicName);

        Topic topic = new Topic(topicName);

        Collection<RawNotification> rawNotifications = this.persister.retrieveNotProcessedRawNotificationsForTopic(topic);

        Gson gson = new Gson();

        String result = gson.toJson(rawNotifications);

        return result;

    }

    @RequestMapping(value = "/rawNotification.do", method =  RequestMethod.GET, params = {"id"})
    @ResponseBody
    public String rawNotificationById(@RequestParam(value="id") String id) {

        ObjectId objId = new ObjectId(id);

        RawNotification rawNotification = this.persister.retrieveRawNotificationById(objId);

        Gson gson = new Gson();

        String result = gson.toJson(rawNotification);

        return result;

    }

    @RequestMapping(value = "/countAllRawNotifications.do", method = RequestMethod.GET)
    @ResponseBody
    public Integer getCountAllRawNotifications() {

        Integer result = new Integer(0);

        Collection<RawNotification> rawNotifications = this.persister.retrieveAllRawNotifications();

        result = rawNotifications.size();

        return result;

    }

    @RequestMapping(value = "/countNotProcessedRawNotifications.do", method = RequestMethod.GET)
    @ResponseBody
    public Integer getCountNotProcessedRawNotifications() {

        Integer result = new Integer(0);

        Collection<RawNotification> rawNotifications = this.persister.retrieveNotProcessedRawNotifications();

        result = rawNotifications.size();

        return result;

    }

    @RequestMapping(value = "/countRawNotificationsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public Integer getCountRawNotificationsForTopic(@RequestParam(value="topic") String topicName) {

        Integer result = new Integer(0);

        Topic topic = new Topic(topicName);

        Collection<RawNotification> rawNotifications = this.persister.retrieveAllRawNotificationsForTopic(topic);

        result = rawNotifications.size();

        return result;

    }

    @RequestMapping(value = "/countNotProcessedRawNotificationsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public Integer getCountNotProcessedRawNotificationsForTopic(@RequestParam(value="topic") String topicName) {

        Integer result = new Integer(0);

        Topic topic = new Topic(topicName);

        Collection<RawNotification> rawNotifications = this.persister.retrieveNotProcessedRawNotificationsForTopic(topic);

        result = rawNotifications.size();

        return result;

    }

    @RequestMapping(value = "/getCreatedRawNotificationsForLastDays.do", method = RequestMethod.GET, params = {"days"})
    @ResponseBody
    public String getRawNotificationsCreatedForLastDays(@RequestParam("days") Integer nbDays) {

        Date date = new Date();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        // Create all dates wanted to retrieve data
        Collection<Date> datesToGet = new ArrayList<>();

        for(Integer day = 0; day < nbDays; day ++) {

            datesToGet.add(cal.getTime());

            cal.add(Calendar.DAY_OF_MONTH, -1);

        }

        //Retrieve data and store it to be sent as response

        Map<String, Integer> stats = new HashMap<>();

        for(Date atDate : datesToGet) {

            Integer nbRowNotificationsCreated = this.persister.retrieveRawNotificationsForDate(atDate).size();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String formattedDate = dateFormat.format(atDate);

            stats.put(formattedDate, nbRowNotificationsCreated);

        }

        Gson gson = new Gson();

        String result = gson.toJson(stats);

        return result;

    }

    @RequestMapping(value = "/getProcessedRawNotificationsForLastDays.do", method = RequestMethod.GET, params = {"days"})
    @ResponseBody
    public String getRawNotificationsProcessedForLastDays(@RequestParam("days") Integer nbDays) {

        Date date = new Date();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        // Create all dates wanted to retrieve data
        Collection<Date> datesToGet = new ArrayList<>();

        for(Integer day = 0; day < nbDays; day ++) {

            datesToGet.add(cal.getTime());

            cal.add(Calendar.DAY_OF_MONTH, -1);

        }

        //Retrieve data and store it to be sent as response

        Map<String, Integer> stats = new HashMap<>();

        for(Date atDate : datesToGet) {

            Integer nbRowNotificationsCreated = this.persister.retrieveProcessedRawNotificationsForDate(atDate).size();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String formattedDate = dateFormat.format(atDate);

            stats.put(formattedDate, nbRowNotificationsCreated);

        }

        Gson gson = new Gson();

        String result = gson.toJson(stats);

        return result;

    }

	public Persister getPersister() {
		return persister;
	}

	public void setPersister(Persister persister) {
		this.persister = persister;
	}
}
