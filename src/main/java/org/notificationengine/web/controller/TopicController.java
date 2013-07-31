package org.notificationengine.web.controller;


import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.notificationengine.domain.Topic;
import org.notificationengine.persistance.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
public class TopicController {

    private static Logger LOGGER = Logger.getLogger(TopicController.class);

    @Autowired
    private Persister persister;

    public TopicController() {

        LOGGER.debug("TopicController instantiated and listening");

    }

    @RequestMapping(value = "/topics.do", method = RequestMethod.GET)
    @ResponseBody
    public String getAllTopics() {

        LOGGER.debug("TopicController: get all topics");

        Gson gson = new Gson();

        Collection<Topic> topics = this.persister.retrieveAllTopics();

        String result = gson.toJson(topics);

        return result;

    }

    @RequestMapping(value = "/subTopicsForTopic.do", method = RequestMethod.GET, params = {"topic"})
    @ResponseBody
    public String getAllSubTopicsForTopic(@RequestParam(value="topic") String topicName) {

        LOGGER.debug("TopicController: get all subTopics for Topic " + topicName);

        Topic topic = new Topic(topicName);

        Collection<Topic> subTopics = this.persister.retrieveAllSubTopicsForTopic(topic);

        Gson gson = new Gson();

        String result = gson.toJson(subTopics);

        return result;

    }

    public Persister getPersister() {
        return persister;
    }

    public void setPersister(Persister persister) {
        this.persister = persister;
    }
}
