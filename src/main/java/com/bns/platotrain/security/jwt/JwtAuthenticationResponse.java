package com.bns.platotrain.security.jwt;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private String userName;
    private Collection<? extends GrantedAuthority> roles;

    public JwtAuthenticationResponse(String accessToken, String userName, Collection<? extends GrantedAuthority> roles) {
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
