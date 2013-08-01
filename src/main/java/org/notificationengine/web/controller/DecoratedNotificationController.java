package org.notificationengine.web.controller;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class DecoratedNotificationController {

    private static Logger LOGGER = Logger.getLogger(DecoratedNotificationController.class);

    @Autowired
    private Persister persister;

    public DecoratedNotificationController() {

        LOGGER.debug("DecoratedNotificationController instantiated and listening");
    }


    @RequestMapping(value = "/notSentDecoratedNotificationsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public String getNotSentDecoratedNotificationsForTopic(@RequestParam(value="topic") String topicName) {

        LOGGER.debug("getNotSentDecoratedNotificationsForTopic in DecoratedNotificationController");

        Topic topic = new Topic(topicName);

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveNotSentDecoratedNotificationsForTopic(topic);

        Gson gson = new Gson();

        String result = gson.toJson(decoratedNotifications);

        return result;

    }

    @RequestMapping(value = "/countNotSentDecoratedNotifications.do", method = RequestMethod.GET)
    @ResponseBody
    public Integer getCountNotSentDecoratedNotifications() {

        LOGGER.debug("getCountNotSentDecoratedNotifications in DecoratedNotificationController");

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveNotSentDecoratedNotifications();

        Integer result = decoratedNotifications.size();

        return result;

    }

    @RequestMapping(value = "/countAllDecoratedNotifications.do", method = RequestMethod.GET)
    @ResponseBody
    public Integer getCountAllDecoratedNotifications() {

        LOGGER.debug("getCountNotSentDecoratedNotifications in DecoratedNotificationController");

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveAllDecoratedNotifications();

        Integer result = decoratedNotifications.size();

        return result;

    }

    @RequestMapping(value = "/countAllDecoratedNotificationsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public Integer getCountAllDecoratedNotificationsForTopic(@RequestParam(value="topic") String topicName) {

        LOGGER.debug("getCountNotSentDecoratedNotifications in DecoratedNotificationController");

        Topic topic = new Topic(topicName);

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveAllDecoratedNotificationsForTopic(topic);

        Integer result = decoratedNotifications.size();

        return result;

    }

    @RequestMapping(value = "/countNotSentDecoratedNotificationsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public Integer getCountNotSentDecoratedNotificationsForTopic(@RequestParam(value="topic") String topicName) {

        LOGGER.debug("getCountNotSentDecoratedNotificationsForTopic in DecoratedNotificationController");

        Topic topic = new Topic(topicName);

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveNotSentDecoratedNotificationsForTopic(topic);

        Integer result = decoratedNotifications.size();

        return result;

    }

    @RequestMapping(value = "/getSentDecoratedNotificationsForLastDays.do", method = RequestMethod.GET, params = {"days"})
    @ResponseBody
    public String getSentDecoratedNotificationsForLastDays(@RequestParam("days") Integer nbDays) {

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

            Integer nbRowNotificationsCreated = this.persister.retrieveSentDecoratedNotificationsForDate(atDate).size();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String formattedDate = dateFormat.format(atDate);

            stats.put(formattedDate, nbRowNotificationsCreated);

        }

        Gson gson = new Gson();

        String result = gson.toJson(stats);

        return result;

    }

    @RequestMapping(value = "/getCreatedDecoratedNotificationsForLastDays.do", method = RequestMethod.GET, params = {"days"})
    @ResponseBody
    public String getCreatedDecoratedNotificationsForLastDays(@RequestParam("days") Integer nbDays) {

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

            Integer nbRowNotificationsCreated = this.persister.retrieveDecoratedNotificationsForDate(atDate).size();

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
