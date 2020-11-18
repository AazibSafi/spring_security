package com.spring.security.security.filter;

import com.spring.security.security.JwtTokenProvider;
import com.spring.security.util.Helper;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Order(1)
@Component
public class AuthenticationFilter implements Filter {

    private JwtTokenProvider tokenProvider;

    public AuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String jwt = tokenProvider.getJwtFromRequest(httpRequest);
        if (Helper.isNotEmptyAndNull(jwt)) {
            if (tokenProvider.validateToken(jwt)) {
                tokenProvider.setUserAuthentication(jwt);
            }
            else {
                log.error("Could not set user authentication in security context");
                HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(httpResponse);
                wrapper.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Un-Authorized or token expired");
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
