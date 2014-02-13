package org.notificationengine.cleaner;


import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.notificationengine.authentication.TokenService;
import org.notificationengine.domain.Token;

import java.util.ArrayList;
import java.util.Collection;

public class TokenCleaner {

    private static Logger LOGGER = Logger.getLogger(TokenCleaner.class);

    private TokenService tokenService;

    public TokenCleaner(TokenService tokenService) {

        LOGGER.debug("Instatiates a new TokenCleaner");

        this.setTokenService(tokenService);
    }

    public void process() {

        LOGGER.debug("Process tokenClean");

        Collection<Token> tokens = this.tokenService.getTokens();

        Collection<Token> invalidTokens = new ArrayList<>();

        //Retrieve all tokens that have a non more valid date
        for (Token token : tokens) {

            DateTime endOfLife = new DateTime(token.getEndOfLife());

            if (endOfLife.isBeforeNow()) {
                invalidTokens.add(token);
            }
        }

        //delete all those invalid tokens in DB
        for (Token invalidToken : invalidTokens) {

            LOGGER.debug("Delete token " + invalidToken.getToken());

            this.tokenService.deleteToken(invalidToken.getToken());
        }
    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
