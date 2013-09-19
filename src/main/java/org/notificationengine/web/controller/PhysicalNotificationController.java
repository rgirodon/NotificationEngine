package org.notificationengine.web.controller;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.PhysicalNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.persistance.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;


@Controller
public class PhysicalNotificationController {

    private static Logger LOGGER = Logger.getLogger(PhysicalNotificationController.class);

    @Autowired
    private Persister persister;

    public PhysicalNotificationController() {

        LOGGER.debug("PhysicalNotificationController instantiated and listening");
    }

    @RequestMapping(value = "/physicalNotifications.do",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getAllPhysicalNotifications() {

        LOGGER.info("Get all physicalNotifications");

        Collection<PhysicalNotification> physicalNotifications = this.persister.retrieveAllPhysicalNotifications();

        JSONArray jsonPhysicalNotifications = new JSONArray();

        for(PhysicalNotification physicalNotification : physicalNotifications) {

            jsonPhysicalNotifications.add(this.physicalNotificationToJson(physicalNotification));

        }

        return jsonPhysicalNotifications.toString();

    }

    @RequestMapping(value = "/physicalNotifications.do",
            method = RequestMethod.GET,
            params = {"email"},
            produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getPhysicalNotificationsForEmail(@RequestParam("email") String email) {

        LOGGER.info("Get physicalNotifications for email " + email);

        Collection<PhysicalNotification> physicalNotifications = this.persister.retrievePhysicalNotificationForEmail(email);

        JSONArray jsonPhysicalNotifications = new JSONArray();

        for(PhysicalNotification physicalNotification : physicalNotifications) {

            jsonPhysicalNotifications.add(this.physicalNotificationToJson(physicalNotification));

        }

        return jsonPhysicalNotifications.toString();

    }

    private JSONObject physicalNotificationToJson(PhysicalNotification physicalNotification) {

        JSONObject jsonPhysicalNotification = new JSONObject();

        jsonPhysicalNotification.put(Constants.ID, physicalNotification.hashCode());

        //recipient
        Recipient recipient = physicalNotification.getRecipient();
        JSONObject jsonRecipient = new JSONObject();

        //email
        String email = recipient.getAddress();
        jsonRecipient.put(Constants.EMAIL, email);

        //displayName
        String displayName = recipient.getDisplayName();
        jsonRecipient.put(Constants.DISPLAY_NAME, displayName);

        jsonPhysicalNotification.put(Constants.RECIPIENT, jsonRecipient);

        //sentAt
        jsonPhysicalNotification.put(Constants.SENT_AT, physicalNotification.getSentAt().getTime());

        //subject
        jsonPhysicalNotification.put(Constants.SUBJECT, physicalNotification.getSubject());

        //notificationContent
        jsonPhysicalNotification.put(Constants.NOTIFICATION_CONTENT, physicalNotification.getNotificationContent());

        //filesAttached
        Collection<ObjectId> filesAttachedIds = physicalNotification.getFilesAttachedIds();

        JSONArray jsonFilesAttached = new JSONArray();

        if(filesAttachedIds != null) {
            for(ObjectId fileId : filesAttachedIds) {

                File fileAttached = this.persister.retrieveFileFromId(fileId);

                JSONObject jsonFileAttached = new JSONObject();

                jsonFileAttached.put(Constants.FILE_NAME, fileAttached.getName());

                jsonFileAttached.put(Constants.ID, fileId.toString());

                jsonFilesAttached.add(jsonFileAttached);

            }
        }

        jsonPhysicalNotification.put(Constants.FILES_ATTACHED, jsonFilesAttached);

        return jsonPhysicalNotification;

    }


}
