package org.notificationengine.web.interceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notificationengine.authentication.TokenService;
import org.notificationengine.authentication.mongodb.MongoTokenService;
import org.notificationengine.constants.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component(Constants.TOKEN_INTERCEPTOR)
public class TokenInterceptor extends HandlerInterceptorAdapter {

    private static Logger LOGGER = Logger.getLogger(TokenInterceptor.class);

    private TokenService tokenService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("token");

        //TODO : see how to refactor this
        if (this.tokenService == null) {
            LOGGER.debug("TokenService was null, had to instantiate one");
            this.tokenService = new MongoTokenService();
        }

        if (!this.tokenService.checkToken(token)) {
            response.sendError(403, "Permission denied");
            return false;
        }

        this.tokenService.updateTokenLife(token);

        return true;
    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public void setTokenService(TokenService tokenService) {
        LOGGER.debug("Set token service in Token Interceptor");
        this.tokenService = tokenService;
    }
}
