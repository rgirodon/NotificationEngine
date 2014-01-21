package org.notificationengine.domain;

import org.apache.commons.codec.digest.DigestUtils;

public class User {

    private String id;

    private String username;

    private String password;

    public User() {
        super();
    }

    public User(String username, String password) {
        this.username = username;

        //Encrypt password before save
        String encodedPassword = DigestUtils.md5Hex(password);

        this.password = encodedPassword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {

        //Encrypt password before save
        String encodedPassword = DigestUtils.md5Hex(password);

        this.password = encodedPassword;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id='").append(id).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
