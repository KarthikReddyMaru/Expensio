package com.cashigo.expensio.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserContext {

    public static String getUserId() {
        Jwt jwt = getAuthenticationToken();
        return Optional.ofNullable(jwt)
                .map(JwtClaimAccessor::getSubject).orElse("Anonymous");
    }

    public static Optional<String> getUserName() {
        Jwt jwt = getAuthenticationToken();
        return Optional.ofNullable(jwt)
                .map(token -> token.getClaim("preferred_username"));
    }

    public static Jwt getAuthenticationToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)
            return jwtAuthenticationToken.getToken();
        return null;
    }

}
