package org.notificationengine.web.listener;

import org.apache.log4j.Logger;
import org.notificationengine.cleaner.CleanerTool;
import org.notificationengine.constants.Constants;
import org.notificationengine.spring.SpringUtils;
import org.notificationengine.task.CleanerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Timer;

public class WorkingDirectoryListener implements ServletContextListener {

    private static Logger LOGGER = Logger.getLogger(WorkingDirectoryListener.class);

    public WorkingDirectoryListener() {

        LOGGER.debug("Working directory listener instantiated");

    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        Timer timer = new Timer();

        CleanerTool cleanerTool = (CleanerTool) SpringUtils.getBean(Constants.CLEANER_TOOL);

        timer.schedule(new CleanerTask(cleanerTool), Constants.CLEAN_TASK_PERDIOD, Constants.CLEAN_TASK_DELAY);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
