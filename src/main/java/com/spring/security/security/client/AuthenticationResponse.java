package com.spring.security.security.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.security.security.constants.SecurityConstants;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/*
 *  Aazib Safi
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {

    private String accessToken;
    private String tokenType;
    private String userName;
    private Collection<? extends GrantedAuthority> roles;
    
    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String accessToken, String userName, Collection<? extends GrantedAuthority> roles) {
        this.tokenType = SecurityConstants.TOKEN_PREFIX;
        this.accessToken = accessToken;
        this.userName = userName;
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Collection<? extends GrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(Collection<? extends GrantedAuthority> roles) {
        this.roles = roles;
    }

}
