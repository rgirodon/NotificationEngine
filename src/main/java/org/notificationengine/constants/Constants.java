package org.notificationengine.constants;

public class Constants {

	final public static String CONFIGURATION_FILE_NAME = "configuration.json";
	
	final public static String CHANNELS = "channels";
	
	final public static String TOPIC = "topic";
	
	final public static String TOPIC_NAME = "topic.name";
	
	final public static String ID = "id";
	
	final public static String SELECTOR_TYPE = "selectorType";
	
	final public static String SELECTOR_TYPE_MONGODB = "mongoDbSelector";
	
	final public static String DATABASE = "notificationengine";
	
	final public static String DATABASE_TEST = "notificationengine_test";
	
	final public static String RAW_NOTIFICATIONS_COLLECTION = "rawnotifications";
	
	final public static String DECORATED_NOTIFICATIONS_COLLECTION = "decoratednotifications";
	
	final public static String PERSISTER = "persister";

	public static final Object PROCESSED = "processed";

	public static final Object _ID = "_id";

	public static final Object REGEX = "$regex";

	public static final long SELECTOR_TASK_DELAY = 20000;

	public static final long SELECTOR_TASK_PERIOD = 60000;

	public static final String DOT = ".";
}
