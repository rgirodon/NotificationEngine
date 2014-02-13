package org.notificationengine.persistance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.json.simple.JSONObject;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.DecoratedNotification;
import org.notificationengine.domain.PhysicalNotification;
import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Topic;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.web.multipart.MultipartFile;

@Component(value=Constants.PERSISTER)
public class Persister implements InitializingBean {

	private static Logger LOGGER = Logger.getLogger(Persister.class);
	
	@Autowired
	private MongoDbSettings mongoDbSettings;

    @Autowired
    private Properties localSettingsProperties;

    private Boolean modeTest;
	
	private MongoCollection rawNotifications;
	
	private MongoCollection decoratedNotifications;

	private MongoCollection deletedDecoratedNotifications;

	private MongoCollection physicalNotifications;

    private DB db;

	public Persister() {
		
		this(Boolean.FALSE);
	}

	// take care, only to be called by test class
	public Persister(Boolean modeTest, MongoDbSettings mongoDbSettings) {
		
		this.modeTest = modeTest;
		
		this.mongoDbSettings = mongoDbSettings;
		
		this.init();
	}
	
	public Persister(Boolean modeTest) {
		
		this.modeTest = modeTest;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		this.init();
	}

    public DB getDb() {
        return db;
    }

    public void setDb(DB db) {
        this.db = db;
    }

    private void init() {
		
		try {
			this.db = null;
			
			if (this.modeTest) {
				this.db = new MongoClient().getDB(Constants.DATABASE_TEST);
			}
			else {
				MongoClient mongoClient = null;
				
				if (!this.mongoDbSettings.getReplicaMode()) {
					
					String host = MongoDbUtils.getHostFromSingleServerUrl(this.mongoDbSettings.getUrl());
					
					int port = MongoDbUtils.getPortFromSingleServerUrl(this.mongoDbSettings.getUrl());
					
					ServerAddress addr = new ServerAddress(host, port);	
					
					mongoClient = new MongoClient(addr);
				}
				else {
					List<ServerAddress> addrs = MongoDbUtils.getServerAddressListFromMultipleServerUrl(this.mongoDbSettings.getUrl());
					
					mongoClient = new MongoClient(addrs);
				}
				
				this.db = mongoClient.getDB(this.mongoDbSettings.getDatabase());
			}
			
			Jongo jongo = new Jongo(this.db);
			
			this.rawNotifications = jongo.getCollection(Constants.RAW_NOTIFICATIONS_COLLECTION);
			
			this.decoratedNotifications = jongo.getCollection(Constants.DECORATED_NOTIFICATIONS_COLLECTION);

            this.deletedDecoratedNotifications = jongo.getCollection(Constants.DELETED_DECORATED_NOTIFICATIONS_COLLECTION);

            this.physicalNotifications = jongo.getCollection(Constants.PHYSICAL_NOTIFICATIONS_COLLECTION);
		}
		catch (UnknownHostException e) {

			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			
			LOGGER.error("Unable to build Persister");
		}
	}	

	public void createDecoratedNotification(
			DecoratedNotification decoratedNotification) {
		
		this.decoratedNotifications.save(decoratedNotification);

		LOGGER.debug("Inserted DecoratedNotification : " + decoratedNotification);
	}
	
	public void createRawNotification(
			RawNotification rawNotification) {
		
		this.rawNotifications.save(rawNotification);
		
		LOGGER.debug("Inserted RawNotification : " + rawNotification);
	}

    public void createPhysicalNotification(PhysicalNotification physicalNotification) {

        this.physicalNotifications.save(physicalNotification);

        LOGGER.debug("Inserted physicalNotification: " + physicalNotification);
    }

    public Collection<ObjectId> saveFiles(List<MultipartFile> files) {

        Collection<ObjectId> fileIds = new HashSet<>();

        if(files != null) {

            for(MultipartFile file : files) {

                try {
                    InputStream inputStream = null;

                    String fileName = file.getOriginalFilename();

                    inputStream = file.getInputStream();

                    GridFS gfsResources = new GridFS(this.db, "resources");

                    GridFSInputFile gfsFile = gfsResources.createFile(inputStream);

                    gfsFile.setFilename(fileName);

                    gfsFile.setContentType(file.getContentType());

                    gfsFile.save();

                    ObjectId fileId = (ObjectId) gfsFile.getId();

                    fileIds.add(fileId);

                    inputStream.close();

                    LOGGER.debug("File " + fileName + " has been saved with id " + fileId);

                }
                catch(Exception e) {

                    LOGGER.warn(ExceptionUtils.getFullStackTrace(e));
                }
            }
        }

        return fileIds;

    }

    public File retrieveFileFromId(ObjectId id) {

        LOGGER.debug("Retrieve file from id " + id.toString());

        GridFS gfsResources = new GridFS(this.db, "resources");

        GridFSDBFile gfsDbFile = gfsResources.findOne(id);

        String fileName = gfsDbFile.getFilename();

        String path = this.localSettingsProperties.getProperty(Constants.WORKING_DIRECTORY);

        File file = new File(path + fileName);

        try {

            InputStream is = gfsDbFile.getInputStream();

            FileUtils.copyInputStreamToFile(is, file);

            is.close();

        }
        catch (IOException ex) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
        }

        return file;

    }

    public File retrieveFileFromIdAndFileName(ObjectId objectId, String fileName) {

        LOGGER.debug("Retrieve file from filename " + fileName);

        GridFS gfsResources = new GridFS(this.db, "resources");

        GridFSDBFile gfsDbFile = gfsResources.findOne(objectId);

        StringBuilder sb = new StringBuilder();

        sb.append(this.localSettingsProperties.getProperty(Constants.WORKING_DIRECTORY));

        sb.append("/");

        long now = new Date().getTime();

        sb.append(now);

        sb.append("/");

        String path = sb.toString();

        File file = new File(path + fileName);

        try {

            InputStream is = gfsDbFile.getInputStream();

            FileUtils.copyInputStreamToFile(is, file);

            is.close();

        }
        catch (IOException ex) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
        }

        return file;

    }
	
	public Collection<RawNotification> retrieveAllRawNotifications() {
		
		LOGGER.debug("Retrieve all RawNotifications");
		
		Collection<RawNotification> result = new ArrayList<>();
		
		Iterable<RawNotification> rawNotificationsForGetAll =
                this.rawNotifications.find().sort("{createdAt: -1}").as(RawNotification.class);
		
		for(RawNotification rawNotification : rawNotificationsForGetAll) {
			
			LOGGER.debug("Found RawNotification (get all): " + rawNotification);
			
			result.add(rawNotification);
			
		}
		
		LOGGER.debug("All RawNotifications found");
		
		return result;
		
	}

    public Collection<RawNotification> retrieveAllRawNotificationsForTopic(Topic topic) {

        LOGGER.debug("Retrieve not processed RawNotifications for Topic : " + topic);

        Collection<RawNotification> result = new ArrayList<>();

        JSONObject exactQueryJsonObject = new JSONObject();
        exactQueryJsonObject.put(Constants.TOPIC_NAME, topic.getName());

        String exactQuery = exactQueryJsonObject.toString();

        Iterable<RawNotification> rawNotificationsForExactQuery =
                this.rawNotifications.find(exactQuery).sort("{createdAt: -1}").as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForExactQuery) {

            LOGGER.debug("Found RawNotification (exact query) : " + rawNotification);

            result.add(rawNotification);
        }


        JSONObject likeQueryJsonObject = new JSONObject();

        JSONObject regularExpressionJsonObject = new JSONObject();
        regularExpressionJsonObject.put(Constants.REGEX, topic.getName() + "\\..*");

        likeQueryJsonObject.put(Constants.TOPIC_NAME, regularExpressionJsonObject);

        String likeQuery = likeQueryJsonObject.toString();

        Iterable<RawNotification> rawNotificationsForLikeQuery = this.rawNotifications.find(likeQuery).as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForLikeQuery) {

            LOGGER.debug("Found RawNotification (like query) : " + rawNotification);

            result.add(rawNotification);
        }

        return result;

    }

    public Collection<RawNotification> retrieveUrgentAndNotProcessedRawNotificationsForTopic(Topic topic) {

        LOGGER.debug("Retrieve urgent and not processed RawNotifications for Topic : " + topic);

        Collection<RawNotification> result = new ArrayList<>();

        JSONObject exactQueryJsonObject = new JSONObject();
        exactQueryJsonObject.put(Constants.PROCESSED, Boolean.FALSE);
        exactQueryJsonObject.put(Constants.TOPIC_NAME, topic.getName());
        exactQueryJsonObject.put(Constants.CONTEXT_URGENT, Boolean.TRUE);

        String exactQuery = exactQueryJsonObject.toString();

        Iterable<RawNotification> rawNotificationsForExactQuery = this.rawNotifications.find(exactQuery).as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForExactQuery) {

            LOGGER.debug("Found RawNotification (exact query) : " + rawNotification);

            result.add(rawNotification);
        }

        JSONObject likeQueryJsonObject = new JSONObject();

        JSONObject regularExpressionJsonObject = new JSONObject();
        regularExpressionJsonObject.put(Constants.REGEX, topic.getName() + "\\..*");

        likeQueryJsonObject.put(Constants.TOPIC_NAME, regularExpressionJsonObject);
        likeQueryJsonObject.put(Constants.CONTEXT_URGENT, Boolean.TRUE);
        likeQueryJsonObject.put(Constants.PROCESSED, Boolean.FALSE);

        String likeQuery = likeQueryJsonObject.toString();

        Iterable<RawNotification> rawNotificationsForLikeQuery = this.rawNotifications.find(likeQuery).as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForLikeQuery) {

            LOGGER.debug("Found RawNotification (like query) : " + rawNotification);

            result.add(rawNotification);
        }

        return result;

    }

    public Collection<RawNotification> retrieveNotProcessedRawNotifications() {

        LOGGER.debug("Retrieve not processed RawNotifications");

        Collection<RawNotification> result = new ArrayList<>();

        JSONObject exactQueryJsonObject = new JSONObject();
        exactQueryJsonObject.put(Constants.PROCESSED, Boolean.FALSE);

        String exactQuery = exactQueryJsonObject.toString();

        Iterable<RawNotification> rawNotificationsForExactQuery =
                this.rawNotifications.find(exactQuery).sort("{createdAt: -1}").as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForExactQuery) {

            LOGGER.debug("Found RawNotification (exact query) : " + rawNotification);

            result.add(rawNotification);
        }

        return result;
    }

	public Collection<RawNotification> retrieveNotProcessedRawNotificationsForTopic(Topic topic) {
		
		LOGGER.debug("Retrieve not processed RawNotifications for Topic : " + topic);
		
		Collection<RawNotification> result = new ArrayList<>();
		
		JSONObject exactQueryJsonObject = new JSONObject();		
		exactQueryJsonObject.put(Constants.PROCESSED, Boolean.FALSE);		
		exactQueryJsonObject.put(Constants.TOPIC_NAME, topic.getName());
		
		String exactQuery = exactQueryJsonObject.toString();
		
		Iterable<RawNotification> rawNotificationsForExactQuery = this.rawNotifications.find(exactQuery).as(RawNotification.class);
		
		for(RawNotification rawNotification : rawNotificationsForExactQuery) {
			
			LOGGER.debug("Found RawNotification (exact query) : " + rawNotification);
			
			result.add(rawNotification);
		}		
		
		JSONObject likeQueryJsonObject = new JSONObject();		
		likeQueryJsonObject.put(Constants.PROCESSED, Boolean.FALSE);
		
		JSONObject regularExpressionJsonObject = new JSONObject();
		regularExpressionJsonObject.put(Constants.REGEX, topic.getName() + "\\..*");
		
		likeQueryJsonObject.put(Constants.TOPIC_NAME, regularExpressionJsonObject);
		
		String likeQuery = likeQueryJsonObject.toString();
		
		Iterable<RawNotification> rawNotificationsForLikeQuery = this.rawNotifications.find(likeQuery).as(RawNotification.class);
		
		for(RawNotification rawNotification : rawNotificationsForLikeQuery) {
			
			LOGGER.debug("Found RawNotification (like query) : " + rawNotification);
			
			result.add(rawNotification);
		}
		
		return result;
	}

	public void markRawNotificationAsProcessed(RawNotification rawNotification) {
		
		rawNotification.setProcessed(Boolean.TRUE);
        rawNotification.setProcessedAt(new Date());
		
		this.rawNotifications.save(rawNotification);
	}

	public void cleanRawNotifications() {
		
		this.rawNotifications.remove();
	}
	
	public void cleanDecoratedNotifications() {
		
		this.decoratedNotifications.remove();
	}

    public void cleanDeletedDecoratedNotifications() {

        this.deletedDecoratedNotifications.remove();
    }

	public RawNotification retrieveRawNotificationById(ObjectId id) {

		return this.rawNotifications.findOne(id).as(RawNotification.class);
	}

	public Collection<DecoratedNotification> retrieveNotSentDecoratedNotificationsForTopic(
			Topic topic) {
		
		LOGGER.debug("Retrieve not sent DecoratedNotifications for Topic : " + topic);
		
		Collection<DecoratedNotification> result = new ArrayList<>();
		
		JSONObject exactQueryJsonObject = new JSONObject();		
		exactQueryJsonObject.put(Constants.SENT, Boolean.FALSE);		
		exactQueryJsonObject.put(Constants.RAW_NOTIFICATION_TOPIC_NAME, topic.getName());
		
		String exactQuery = exactQueryJsonObject.toString();
		
		Iterable<DecoratedNotification> decoratedNotificationsForExactQuery = this.decoratedNotifications.find(exactQuery).as(DecoratedNotification.class);
		
		for(DecoratedNotification decoratedNotification : decoratedNotificationsForExactQuery) {
			
			LOGGER.debug("Found DecoratedNotification (exact query) : " + decoratedNotification);
			
			result.add(decoratedNotification);
		}		
		
		JSONObject likeQueryJsonObject = new JSONObject();		
		likeQueryJsonObject.put(Constants.SENT, Boolean.FALSE);
		
		JSONObject regularExpressionJsonObject = new JSONObject();
		regularExpressionJsonObject.put(Constants.REGEX, topic.getName() + "\\..*");
		
		likeQueryJsonObject.put(Constants.RAW_NOTIFICATION_TOPIC_NAME, regularExpressionJsonObject);
		
		String likeQuery = likeQueryJsonObject.toString();
		
		Iterable<DecoratedNotification> decoratedNotificationsForLikeQuery = this.decoratedNotifications.find(likeQuery).as(DecoratedNotification.class);
		
		for(DecoratedNotification decoratedNotification : decoratedNotificationsForLikeQuery) {
			
			LOGGER.debug("Found DecoratedNotification (like query) : " + decoratedNotification);
			
			result.add(decoratedNotification);
		}
		
		return result;
	}

    public Collection<DecoratedNotification> retrieveUrgentAndNotSentDecoratedNotificationsForTopic(
            Topic topic) {

        LOGGER.debug("Retrieve urgent and not sent DecoratedNotifications for Topic : " + topic);

        Collection<DecoratedNotification> result = new ArrayList<>();

        JSONObject exactQueryJsonObject = new JSONObject();
        exactQueryJsonObject.put(Constants.SENT, Boolean.FALSE);
        exactQueryJsonObject.put(Constants.RAW_NOTIFICATION_TOPIC_NAME, topic.getName());
        exactQueryJsonObject.put(Constants.RAW_NOTIFICATION_CONTEXT_URGENT, Boolean.TRUE);

        String exactQuery = exactQueryJsonObject.toString();

        Iterable<DecoratedNotification> decoratedNotificationsForExactQuery = this.decoratedNotifications.find(exactQuery).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForExactQuery) {

            LOGGER.debug("Found DecoratedNotification (exact query) : " + decoratedNotification);

            result.add(decoratedNotification);
        }

        JSONObject likeQueryJsonObject = new JSONObject();
        likeQueryJsonObject.put(Constants.SENT, Boolean.FALSE);

        JSONObject regularExpressionJsonObject = new JSONObject();
        regularExpressionJsonObject.put(Constants.REGEX, topic.getName() + "\\..*");

        likeQueryJsonObject.put(Constants.RAW_NOTIFICATION_TOPIC_NAME, regularExpressionJsonObject);
        likeQueryJsonObject.put(Constants.RAW_NOTIFICATION_CONTEXT_URGENT, Boolean.TRUE);

        String likeQuery = likeQueryJsonObject.toString();

        Iterable<DecoratedNotification> decoratedNotificationsForLikeQuery = this.decoratedNotifications.find(likeQuery).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForLikeQuery) {

            LOGGER.debug("Found DecoratedNotification (like query) : " + decoratedNotification);

            result.add(decoratedNotification);
        }

        return result;
    }

    public Collection<DecoratedNotification> retrieveAllDecoratedNotifications() {

        LOGGER.debug("Retrieve all DecoratedNotifications");

        Collection<DecoratedNotification> result = new ArrayList<>();

        Iterable<DecoratedNotification> decoratedNotifications =
                this.decoratedNotifications.find().sort("{sentAt: -1}").as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotifications) {

            LOGGER.debug("Found DecoratedNotification (exact query) : " + decoratedNotification);

            result.add(decoratedNotification);
        }

        return result;

    }

    public Collection<DecoratedNotification> retrieveDecoratedNotificationsForEmail(String email) {

        LOGGER.debug("Retrieve DecoratedNotifications for email");

        Collection<DecoratedNotification> result = new ArrayList<>();

        JSONObject jsonQuery = new JSONObject();

        jsonQuery.put(Constants.RECIPIENT_ADDRESS, email);

        String query = jsonQuery.toString();

        Iterable<DecoratedNotification> decoratedNotifications =
                this.decoratedNotifications.find(query).sort("{sentAt: -1}").as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotifications) {

            LOGGER.debug("Found DecoratedNotification (exact query) : " + decoratedNotification);

            result.add(decoratedNotification);
        }

        return result;

    }

    public Collection<DecoratedNotification> retrieveAllDecoratedNotificationsForTopic(Topic topic) {

        LOGGER.debug("Retrieve not sent DecoratedNotifications for Topic : " + topic);

        Collection<DecoratedNotification> result = new ArrayList<>();

        JSONObject exactQueryJsonObject = new JSONObject();
        exactQueryJsonObject.put(Constants.RAW_NOTIFICATION_TOPIC_NAME, topic.getName());

        String exactQuery = exactQueryJsonObject.toString();

        LOGGER.debug("Exact query: " + exactQuery);

        Iterable<DecoratedNotification> decoratedNotificationsForExactQuery = this.decoratedNotifications.find(exactQuery).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForExactQuery) {

            LOGGER.debug("Found DecoratedNotification (exact query) : " + decoratedNotification);

            result.add(decoratedNotification);
        }


        JSONObject likeQueryJsonObject = new JSONObject();

        JSONObject regularExpressionJsonObject = new JSONObject();
        regularExpressionJsonObject.put(Constants.REGEX, topic.getName() + "\\..*");

        likeQueryJsonObject.put(Constants.RAW_NOTIFICATION_TOPIC_NAME, regularExpressionJsonObject);

        String likeQuery = likeQueryJsonObject.toString();

        Iterable<DecoratedNotification> decoratedNotificationsForLikeQuery = this.decoratedNotifications.find(likeQuery).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForLikeQuery) {

            LOGGER.debug("Found DecoratedNotification (like query) : " + decoratedNotification);

            result.add(decoratedNotification);
        }

        return result;

    }

    public Collection<DecoratedNotification> retrieveNotSentDecoratedNotifications() {

        LOGGER.debug("Retrieve not sent DecoratedNotifications");

        Collection<DecoratedNotification> result = new ArrayList<>();

        JSONObject exactQueryJsonObject = new JSONObject();
        exactQueryJsonObject.put(Constants.SENT, Boolean.FALSE);

        String exactQuery = exactQueryJsonObject.toString();

        Iterable<DecoratedNotification> decoratedNotificationsForExactQuery =
                this.decoratedNotifications.find(exactQuery).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForExactQuery) {

            LOGGER.debug("Found DecoratedNotification (exact query) : " + decoratedNotification);

            result.add(decoratedNotification);
        }

        return result;
    }

    public Collection<RawNotification> retrieveRawNotificationsForDate(Date date) {

        Collection<RawNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{createdAt: {$gt: #, $lt: #}}";

        LOGGER.debug("Date query: " + exactQuery);

        Iterable<RawNotification> rawNotificationsForDate = this.rawNotifications.find(exactQuery, beginDate, endDate).as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForDate) {

            result.add(rawNotification);

        }

        return result;

    }

    public Collection<RawNotification> retrieveRawNotificationsForDateAndTopic(Date date, Topic topic) {

        LOGGER.debug("retrieveRawNotificationsForDate " + date.toString() + " with topic " + topic.getName());

        Collection<RawNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{createdAt: {$gt: #, $lt: #}, \"topic.name\": #}";

        Iterable<RawNotification> rawNotificationsForDateAndTopic =
                this.rawNotifications.find(exactQuery, beginDate, endDate, topic.getName()).as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForDateAndTopic) {

            result.add(rawNotification);

        }

        String likeQuery = "{createdAt: {$gt: #, $lt: #}, \"topic.name\": {$regex: #}}";

        Iterable<RawNotification> rawNotificationsForDateAndTopicLike =
                this.rawNotifications.find(likeQuery, beginDate, endDate, topic.getName() + "\\..*").as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForDateAndTopicLike) {

            result.add(rawNotification);

        }

        return result;

    }

    public Collection<RawNotification> retrieveProcessedRawNotificationsForDate(Date date) {

        Collection<RawNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{processedAt: {$gt: #, $lt: #}, processed: true}";

        LOGGER.debug("Date query: " + exactQuery);

        Iterable<RawNotification> rawNotificationsForDate = this.rawNotifications.find(exactQuery, beginDate, endDate).as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForDate) {

            result.add(rawNotification);

        }

        return result;

    }

    public Collection<RawNotification> retrieveProcessedRawNotificationsForDateAndTopic(Date date, Topic topic) {

        LOGGER.debug("retrieveProcessedRawNotificationsForDate " + date.toString() + " with topic " + topic.getName());

        Collection<RawNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{createdAt: {$gt: #, $lt: #}, processed: true, \"topic.name\": #}";

        Iterable<RawNotification> rawNotificationsForDateAndTopic =
                this.rawNotifications.find(exactQuery, beginDate, endDate, topic.getName()).as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForDateAndTopic) {

            result.add(rawNotification);

        }

        String likeQuery = "{createdAt: {$gt: #, $lt: #}, processed: true, \"topic.name\": {$regex: #}}";

        Iterable<RawNotification> rawNotificationsForDateAndTopicLike =
                this.rawNotifications.find(likeQuery, beginDate, endDate, topic.getName() + "\\..*").as(RawNotification.class);

        for(RawNotification rawNotification : rawNotificationsForDateAndTopicLike) {

            result.add(rawNotification);

        }

        return result;

    }

    public Collection<DecoratedNotification> retrieveSentDecoratedNotificationsForDate(Date date) {

        LOGGER.debug("retrieveSentDecoratedNotificationsForDate: " + date.toString() );

        Collection<DecoratedNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{sentAt: {$gt: #, $lt: #}, sent: true}";

        LOGGER.debug("Date query: " + exactQuery);

        Iterable<DecoratedNotification> decoratedNotificationsForDate =
                this.decoratedNotifications.find(exactQuery, beginDate, endDate).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDate) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);

        }

        return result;

    }

    public Collection<DecoratedNotification> retrieveSentDecoratedNotificationsForDateAndTopic(Date date, Topic topic) {

        LOGGER.debug("retrieveSentDecoratedNotificationsForDate: " + date.toString() + " with topic " + topic.getName());

        Collection<DecoratedNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{sentAt: {$gt: #, $lt: #}, sent: true, \"rawNotification.topic.name\": #}";

        Iterable<DecoratedNotification> decoratedNotificationsForDateAndTopic =
                this.decoratedNotifications.find(exactQuery, beginDate, endDate, topic.getName()).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDateAndTopic) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);

        }

        String likeQuery = "{sentAt: {$gt: #, $lt: #}, sent: true, \"rawNotification.topic.name\": {$regex: #}}";

        Iterable<DecoratedNotification> decoratedNotificationsForDateAndLikeTopic =
                this.decoratedNotifications.find(likeQuery, beginDate, endDate, topic.getName() + "\\..*").as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDateAndLikeTopic) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);

        }

        return result;

    }

    public Collection<DecoratedNotification> retrieveDecoratedNotificationsForDate(Date date) {

        LOGGER.debug("retrieveDecoratedNotificationsForDate: " + date.toString() );

        Collection<DecoratedNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{createdAt: {$gt: #, $lt: #}}";

        Iterable<DecoratedNotification> decoratedNotificationsForDate =
                this.decoratedNotifications.find(exactQuery, beginDate, endDate).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDate) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);

        }

        return result;

    }

    public Collection<DecoratedNotification> retrieveDecoratedNotificationsForDateAndTopic(Date date, Topic topic) {

        LOGGER.debug("retrieveDecoratedNotificationsForDate: " + date.toString() + " with topic " + topic.getName());

        Collection<DecoratedNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{createdAt: {$gt: #, $lt: #}, \"rawNotification.topic.name\": #}";

        Iterable<DecoratedNotification> decoratedNotificationsForDateAndTopic =
                this.decoratedNotifications.find(exactQuery, beginDate, endDate, topic.getName()).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDateAndTopic) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);

        }

        String likeQuery = "{createdAt: {$gt: #, $lt: #}, \"rawNotification.topic.name\": {$regex: #}}";

        Iterable<DecoratedNotification> decoratedNotificationsForDateAndLikeTopic =
                this.decoratedNotifications.find(likeQuery, beginDate, endDate, topic.getName() + "\\..*").as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDateAndLikeTopic) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);

        }

        return result;

    }

    public Collection<DecoratedNotification> retrieveDecoratedNotificationsForEmailAndDate(String email, Date date) {

        LOGGER.debug("retrieveDecoratedNotificationsForEmailAndDate: " + date.toString() + " with email " + email);

        Collection<DecoratedNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{createdAt: {$gt: #, $lt: #}, \"recipient.address\": #}";

        Iterable<DecoratedNotification> decoratedNotificationsForDateAndTopic =
                this.decoratedNotifications.find(exactQuery, beginDate, endDate, email).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDateAndTopic) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);

        }

        return result;
    }

    public Collection<DecoratedNotification> retrieveDecoratedNotificationsForCriteria(String criteriaName, String criteriaValue) {

        LOGGER.debug("retrieveDecoratedNotificationsForCriteria: criteria is " + criteriaName + " with value " + criteriaValue);

        Collection<DecoratedNotification> result = new ArrayList<>();

        String recipientProperty = Constants.RECIPIENT + "." + criteriaName;

        JSONObject query = new JSONObject();
        query.put(recipientProperty, criteriaValue);

        String exactQuery = query.toString();

        Iterable<DecoratedNotification> decoratedNotificationsForDateAndTopic =
                this.decoratedNotifications.find(exactQuery).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDateAndTopic) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);
        }

        return result;
    }

    public Collection<DecoratedNotification> retrieveDecoratedNotificationsForCriteriaAndDate(String criteriaName, String criteriaValue, Date date) {

        LOGGER.debug("retrieveDecoratedNotificationsForCriteria: criteria is " + criteriaName + " with value " + criteriaValue);

        Collection<DecoratedNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        if (criteriaName.equals(Constants.EMAIL)) {
            criteriaName = Constants.ADDRESS;
        }

        String recipientProperty = Constants.RECIPIENT + "." + criteriaName;

        JSONObject query = new JSONObject();
        query.put(recipientProperty, criteriaValue);

        // query created manually
        String exactQuery = "{createdAt: {$gt: #, $lt: #}, \"" + recipientProperty + "\": #}";

        Iterable<DecoratedNotification> decoratedNotificationsForDateAndTopic =
                this.decoratedNotifications.find(exactQuery, beginDate, endDate, criteriaValue).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDateAndTopic) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);
        }

        return result;
    }

    public Collection<Topic> retrieveAllTopics() {

        LOGGER.debug("Retrieve all Topics");

        Collection<Topic> result = new HashSet<>();

        Collection<RawNotification> rawNotifications = this.retrieveAllRawNotifications();

        for(RawNotification rowNotification : rawNotifications) {

            result.add(rowNotification.getTopic());
        }

        for(Topic topic : result) {

            LOGGER.debug("Topic found: " + topic);
        }

        return result;

    }

    public Collection<Topic> retrieveAllSubTopicsForTopic(Topic topic) {

        LOGGER.debug("Retrieve all subtopics of topic " + topic.getName());

        Collection<Topic> result = new HashSet<>();

        Collection<RawNotification> rawNotifications = this.retrieveAllRawNotificationsForTopic(topic);

        for(RawNotification rawNotification : rawNotifications) {

            result.add(rawNotification.getTopic());
        }

        for(Topic topicFound : result) {
            LOGGER.debug("Topic found " + topicFound);
        }

        return result;

    }

	public void markDecoratedNotificationAsSent(
			DecoratedNotification decoratedNotification) {
		
		decoratedNotification.setSent(Boolean.TRUE);
        decoratedNotification.setSentAt(new Date());
		
		this.decoratedNotifications.save(decoratedNotification);
	}

	public DecoratedNotification retrieveDecoratedNotificationById(
			ObjectId id) {
		
		return this.decoratedNotifications.findOne(id).as(DecoratedNotification.class);
	}

	public Boolean getModeTest() {
		return modeTest;
	}

	public void setModeTest(Boolean modeTest) {
		this.modeTest = modeTest;
	}
	
	public MongoDbSettings getMongoDbSettings() {
		return mongoDbSettings;
	}

	public void setMongoDbSettings(MongoDbSettings mongoDbSettings) {
		this.mongoDbSettings = mongoDbSettings;
	}

	public MongoCollection getRawNotifications() {
		return rawNotifications;
	}

	public void setRawNotifications(MongoCollection rawNotifications) {
		this.rawNotifications = rawNotifications;
	}
	
	public MongoCollection getDecoratedNotifications() {
		return decoratedNotifications;
	}

	public void setDecoratedNotifications(MongoCollection decoratedNotifications) {
		this.decoratedNotifications = decoratedNotifications;
	}

    public MongoCollection getDeletedDecoratedNotifications() {
        return deletedDecoratedNotifications;
    }

    public void setDeletedDecoratedNotifications(MongoCollection deletedDecoratedNotifications) {
        this.deletedDecoratedNotifications = deletedDecoratedNotifications;
    }

    public MongoCollection getPhysicalNotifications() {
        return physicalNotifications;
    }

    public void setPhysicalNotifications(MongoCollection physicalNotifications) {
        this.physicalNotifications = physicalNotifications;
    }

    public void moveFailedDecoratedNotification(DecoratedNotification decoratedNotificationToDelete) {

        ObjectId decoratedNotificationId = decoratedNotificationToDelete.get_id();

        this.decoratedNotifications.remove(decoratedNotificationId);

        //We store deleted decoratedNotifications in another collection to have some metrics
        this.saveDeletedDecoratedNotification(decoratedNotificationToDelete);

    }

    public void saveDeletedDecoratedNotification(DecoratedNotification decoratedNotification) {

        decoratedNotification.setDeletedAt(new Date());

        this.deletedDecoratedNotifications.save(decoratedNotification);
    }

	public void saveDecoratedNotification(
			DecoratedNotification decoratedNotificationToSave) {
		
		this.decoratedNotifications.save(decoratedNotificationToSave);
	}

    public void savePhysicalNotification(PhysicalNotification physicalNotification) {

        this.physicalNotifications.save(physicalNotification);
    }

    public Collection<DecoratedNotification> retrieveAllDeletedDecoratedNotifications() {

        LOGGER.debug("Retrieve all DecoratedNotifications");

        Collection<DecoratedNotification> result = new ArrayList<>();

        Iterable<DecoratedNotification> decoratedNotifications = this.deletedDecoratedNotifications.find().as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotifications) {

            LOGGER.debug("Found DeletedDecoratedNotification (exact query) : " + decoratedNotification);

            result.add(decoratedNotification);
        }

        return result;

    }

    public Collection<DecoratedNotification> retrieveDeletedDecoratedNotificationForDate(Date date) {

        LOGGER.debug("retrieveDeletedDecoratedNotificationForDate: " + date.toString() );

        Collection<DecoratedNotification> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date beginDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        Date endDate = cal.getTime();

        // query created manually
        String exactQuery = "{deletedAt: {$gt: #, $lt: #}}";

        Iterable<DecoratedNotification> decoratedNotificationsForDate =
                this.deletedDecoratedNotifications.find(exactQuery, beginDate, endDate).as(DecoratedNotification.class);

        for(DecoratedNotification decoratedNotification : decoratedNotificationsForDate) {

            LOGGER.debug("Decorated notification found: " + decoratedNotification);

            result.add(decoratedNotification);

        }

        return result;
    }

    public Collection<PhysicalNotification> retrieveAllPhysicalNotifications() {

        LOGGER.debug("Retrieve all physical notifications");

        Collection<PhysicalNotification> result = new ArrayList<>();

        Iterable<PhysicalNotification> physicalNotifications =
                this.physicalNotifications.find().sort("{sentAt: -1}").as(PhysicalNotification.class);

        for(PhysicalNotification physicalNotification : physicalNotifications) {

            LOGGER.debug("PhysicalNotification found: " + physicalNotification);

            result.add(physicalNotification);

        }

        return result;

    }

    public Collection<PhysicalNotification> retrievePhysicalNotificationForEmail(String email) {

        LOGGER.debug("Retrieve physical notifications sent to " + email);

        Collection<PhysicalNotification> result = new ArrayList<>();

        JSONObject exactQueryJson = new JSONObject();

        exactQueryJson.put(Constants.RECIPIENT_ADDRESS, email);

        String query = exactQueryJson.toString();

        Iterable<PhysicalNotification> physicalNotifications = this.physicalNotifications.find(query).as(PhysicalNotification.class);

        for(PhysicalNotification physicalNotification : physicalNotifications) {

            LOGGER.debug("Physical notification found:" + physicalNotification);

            result.add(physicalNotification);

        }

        return result;

    }
}
