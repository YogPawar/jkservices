package com.jkservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
  @Bean
  public OpenAPI jkservicesOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("JKServices API")
                .description(
                    "REST API's for JKServices business and agricultural work management platform.")
                .version("v1"));
  }
}
