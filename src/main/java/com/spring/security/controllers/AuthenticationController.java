package com.spring.security.controllers;

import com.spring.security.security.client.AuthenticationRequest;
import com.spring.security.security.client.AuthenticationResponse;
import com.spring.security.security.constants.SecurityConstants;
import com.spring.security.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/login")
    public AuthenticationResponse authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            return authenticationService.authenticateUser(authenticationRequest);
        }
        catch (BadCredentialsException ex) {
            log.error(String.format("Authentication failed '%s'.", ex.getMessage()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SecurityConstants.BAD_CREDENTIALS_UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/logout")
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        authenticationService.logoutUser(httpServletRequest, httpServletResponse, authentication);
    }

    @GetMapping("/refreshToken")
    public AuthenticationResponse refreshToken(HttpServletRequest httpRequest) {
        try {
            return authenticationService.refreshToken(httpRequest);
        }
        catch (BadCredentialsException ex) {
            log.error(String.format("Refresh Token failed '%s'.", ex.getMessage()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SecurityConstants.BAD_CREDENTIALS_UNAUTHORIZED);
        }
    }

}
