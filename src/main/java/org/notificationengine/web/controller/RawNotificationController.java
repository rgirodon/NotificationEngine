package org.notificationengine.web.controller;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Topic;
import org.notificationengine.dto.RawNotificationDTO;
import org.notificationengine.persistance.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

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
	
	public Persister getPersister() {
		return persister;
	}

	public void setPersister(Persister persister) {
		this.persister = persister;
	}
}
