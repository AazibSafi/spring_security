package com.spring.security.security;

import com.spring.security.security.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.TextCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.spring.security.security.constants.SecurityConstants.TOKEN_HEADER;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationIn}")
    private int jwtExpirationIn;

    public String generateToken(UserPrincipal userPrincipal) {
        String token = Jwts.builder()
                .setClaims(createClaimsMap(userPrincipal))
                .setSubject(userPrincipal.getUsername()).setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationIn * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
        log.debug("New Token Generated: "+token);
        return token;
    }


    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(TextCodec.BASE64.encode(jwtSecret)).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    /**
     * creating claims metadata when generating the token
     * @param userPrincipal
     * @return
     */
    private Map<String, Object> createClaimsMap(UserPrincipal userPrincipal) {
        Map<String, Object> claimMap = new HashMap<>();
        claimMap.put(SecurityConstants.USER_ID, userPrincipal.id());
        claimMap.put(SecurityConstants.USER_EMAIL, userPrincipal.email());
        claimMap.put(SecurityConstants.USER_NAME, userPrincipal.getUsername());
        final String authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claimMap.put(SecurityConstants.ROLES, authorities);
        return claimMap;
    }

    /**
     * set the user authentication context in spring security context.
     * @param authToken
     */
    public void setUserAuthentication(String authToken) {
        try {
            Claims claims = getAllClaimsFromToken(authToken);
            UserDetails userDetails = getUserPrincipalFromClaims(claims);
            // set user authentication in spring security context
            setUserAuthentication(authToken, userDetails);
        }
        catch (Exception ex) {
            log.error(String.format("Error occurred when setting the auth object into context '%s'.", ex.getMessage()));
        }
    }

    public void setUserAuthentication(String authToken, UserDetails userDetails) {
        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
        authentication.setToken(authToken);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * get Claims (payload) from the token.
     * @param token
     * @return claims
     */
    public Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        }
        catch (Exception ex) {
            log.error(String.format("Error occurred when getting the claims from token'%s'.", ex.getMessage()));
            claims = null;
        }
        return claims;
    }

    /**
     * getting context map from claims
     * @param token
     * @return
     */
    public Map<String, Object> getUserContextFromToken(String token) {
        Map<String, Object> map = new HashMap<>();
        Claims claims = getAllClaimsFromToken(token);
        if(Objects.isNull(claims)) {
            throw new UsernameNotFoundException("Error occurred when getting the claims from token");
        }
        map.put(SecurityConstants.USER_ID, claims.get(SecurityConstants.USER_ID));
        map.put(SecurityConstants.USER_NAME, claims.get(SecurityConstants.USER_NAME));
        map.put(SecurityConstants.ROLES, claims.get(SecurityConstants.ROLES));
        return map;
    }

    /**
     * get user details/user principal object
     * @param claims
     * @return UserPrincipal
     */
    public UserPrincipal getUserPrincipalFromClaims(Claims claims) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(SecurityConstants.ROLES).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        Long userId = Long.valueOf(claims.get(SecurityConstants.USER_ID).toString());
        String userEmail = String.valueOf(claims.get(SecurityConstants.USER_EMAIL));
        String userName = String.valueOf(claims.get(SecurityConstants.USER_NAME));
        return new UserPrincipal(userId, userName, userEmail, authorities);
    }

    /**
     * Getting the token from Authentication header.
     * e.g Bearer your_token
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.TOKEN_TYPE+" ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
