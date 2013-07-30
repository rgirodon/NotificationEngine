package org.notificationengine.web.controller;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class DecoratedNotificationController {

    private static Logger LOGGER = Logger.getLogger(DecoratedNotificationController.class);

    @Autowired
    private Persister persister;

    public DecoratedNotificationController() {

        LOGGER.debug("DecoratedNotificationController instantiated and listening");
    }


    @RequestMapping(value = "/notSentDecoratedNotifications.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public String getNotSentDecoratedNotificationsForTopic(@RequestParam(value="topic") String topicName) {

        LOGGER.debug("getNotSentDecoratedNotificationsForTopic in DecoratedNotificationController");

        Topic topic = new Topic(topicName);

        Collection<DecoratedNotification> decoratedNotifications = this.persister.retrieveNotSentDecoratedNotificationsForTopic(topic);

        Gson gson = new Gson();

        String result = gson.toJson(decoratedNotifications);

        return result;

    }

    public Persister getPersister() {
        return persister;
    }

    public void setPersister(Persister persister) {
        this.persister = persister;
    }


}
