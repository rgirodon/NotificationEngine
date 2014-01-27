package org.notificationengine.authentication;

import org.notificationengine.domain.User;

public interface Authenticator {

    public Boolean isAdmin(User user);

    public Boolean isAdmin(String username, String encryptedPassword);
}
