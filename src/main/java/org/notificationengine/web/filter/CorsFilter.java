package org.notificationengine.web.filter;


import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        response.addHeader("Access-Control-Allow-Origin", "*");

        if(request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {

            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");

            response.addHeader("Access-Control-Allow-Headers", "Content-Type, token");

            response.addHeader("Access-Control-Max-Age", "3600");
        }

        filterChain.doFilter(request, response);

    }
}
