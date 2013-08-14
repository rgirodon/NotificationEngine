package org.notificationengine.notificator.mail;

import org.notificationengine.domain.Topic;

import java.util.Collection;
import java.util.HashSet;

public class MailTemplateConfiguration {

    private Boolean isHtmlTemplate;

    private Collection<Topic> topics;

    public MailTemplateConfiguration() {

        this.isHtmlTemplate = Boolean.FALSE;
        this.topics = new HashSet<>();

    }

    public MailTemplateConfiguration(Boolean htmlTemplate, Collection<Topic> topics) {

        this.isHtmlTemplate = htmlTemplate;
        this.topics = topics;

    }

    public Boolean getHtmlTemplate() {
        return isHtmlTemplate;
    }

    public void setHtmlTemplate(Boolean htmlTemplate) {

        if(!this.isHtmlTemplate) {

            isHtmlTemplate = htmlTemplate;
        }

    }

    public Collection<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Collection<Topic> topics) {
        this.topics = topics;
    }

    public void add(Boolean isHtmlTemplate, Topic topic) {

        this.setHtmlTemplate(isHtmlTemplate);

        this.topics.add(topic);

    }
}
