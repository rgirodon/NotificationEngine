package org.notificationengine.cleaner;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.notificationengine.constants.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Properties;


@Component(Constants.CLEANER_TOOL)
public class CleanerTool implements InitializingBean {

    private static Logger LOGGER = Logger.getLogger(CleanerTool.class);

    @Autowired
    private Properties localSettingsProperties;

    private File workingDirectory;

    public CleanerTool() {

    }

    public void init() {

        String workingDirectoryPath = this.localSettingsProperties.getProperty(Constants.WORKING_DIRECTORY);

        this.workingDirectory = new File(workingDirectoryPath);

    }

    public void process() {

        File filesInWorkingDirectory[] = this.workingDirectory.listFiles();

        for(File fileToCheck : filesInWorkingDirectory) {

            long lastModified = fileToCheck.lastModified();

            DateTime lastModifiedDate = new DateTime(lastModified);

            DateTime now = new DateTime();

            DateTime tenMinutesEarlier = now.minusMinutes(10);

            if(lastModifiedDate.isBefore(tenMinutesEarlier)) {

                this.delete(fileToCheck);

            }
        }

    }

    // Taken from http://www.mkyong.com/java/how-to-delete-directory-in-java/
    private void delete(File file) {

        if(file.isDirectory()) {

            //if it is empty, we can delete it
            if(file.list().length == 0) {

                file.delete();
                LOGGER.debug("Directory " + file.getAbsolutePath() + " deleted");

            }
            else {

                File files[] = file.listFiles();

                for(File fileFound : files) {

                    //recursive delete
                    this.delete(fileFound);
                }

                //Check if file is empty again
                if(file.list().length == 0) {

                    file.delete();

                    LOGGER.debug("Directory " + file.getAbsolutePath() + " deleted");
                }
            }

        }
        else {
            file.delete();

            LOGGER.debug("Directory " + file.getAbsolutePath() + " deleted");
        }

    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        this.init();

    }
}
