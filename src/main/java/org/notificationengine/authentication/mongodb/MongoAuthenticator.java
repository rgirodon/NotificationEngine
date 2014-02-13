package org.notificationengine.authentication.mongodb;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.json.simple.JSONObject;
import org.notificationengine.authentication.Authenticator;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.User;
import org.notificationengine.persistance.MongoDbSettings;
import org.notificationengine.persistance.MongoDbUtils;
import org.notificationengine.spring.SpringUtils;

import java.net.UnknownHostException;
import java.util.List;

public class MongoAuthenticator implements Authenticator {

    private static Logger LOGGER = Logger.getLogger(MongoAuthenticator.class);

    private Boolean modeTest;

    private MongoDbSettings mongoDbSettings;

    private MongoCollection usersCollection;

    public MongoAuthenticator() {

        this(Boolean.FALSE);
    }

    public MongoAuthenticator(Boolean modeTest) {

        LOGGER.debug("MongoAuthenticator instantiated");

        this.modeTest = modeTest;

        this.mongoDbSettings = (MongoDbSettings) SpringUtils.getBean(Constants.MONGODB_SETTINGS);

        this.init();
    }

    public MongoAuthenticator(Boolean modeTest, MongoDbSettings mongoDbSettings) {

        this.modeTest = modeTest;

        this.mongoDbSettings = mongoDbSettings;

        this.init();
    }

    public void init() {
        try {
            DB db = null;

            if (this.modeTest) {
                db = new MongoClient().getDB(Constants.DATABASE_TEST);
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

                db = mongoClient.getDB(this.mongoDbSettings.getDatabase());
            }

            Jongo jongo = new Jongo(db);

            this.usersCollection = jongo.getCollection(Constants.USERS_COLLECTION);
        }
        catch (UnknownHostException e) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(e));

            LOGGER.error("Unable to build MongoAuthenticator");
        }
    }

    @Override
    public Boolean isAdmin(User user) {

        return this.isAdmin(user.getUsername(), user.getPassword());
    }

    @Override
    public Boolean isAdmin(String username, String encryptedPassword) {

        JSONObject queryWithPassword = new JSONObject();
        queryWithPassword.put(Constants.USERNAME, username);
        queryWithPassword.put(Constants.PASSWORD, encryptedPassword);

        String query = queryWithPassword.toString();

        User userFound = this.usersCollection.findOne(query).as(User.class);

        if (userFound != null) {
            LOGGER.debug("User " + username + " logged in");
            return Boolean.TRUE;
        }

        LOGGER.debug("User " + username + " tried to log in");
        return Boolean.FALSE;
    }

    public void saveUser(String username, String password) {

        User newUser = new User(username, password);

        this.saveUser(newUser);
    }

    public void saveUser(User user) {

        this.usersCollection.save(user);
    }

    public void updateUser(User user) {

        this.updateUser(user.getId(), user.getUsername(), user.getPassword());
    }

    public void updateUser(String id, String newUsername, String newPassword) {

        ObjectId objectId = new ObjectId(id);

        JSONObject updateQuery = new JSONObject();
        updateQuery.put(Constants.USERNAME, newUsername);
        updateQuery.put(Constants.PASSWORD, newPassword);

        String query = updateQuery.toString();

        this.usersCollection.update(objectId).with(query);
    }

    public void updateUserPassword(String id, String newPassword) {

        ObjectId objectId = new ObjectId(id);

        JSONObject updateQuery = new JSONObject();
        updateQuery.put(Constants.PASSWORD, newPassword);

        String query = updateQuery.toString();

        this.usersCollection.update(objectId).with(query);
    }

    public void deleteUser(User user) {

        String id = user.getId();

        this.deleteUser(id);
    }

    public void deleteUser(String id) {

        ObjectId objectId = new ObjectId(id);

        this.usersCollection.remove(objectId);
    }

    private User encryptUserPassword(User user) {

        String password = user.getPassword();

        String encodedPassword = DigestUtils.md5Hex(password);

        user.setPassword(encodedPassword);

        return user;
    }
}
