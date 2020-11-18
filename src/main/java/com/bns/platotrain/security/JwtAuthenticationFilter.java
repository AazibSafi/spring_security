package com.bns.platotrain.security;

import com.bns.platotrain.security.jwt.JwtTokenProvider;
import com.bns.platotrain.util.Helper;
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
import java.util.Map;

@Slf4j
@Component
@Order(1)
public class JwtAuthenticationFilter implements Filter {

    private JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        ExtendedServletRequestWrapper extendedServletRequestWrapper =
                new ExtendedServletRequestWrapper(httpRequest);
        String jwt = tokenProvider.getJwtFromRequest(httpRequest);
        if (Helper.isNotEmptyAndNull(jwt)) {
            if (tokenProvider.validateToken(jwt)) {
                tokenProvider.setUserAuthentication(jwt);
                Map<String, Object> userContextMap = tokenProvider.getUserContextFromToken(jwt);
                Helper.addHeaderValues(extendedServletRequestWrapper, userContextMap);
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
