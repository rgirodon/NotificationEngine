package org.notificationengine.persistance;

import org.notificationengine.constants.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(Constants.MONGODB_SETTINGS)
public class MongoDbSettings {

	@Value("${mongo.replica.mode}")
	private Boolean replicaMode;
	
	@Value("${mongo.url}")
	private String url;
	
	@Value("${mongo.database}")
	private String database;
	
	public MongoDbSettings() {
		super();
	}

	public MongoDbSettings(Boolean replicaMode, String url, String database) {
		super();
		this.replicaMode = replicaMode;
		this.url = url;
		this.database = database;
	}

	public Boolean getReplicaMode() {
		return replicaMode;
	}

	public void setReplicaMode(Boolean replicaMode) {
		this.replicaMode = replicaMode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
