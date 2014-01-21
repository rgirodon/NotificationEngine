package org.notificationengine.domain;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

public class Token {

    private String id;

    private String token;

    private Date creationDate;

    private Date endOfLife;

    public Token() {

        SecureRandom random = new SecureRandom();

        this.token = new BigInteger(130, random).toString(16);

        this.creationDate = new Date();

        this.endOfLife = new DateTime().plusMinutes(30).toDate();
    }

    public Boolean isStillValid() {

        long milliSec = this.endOfLife.getTime();

        DateTime endOfLifeTime = new DateTime(milliSec);

        return endOfLifeTime.isAfterNow();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getEndOfLife() {
        return endOfLife;
    }

    public void setEndOfLife(Date endOfLife) {
        this.endOfLife = endOfLife;
    }
}
