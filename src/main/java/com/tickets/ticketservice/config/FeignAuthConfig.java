package com.tickets.ticketservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor forwardBearerToken() {

        return requestTemplate -> {

            var attributes = RequestContextHolder.getRequestAttributes();

            if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
                return;
            }

            String authorization =
                    servletAttributes.getRequest()
                            .getHeader("Authorization");

            if (authorization != null &&
                    authorization.startsWith("Bearer ")) {

                requestTemplate.header(
                        "Authorization",
                        authorization
                );
            }
        };
    }
}