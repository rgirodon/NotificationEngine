package org.notificationengine.web.controller;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.notificationengine.constants.Constants;
import org.notificationengine.selector.ISelector;
import org.notificationengine.selector.ISelectorWriteEnabled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller(value = Constants.CONFIGURATION_CONTROLLER)
public class ConfigurationController {

    private static Logger LOGGER = Logger.getLogger(ConfigurationController.class);

    private Map<String, ISelector> selectors;

    @Value("${config.directory}")
    private String configDirectory;

    public ConfigurationController() {

        LOGGER.debug("ConfigurationController instantiated and listening");

        this.selectors = new HashMap<>();
    }

    @RequestMapping(value = "/getConfiguration.do", method = RequestMethod.GET)
    @ResponseBody
    public String getConfiguration() {

        LOGGER.debug("Get configuration");

        String configurationString = "";

        try {

            configurationString = FileUtils.readFileToString(new File(this.configDirectory + System.getProperty("file.separator") + Constants.CONFIGURATION_FILE_NAME));
        }
        catch (IOException ex) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
        }
        finally {

            return configurationString;
        }
    }

    @RequestMapping(value = "/getSelectors.do", method = RequestMethod.GET)
    @ResponseBody
    public String getJsonSelectors() {

        JSONArray jsonArray = new JSONArray();

        for (Map.Entry<String, ISelector> entry : this.selectors.entrySet()) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.SELECTOR_TYPE, entry.getKey());

            jsonObject.put(Constants.IS_SELECTOR_WRITE_ENABLED, entry.getValue() instanceof ISelectorWriteEnabled);

            jsonArray.add(jsonObject);

        }

        return jsonArray.toString();

    }

    public Map<String, ISelector> getSelectors() {
        return selectors;
    }

    public void setSelectors(Map<String, ISelector> selectors) {
        this.selectors = selectors;
    }

    public void addSelector(String selectorName, ISelector selector) {
        this.selectors.put(selectorName, selector);
    }

    public ISelector getSelectorByName(String selectorName) {
        return this.selectors.get(selectorName);
    }
}
