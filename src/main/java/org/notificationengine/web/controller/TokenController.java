package org.notificationengine.web.controller;


import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.notificationengine.authentication.TokenService;
import org.notificationengine.constants.Constants;
import org.notificationengine.domain.Token;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller(Constants.TOKEN_CONTROLLER)
public class TokenController {

    private TokenService tokenService;

    @RequestMapping(value = "/requestToken.do", method = RequestMethod.GET)
    @ResponseBody
    public String createStaticToken() {

        DateTime endOfLife = new DateTime().plusYears(200);

        Token staticToken = new Token();

        staticToken.setEndOfLife(endOfLife.toDate());

        this.tokenService.saveToken(staticToken);

        return staticToken.getToken();
    }

    @RequestMapping(value = "/token.do", method = RequestMethod.DELETE, params = {"token"})
    @ResponseStatus(HttpStatus.OK)
    public void deleteToken(@RequestParam("token") String token) {

        this.tokenService.deleteToken(token);
    }

    @RequestMapping(value = "/tokens.do", method = RequestMethod.GET)
    @ResponseBody
    public String getTokens() {

        Gson response = new Gson();

        Collection<Token> tokens = this.tokenService.getTokens();

        return response.toJson(tokens).toString();

    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
