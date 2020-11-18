package com.spring.security.security.filter;

import com.spring.security.security.ExtendedHttpRequest;
import com.spring.security.security.JwtTokenProvider;
import com.spring.security.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Map;

@Order(2)
@Component
public class UserContextFilter implements Filter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        ExtendedHttpRequest extendedHttpRequest =
                new ExtendedHttpRequest(httpRequest);
        String jwt = tokenProvider.getJwtFromRequest(httpRequest);
        if (Helper.isNotEmptyAndNull(jwt)) {
            if (tokenProvider.validateToken(jwt)) {
                Map<String, Object> userContextMap = tokenProvider.getUserContextFromToken(jwt);
                Helper.addHeaderValues(extendedHttpRequest, userContextMap);
            }
            else {
                HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(httpResponse);
                wrapper.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Un-Authorized or token expired");
                return;
            }
        }
        filterChain.doFilter(extendedHttpRequest, httpResponse);
    }

}
