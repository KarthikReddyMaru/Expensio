package com.cashigo.expensio.common.documentation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.servers.Server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@OpenAPIDefinition(
        info = @Info(
                title = "Cashigo",
                description = "A money managing app",
                version = "1.1.0",
                contact = @Contact(
                        name = "Beast Boy",
                        email = "karthikreddy.maru@gmail.com"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:9000/expensio",
                        description = "Local Expensio"
                ),
                @Server(
                        url = "http://localhost:9000",
                        description = "Local Gateway"
                )
        },
        security = @SecurityRequirement(name = "OAuth2")
)
@SecurityScheme(
        name = "OAuth2",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow (
                        authorizationUrl = "http://localhost:8000/realms/Cashigo/protocol/openid-connect/auth",
                        tokenUrl = "http://localhost:8000/realms/Cashigo/protocol/openid-connect/token",
                        scopes = {
                                @OAuthScope(name = "openid"), @OAuthScope(name = "email"), @OAuthScope(name = "profile")
                        }
                )
        )
)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AppDoc {
}
