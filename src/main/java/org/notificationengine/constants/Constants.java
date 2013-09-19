package org.notificationengine.constants;

public class Constants {

	final public static String CONFIGURATION_FILE_NAME = "configuration.json";
	
	final public static String CHANNELS = "channels";
	
	final public static String TOPIC = "topic";

	final public static String NAME = "name";

	final public static String EMAIL = "email";

    final public static String FILE_NAME = "fileName";

    final public static String FILES_ATTACHED = "filesAttached";

	final public static String NOTIFICATION_CONTENT = "notificationContent";

	final public static String TOPIC_NAME = "topic.name";

	final public static String RECIPIENT_ADDRESS = "recipient.address";

    final public static String DEFAULT_SUBJECT = "mail.subject";
    
    final public static String DEFAULT_FROM = "mail.from";

	final public static String RAW_NOTIFICATION_TOPIC_NAME = "rawNotification.topic.name";

	final public static String ID = "id";
	
	final public static String SELECTOR_TYPE = "selectorType";
	
	public static final String NOTIFICATOR_TYPE = "notificatorType";
	
	final public static String SELECTOR_TYPE_MONGODB = "mongoDbSelector";
	
	final public static String DEFAULT_MONGODB_URL = "localhost:27017";
	
	final public static String DEFAULT_MONGODB_DATABASE = "notificationengine";
	
	final public static String DATABASE_TEST = "notificationengine_test";
	
	final public static String RAW_NOTIFICATIONS_COLLECTION = "rawnotifications";
	
	final public static String DECORATED_NOTIFICATIONS_COLLECTION = "decoratednotifications";

	final public static String DELETED_DECORATED_NOTIFICATIONS_COLLECTION = "deleteddecoratednotifications";

	final public static String PHYSICAL_NOTIFICATIONS_COLLECTION = "physicalnotifications";

	final public static String SUBSCRIPTIONS_COLLECTION = "subscriptions";
	
	final public static String PERSISTER = "persister";

	final public static String CLEANER_TOOL = "cleanerTool";

	final public static String TEMPLATE_ENGINE = "templateEngine";
	
	final public static String MAILER = "mailer";
	
	final public static String CONFIGURATION_READER = "configurationReader";

    public static final String CREATED_AT = "createdAt";

	public static final Object PROCESSED = "processed";

	public static final Object _ID = "_id";

	public static final Object REGEX = "$regex";

	public static final long SELECTOR_TASK_DELAY = 10000;

	public static final long SELECTOR_TASK_PERIOD = 60000;

	public static final long CLEAN_TASK_PERDIOD = 60000;

	public static final long CLEAN_TASK_DELAY = 60000;

	public static final String DOT = ".";

	public static final String SENT = "sent";

	public static final String SENT_AT = "sentAt";

	public static final String NOTIFICATOR_TYPE_MULTIPLE_MAIL_BY_RECIPIENT = "multipleMailByRecipient";
	
	public static final String NOTIFICATOR_TYPE_SINGLE_MAIL_BY_RECIPIENT = "singleMailByRecipient";
	
	public static final String NOTIFICATOR_TYPE_SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT = "singleMultiTopicMailByRecipient";
	
	public static final String SINGLE_MULTI_TOPIC_MAIL_BY_RECIPIENT_NOTIFICATOR = "singleMultiTopicMailByRecipientNotificator";

	public static final int NOTIFICATOR_TASK_DELAY = 20000;
	
	public static final long NOTIFICATOR_TASK_PERIOD = 60000;	

	public static final String TEMPLATE_EXTENSION = ".template";
	
	public static final String MAIL_TEMPLATE = "mailTemplate";

    public static final String IS_HTML_TEMPLATE = "isHtmlTemplate";

	public static final String RECIPIENT = "recipient";

	public static final String DISPLAY_NAME = "displayName";

	public static final String NOTIFICATIONS_BY_RECIPIENT = "notificationsByRecipient";

	public static final String SUBSCRIPTION_CONTROLLER = "subscriptionController";

	public static final String CONFIGURATION_CONTROLLER = "configurationController";

	public static final String NOTIFICATIONS_FOR_TOPIC = "notificationsForTopic";

	public static final String TOPICS = "topics";

	public static final String NOTIFICATOR_TYPE_CUSTOM = "customNotificator";
	
	public static final String SELECTOR_TYPE_CUSTOM = "customSelector";

	public static final String OPTION_NOTIFICATOR_TASK_PERIOD = "notificatorTaskPeriod";
	
	public static final String OPTION_SELECTOR_TASK_PERIOD = "selectorTaskPeriod";
	
	public static final String MONGODB_SETTINGS = "mongoDbSettings";

	public static final String SELECTOR_CLASS = "selectorClass";

	public static final String IS_SELECTOR_WRITE_ENABLED = "isSelectorWriteEnabled";

	public static final String ADMINISTRATOR_ADDRESS = "administrator.address";

    public static final String ADMINISTRATOR_DISPLAY_NAME = "administrator.name";

	public static final String LOCAL_SETTINGS_PROPERTIES = "localSettingsProperties";

	public static final String NOTIFICATOR_CLASS = "notificatorClass";

    public static final Integer MAX_ATTEMPTS = 5;

	public static final String SUBJECT = "subject";
	
	public static final String FROM = "from";

    public static final String COUNT = "count";

    public static final String DATE = "date";

    public static final String RESOURCES_FOLDER = "resources.folder";
    
    public static final String WORKING_DIRECTORY = "working.directory";

    public static final double MAX_ATTACHMENT_SIZE = 1024*1024*10;

    public static final String MAIL_TEXT_OTHER_ATTACHMENTS = "Please find attachments for previous email";

    public static final String RECIPIENTS = "recipients";

    public static final String SELECTOR_TYPE_HOLD_IN_NOTIFICATION = "holdInNotificationSelector";

    public static final String CONTEXT_URGENT = "context.urgent";

    public static final String RAW_NOTIFICATION_CONTEXT_URGENT = "rawNotification.context.urgent";

    public static final String URGENT_ENABLED = "urgentEnabled";

    public static final long URGENT_SELECTOR_TASK_DELAY = 5000;

    public static final long URGENT_NOTIFICATOR_TASK_DELAY = 5000;

    public static final long URGENT_SELECTOR_TASK_PERIOD = 5000;

    public static final long URGENT_NOTIFICATOR_TASK_PERIOD = 5000;

    public static final String URGENT_MAIL_TEMPLATE = "urgentMailTemplate";

    public static final String IS_URGENT_MAIL_TEMPLATE = "isUrgentHtmlTemplate";

}
