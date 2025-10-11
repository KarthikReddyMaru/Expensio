package com.cashigo.expensio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${gateway.base.uri}")
    private String baseUri;

    @Bean
    OpenAPI transactionBaseUri() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(getInfo());
        openAPI.servers(List.of(getServer()));
        return openAPI;
    }

    private Server getServer() {
        Server server = new Server();
        server.setUrl(
                UriComponentsBuilder.fromUriString(baseUri)
                        .path("/expensio")
                        .toUriString()
        );
        return server;
    }

    private Info getInfo() {
        Info info = new Info();
        info.setTitle("Cashigo APIs");
        info.setVersion("1.1.0");
        return info;
    }

}
