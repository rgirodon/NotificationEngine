package org.notificationengine.authentication.mongodb;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.json.simple.JSONObject;
import org.notificationengine.authentication.TokenService;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.Token;
import org.notificationengine.persistance.MongoDbSettings;
import org.notificationengine.persistance.MongoDbUtils;
import org.notificationengine.spring.SpringUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MongoTokenService implements TokenService{

    private static Logger LOGGER = Logger.getLogger(MongoAuthenticator.class);

    private Boolean modeTest;

    private MongoDbSettings mongoDbSettings;

    private MongoCollection tokensCollection;

    public MongoTokenService() {

        this(Boolean.FALSE);
    }

    public MongoTokenService(Boolean modeTest) {

        LOGGER.debug("MongoTokenService instantiated");

        this.modeTest = modeTest;

        this.mongoDbSettings = (MongoDbSettings) SpringUtils.getBean(Constants.MONGODB_SETTINGS);

        this.init();
    }

    public MongoTokenService(Boolean modeTest, MongoDbSettings mongoDbSettings) {

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

            this.tokensCollection = jongo.getCollection(Constants.TOKENS_COLLECTION);
        }
        catch (UnknownHostException e) {

            LOGGER.error(ExceptionUtils.getFullStackTrace(e));

            LOGGER.error("Unable to build MongoAuthenticator");
        }
    }

    @Override
    public Token saveToken(Token token) {

        LOGGER.debug("Insert token " + token.getToken());

        this.tokensCollection.insert(token);

        return token;
    }

    @Override
    public Boolean checkToken(String token) {

        //Look for the token in the database
        JSONObject tokenQuery = new JSONObject();
        tokenQuery.put(Constants.TOKEN, token);

        String query = tokenQuery.toString();

        Token tokenFound = this.tokensCollection.findOne(query).as(Token.class);

        //If the token isn't found or is empty, the token is not valid
        if (tokenFound == null || StringUtils.isEmpty(tokenFound.getToken())) {
            return Boolean.FALSE;
        }

        //Check if the token is still valid
        Boolean result = tokenFound.isStillValid();

        //If it isn't valid, delete it
        if (!result) {
            this.deleteToken(token);
        }

        return result;
    }

    @Override
    public Collection<Token> getTokens() {

        Iterable<Token> tokensIterable = this.tokensCollection.find().as(Token.class);

        Collection<Token> tokens = new ArrayList<>();

        for (Token token : tokensIterable) {
            tokens.add(token);
        }

        return tokens;
    }

    @Override
    public void deleteToken(String token) {

        //Look for the token in the database
        JSONObject tokenQuery = new JSONObject();
        tokenQuery.put(Constants.TOKEN, token);

        String query = tokenQuery.toString();

        this.tokensCollection.remove(query);

    }

    @Override
    public void updateTokenLife(String token) {

        //Look for the token in the database
        JSONObject tokenQuery = new JSONObject();
        tokenQuery.put(Constants.TOKEN, token);

        String query = tokenQuery.toString();

        Token tokenToUpdate = this.tokensCollection.findOne(query).as(Token.class);

        DateTime endOfLifeTime = new DateTime(tokenToUpdate.getEndOfLife().getTime());

        DateTime newEndOfLife = new DateTime().plusMinutes(30);

        //If the previous end of life is later than in 30 minutes, we do nothing
        //Otherwise, do the update.
        if (endOfLifeTime.isBefore(newEndOfLife)) {

            tokenToUpdate.setEndOfLife(newEndOfLife.toDate());

            this.tokensCollection.update(query).with(tokenToUpdate);
        }
    }
}
