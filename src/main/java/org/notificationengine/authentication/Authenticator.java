package org.notificationengine.authentication;


import org.notificationengine.domain.Token;
import org.notificationengine.domain.User;

public interface Authenticator {

    public Boolean isAdmin(User user);

    public Boolean isAdmin(String username, String encryptedPassword);

    public void saveUser(String username, String password);

    public void saveUser(User user);

    public void updateUser(User user);

    public void updateUser(String id, String newUsername, String newPassword);

    public void updateUserPassword(String id, String newPassword);

    public void deleteUser(User user);

    public void deleteUser(String id);
}
