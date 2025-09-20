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

    public Optional<String> getUserId() {
        Jwt jwt = getAuthenticationToken();
        return Optional.ofNullable(jwt)
                .map(JwtClaimAccessor::getSubject);
    }

    public Optional<String> getUserName() {
        Jwt jwt = getAuthenticationToken();
        return Optional.ofNullable(jwt)
                .map(token -> token.getClaim("preferred_username"));
    }

    public Jwt getAuthenticationToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)
            return jwtAuthenticationToken.getToken();
        return null;
    }

}
