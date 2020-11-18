package com.bns.platotrain.security.jwt;

import com.bns.platotrain.security.SecurityConstants;
import com.bns.platotrain.security.TokenBasedAuthentication;
import com.bns.platotrain.security.UserPrincipal;
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
import org.springframework.security.core.Authentication;
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

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setClaims(createClaimsMap((UserPrincipal) authentication.getPrincipal())).setSubject(authentication.getName()).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryDate.getTime() * 1000))
                .signWith(SignatureAlgorithm.HS512, TextCodec.BASE64.encode(jwtSecret))
                .compact();
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
        claimMap.put(SecurityConstants.USER_NAME, userPrincipal.getUsername());
        final String authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claimMap.put(SecurityConstants.ROLES, authorities);
        return claimMap;
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
     * get Claims (payload) from the token.
     * @param token
     * @return claims
     */
    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(TextCodec.BASE64.encode(jwtSecret))
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (Exception ex) {
            log.error(String.format("Error occurred when getting the claims from token'%s'.", ex.getMessage()));
            claims = null;
        }
        return claims;
    }

    /**
     * Getting the token from Authentication header.
     * e.g Bearer your_token
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    /**
     * get user details/user principal object
     * @param claims
     * @return UserPrincipal
     */
    private UserPrincipal getUserPrincipalFromClaims(Claims claims) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(SecurityConstants.ROLES).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        Long userId = Long.valueOf(claims.get(SecurityConstants.USER_ID).toString());
        String userName = String.valueOf(claims.get(SecurityConstants.USER_NAME));
        return new UserPrincipal(userId, userName, authorities);
    }

    /**
     * set the user authentication context in spring security context.
     * @param authToken
     */
    public void setUserAuthentication(String authToken) {
        try {
            Claims claims = getAllClaimsFromToken(authToken);
            log.info("Token Validating");
            UserDetails userDetails = getUserPrincipalFromClaims(claims);
            // set user authentication in spring security context
            TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
            authentication.setToken(authToken);
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception ex) {
            log.error(String.format("Error occurred when setting the auth object into context'%s'.", ex.getMessage()));
        }
    }

}
