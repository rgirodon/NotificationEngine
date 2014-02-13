package org.notificationengine.task;

import org.apache.log4j.Logger;
import org.notificationengine.cleaner.TokenCleaner;

import java.util.TimerTask;


public class TokenCleanerTask extends TimerTask {

    private static Logger LOGGER = Logger.getLogger(TokenCleanerTask.class);

    private TokenCleaner tokenCleaner;

    public TokenCleanerTask(TokenCleaner tokenCleaner) {

        LOGGER.debug("Instantiates TokenCleanerTask");

        this.tokenCleaner = tokenCleaner;

        LOGGER.debug("Instantiated CleanerTask");
    }

    @Override
    public void run() {

        LOGGER.debug("CleanerTask launched");

        this.tokenCleaner.process();

        LOGGER.debug("CleanerTask finished");
    }
}
