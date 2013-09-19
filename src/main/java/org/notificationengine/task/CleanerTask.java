package org.notificationengine.task;

import org.apache.log4j.Logger;
import org.notificationengine.cleaner.CleanerTool;
import java.util.TimerTask;

public class CleanerTask extends TimerTask {

    private static Logger LOGGER = Logger.getLogger(CleanerTask.class);

    private CleanerTool cleanerTool;

    public CleanerTask(CleanerTool cleanerTool) {

        LOGGER.debug("Instantiates CleanerTask");

        this.cleanerTool = cleanerTool;

        LOGGER.debug("Instantiates CleanerTask");

    }

    @Override
    public void run() {

        LOGGER.debug("CleanerTask launched");

        this.cleanerTool.process();

        LOGGER.debug("CleanerTask finished");

    }
}
