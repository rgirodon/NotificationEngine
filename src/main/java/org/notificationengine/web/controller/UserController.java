package org.notificationengine.web.controller;

import org.apache.log4j.Logger;
import org.notificationengine.authentication.Authenticator;
import org.notificationengine.authentication.TokenService;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.Token;
import org.notificationengine.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller(Constants.USER_CONTROLLER)
public class UserController {

    private static Logger LOGGER = Logger.getLogger(UserController.class);

    private Authenticator authenticator;

    private TokenService tokenService;

    public UserController() {
        LOGGER.debug("UserController instantiated");
    }

    @RequestMapping(value = "/login.do", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String doLogin(@RequestBody User user, HttpServletResponse response) throws IOException {

        String username = user.getUsername();
        String password = user.getPassword();

        if (!this.authenticator.isAdmin(username, password)) {
            response.sendError(403, "Access denied");
            return "";
        }

        return this.createToken();
    }

    private String createToken() {
        Token token = new Token();

        this.tokenService.saveToken(token);

        return token.getToken();
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}

