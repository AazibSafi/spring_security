package com.spring.security.security.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/*
 *  Aazib Safi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationRequest implements Serializable {

    private String username;
    private String email;

    @JsonProperty("password")
    @NotNull(message="Password is a required")
    private String password;

    public AuthenticationRequest(String username, @NotNull(message = "Password is a required") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
