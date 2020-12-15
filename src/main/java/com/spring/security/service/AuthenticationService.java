package com.spring.security.service;

import com.spring.security.security.JwtTokenProvider;
import com.spring.security.security.UserPrincipal;
import com.spring.security.security.client.AuthenticationRequest;
import com.spring.security.security.client.AuthenticationResponse;
import com.spring.security.util.Helper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
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
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
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

    public AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String usernameOrEmail = Helper.getOrDefault(authenticationRequest.getUsername(),
                authenticationRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(usernameOrEmail, authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        log.info("User Authenticated");
        return new AuthenticationResponse(tokenProvider.generateToken(userPrincipal), userPrincipal.getUsername(), authentication.getAuthorities());
    }

    public void logoutUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        logoutSuccessHandler.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.clearContext();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest httpRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String token = tokenProvider.getJwtFromRequest(httpRequest);
        Claims claims = tokenProvider.getAllClaimsFromToken(token);
        UserPrincipal userPrincipal = tokenProvider.getUserPrincipalFromClaims(claims);
        String newToken = tokenProvider.generateToken(userPrincipal);
        return new AuthenticationResponse(newToken, userPrincipal.getUsername(), userPrincipal.getAuthorities());
    }

}
