package org.notificationengine.authentication;

import org.notificationengine.domain.Token;

import java.util.Collection;

public interface TokenService {

    public Token saveToken(Token token);

    public Boolean checkToken(String token);

    public Collection<Token> getTokens();

    public void deleteToken(String token);

    public void updateTokenLife(String token);
}
