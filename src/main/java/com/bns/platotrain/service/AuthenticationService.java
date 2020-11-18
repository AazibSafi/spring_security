package com.bns.platotrain.service;

import com.bns.platotrain.dto.AuthenticationRequest;
import com.bns.platotrain.security.UserPrincipal;
import com.bns.platotrain.security.jwt.JwtAuthenticationResponse;
import com.bns.platotrain.security.jwt.JwtTokenProvider;
import com.bns.platotrain.util.Helper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class AuthenticationService {

    @Qualifier(value = "logoutSuccess")
    private LogoutSuccessHandler logoutSuccessHandler;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;

    public AuthenticationService(LogoutSuccessHandler logoutSuccessHandler, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public JwtAuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) {
        String usernameOrEmail = Helper.getOrDefault(authenticationRequest.getUsername(),
                authenticationRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(usernameOrEmail, authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return new JwtAuthenticationResponse(tokenProvider.generateToken(authentication), userPrincipal.getUsername(), authentication.getAuthorities());
    }

    public void logoutUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        logoutSuccessHandler.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.clearContext();
    }

}
