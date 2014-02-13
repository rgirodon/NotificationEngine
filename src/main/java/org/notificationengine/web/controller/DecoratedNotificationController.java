package org.notificationengine.web.controller;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.notificationengine.constants.Constants;
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
    public String getCountNotSentDecoratedNotifications() {

        LOGGER.debug("getCountNotSentDecoratedNotifications in DecoratedNotificationController");

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveNotSentDecoratedNotifications();

        Integer countNotSentDecoratedNotifications = decoratedNotifications.size();

        JSONObject response = new JSONObject();

        response.put(Constants.COUNT, countNotSentDecoratedNotifications);

        return response.toString();

    }

    @RequestMapping(value = "/countAllDecoratedNotifications.do", method = RequestMethod.GET)
    @ResponseBody
    public String getCountAllDecoratedNotifications() {

        LOGGER.debug("getCountNotSentDecoratedNotifications in DecoratedNotificationController");

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveAllDecoratedNotifications();

        Integer countDecoratedNotifications = decoratedNotifications.size();

        JSONObject response = new JSONObject();

        response.put(Constants.COUNT, countDecoratedNotifications);

        return response.toString();

    }

    @RequestMapping(value = "/getDecoratedAllNotifications.do", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getDecoratedNotifications(
            @RequestParam(value = "number", required = false) Integer number, @RequestParam(value = "email", required = false) String email) {

        LOGGER.debug("getCountNotSentDecoratedNotifications in DecoratedNotificationController");

        Collection<DecoratedNotification> decoratedNotifications;

        if(StringUtils.isEmpty(email)) {

             decoratedNotifications = this.persister.retrieveAllDecoratedNotifications();
        }
        else {

            decoratedNotifications = this.persister.retrieveDecoratedNotificationsForEmail(email);
        }

        Collection<DecoratedNotification> wantedDecoratedNotifications = new HashSet<>();

        if(number != null && number < decoratedNotifications.size()) {
            for(int i = 0; i < number; i++) {

                wantedDecoratedNotifications.add(Iterables.get(decoratedNotifications, i));
            }
        }
        else {

            wantedDecoratedNotifications = decoratedNotifications;
        }

        Gson gson = new Gson();

        String result = gson.toJson(wantedDecoratedNotifications);

        return result;

    }

    @RequestMapping(value = "/getDecoratedNotifications.do", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getDecoratedNotificationsForCriteria(
            @RequestParam(value = "criteriaName", required = false) String criteriaName,
            @RequestParam(value = "criteriaValue", required = false) String criteriaValue,
            @RequestParam(value = "number", required = false) Integer number,
            @RequestParam(value = "days", required = false) Integer days) {

        LOGGER.debug("Get decorated notifications with params: criteriaName=" + criteriaName + ", criteriaValue="
                + criteriaValue + ", number=" + number + ", days=" + days);

        List<DecoratedNotification> decoratedNotifications = new ArrayList<>();

        if (days != null && days > 0) {

            Collection<Date> datesToGet = this.getDatesForDays(days);

            if (StringUtils.isEmpty(criteriaValue)) {
                //Return everything for x days last days

                for (Date date : datesToGet) {

                    decoratedNotifications.addAll(this.persister.retrieveDecoratedNotificationsForDate(date));
                }

            } else if (StringUtils.isEmpty(criteriaName)) {
                //Return everything for given email for x last days

                for (Date date : datesToGet) {

                    decoratedNotifications.addAll(this.persister.retrieveDecoratedNotificationsForEmailAndDate(criteriaValue, date));
                }

            } else {
                //Return everything for the given param with the given value for x last days

                for (Date date : datesToGet) {

                    decoratedNotifications.addAll(this.persister.retrieveDecoratedNotificationsForCriteriaAndDate(criteriaName, criteriaValue, date));
                }
            }

        } else {

            if (StringUtils.isEmpty(criteriaValue)) {

                //Return everything
                decoratedNotifications.addAll(this.persister.retrieveAllDecoratedNotifications());

            } else if (StringUtils.isEmpty(criteriaName)) {

                //Return everything for the given email
                decoratedNotifications.addAll(this.persister.retrieveDecoratedNotificationsForEmail(criteriaValue));

            } else {

                //Return everything for the given param with given value
                decoratedNotifications.addAll(this.persister.retrieveDecoratedNotificationsForCriteria(criteriaName, criteriaValue));
            }

        }

        //Sort the list of decorated notifications by decreasing date
        Collections.sort(decoratedNotifications, new Comparator<DecoratedNotification>() {
            @Override
            public int compare(DecoratedNotification dn1, DecoratedNotification dn2) {
                return dn2.getCreatedAt().compareTo(dn1.getCreatedAt());
            }
        });

        Collection<DecoratedNotification> wantedDecoratedNotifications = new HashSet<>();

        if(number != null && number > 0 && number < decoratedNotifications.size()) {

            for(int i = 0; i < number; i++) {

                wantedDecoratedNotifications.add(Iterables.get(decoratedNotifications, i));
            }
        }
        else {

            wantedDecoratedNotifications = decoratedNotifications;
        }

        Gson gson = new Gson();

        String result = gson.toJson(wantedDecoratedNotifications);

        return result;
    }

    @RequestMapping(value = "/countAllDecoratedNotificationsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public String getCountAllDecoratedNotificationsForTopic(@RequestParam(value="topic") String topicName) {

        LOGGER.debug("getCountNotSentDecoratedNotificationsForTopic " + topicName + " in DecoratedNotificationController");

        Topic topic = new Topic(topicName);

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveAllDecoratedNotificationsForTopic(topic);

        Integer countDecoratedNotificationsForTopic = decoratedNotifications.size();

        JSONObject response = new JSONObject();

        response.put(Constants.COUNT, countDecoratedNotificationsForTopic);

        JSONObject topicObject = new JSONObject();

        topicObject.put(Constants.NAME, topic.getName());

        response.put(Constants.TOPIC, topicObject);

        return response.toString();

    }

    @RequestMapping(value = "/countNotSentDecoratedNotificationsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public String getCountNotSentDecoratedNotificationsForTopic(@RequestParam(value="topic") String topicName) {

        LOGGER.debug("getCountNotSentDecoratedNotificationsForTopic in DecoratedNotificationController");

        Topic topic = new Topic(topicName);

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveNotSentDecoratedNotificationsForTopic(topic);

        Integer countNotSentDecoratedNotificationsForTopic = decoratedNotifications.size();

        JSONObject response = new JSONObject();

        response.put(Constants.COUNT, countNotSentDecoratedNotificationsForTopic);

        JSONObject topicObject = new JSONObject();

        topicObject.put(Constants.NAME, topic.getName());

        response.put(Constants.TOPIC, topicObject);

        return response.toString();

    }

    @RequestMapping(value = "/countSentDecoratedNotificationsForLastDays.do", method = RequestMethod.GET, params = {"days"})
    @ResponseBody
    public String getSentDecoratedNotificationsForLastDays(@RequestParam("days") Integer nbDays) {

        Collection<Date> datesToGet = this.getDatesForDays(nbDays);

        //Retrieve data and store it to be sent as response

        Map<String, Integer> stats = new HashMap<>();

        for(Date atDate : datesToGet) {

            Integer nbRowNotificationsCreated = this.persister.retrieveSentDecoratedNotificationsForDate(atDate).size();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String formattedDate = dateFormat.format(atDate);

            stats.put(formattedDate, nbRowNotificationsCreated);

        }

        JSONArray result = new JSONArray();

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {

            JSONObject oneStat = new JSONObject();

            oneStat.put(Constants.DATE, entry.getKey());
            oneStat.put(Constants.COUNT, entry.getValue());

            result.add(oneStat);
        }

        return result.toString();

    }

    @RequestMapping(value = "/countCreatedDecoratedNotificationsForLastDays.do", method = RequestMethod.GET, params = {"days"})
    @ResponseBody
    public String getCreatedDecoratedNotificationsForLastDays(@RequestParam("days") Integer nbDays) {

        Collection<Date> datesToGet = this.getDatesForDays(nbDays);

        //Retrieve data and store it to be sent as response

        Map<String, Integer> stats = new HashMap<>();

        for(Date atDate : datesToGet) {

            Integer nbRowNotificationsCreated = this.persister.retrieveDecoratedNotificationsForDate(atDate).size();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String formattedDate = dateFormat.format(atDate);

            stats.put(formattedDate, nbRowNotificationsCreated);

        }

        JSONArray result = new JSONArray();

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {

            JSONObject oneStat = new JSONObject();

            oneStat.put(Constants.DATE, entry.getKey());
            oneStat.put(Constants.COUNT, entry.getValue());

            result.add(oneStat);
        }

        return result.toString();

    }

    @RequestMapping(value = "/countCreatedDecoratedNotificationsForLastDaysWithTopic.do", method = RequestMethod.GET, params = {"days", "topic"})
    @ResponseBody
    public String getCreatedDecoratedNotificationsForLastDaysWithTopic(@RequestParam("days") Integer nbDays, @RequestParam("topic") String topicName) {

        Topic topic = new Topic(topicName);

        Collection<Date> datesToGet = this.getDatesForDays(nbDays);

        //Retrieve data and store it to be sent as response

        Map<String, Integer> stats = new HashMap<>();

        for(Date atDate : datesToGet) {

            Integer nbRowNotificationsCreated = this.persister.retrieveDecoratedNotificationsForDateAndTopic(atDate, topic).size();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String formattedDate = dateFormat.format(atDate);

            stats.put(formattedDate, nbRowNotificationsCreated);

        }

        JSONArray result = new JSONArray();

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {

            JSONObject oneStat = new JSONObject();

            oneStat.put(Constants.DATE, entry.getKey());
            oneStat.put(Constants.COUNT, entry.getValue());

            result.add(oneStat);
        }

        return result.toString();

    }

    @RequestMapping(value = "/countSentDecoratedNotificationsForLastDaysWithTopic.do", method = RequestMethod.GET, params = {"days", "topic"})
    @ResponseBody
    public String getSentDecoratedNotificationsForLastDaysWithTopic(@RequestParam("days") Integer nbDays, @RequestParam("topic") String topicName) {

        Topic topic = new Topic(topicName);

        Collection<Date> datesToGet = this.getDatesForDays(nbDays);

        //Retrieve data and store it to be sent as response

        Map<String, Integer> stats = new HashMap<>();

        for(Date atDate : datesToGet) {

            Integer nbRowNotificationsCreated = this.persister.retrieveSentDecoratedNotificationsForDateAndTopic(atDate, topic).size();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String formattedDate = dateFormat.format(atDate);

            stats.put(formattedDate, nbRowNotificationsCreated);

        }

        JSONArray result = new JSONArray();

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {

            JSONObject oneStat = new JSONObject();

            oneStat.put(Constants.DATE, entry.getKey());
            oneStat.put(Constants.COUNT, entry.getValue());

            result.add(oneStat);
        }

        return result.toString();

    }

    private Collection<Date> getDatesForDays(Integer numberOfDays) {
        Date date = new Date();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        // Create all dates wanted to retrieve data
        Collection<Date> datesToGet = new ArrayList<>();

        for(Integer day = 0; day < numberOfDays; day ++) {

            datesToGet.add(cal.getTime());

            cal.add(Calendar.DAY_OF_MONTH, -1);

        }

        return datesToGet;
    }

    public Persister getPersister() {
        return persister;
    }

    public void setPersister(Persister persister) {
        this.persister = persister;
    }


}
