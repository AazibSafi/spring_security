package com.bns.platotrain.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

public class TokenBasedAuthentication extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;
    private final UserDetails principle;
    private String token;

    public TokenBasedAuthentication(UserDetails principle) {
        super(principle.getAuthorities());
        this.principle = principle;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public UserDetails getPrincipal() {
        return principle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TokenBasedAuthentication that = (TokenBasedAuthentication) o;
        return Objects.equals(getToken(), that.getToken()) &&
                Objects.equals(principle, that.principle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getToken(), principle);
    }

}
