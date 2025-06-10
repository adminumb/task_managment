package com.example.task_service.task_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*Access to user interface Swagger
http://localhost:8081/swagger-ui/index.html
* */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "REST API InTaskquiry Service",
                version = "1.0",
                description = """
                        Task Service application.
                        """,
                contact = @Contact(
                        name = "Bogdanovich Pavel",
                        email = "sega0172@gmail.com"
                )
        ),
        servers = {
                @Server(url = "/task-service", description = "Gateway Server")
        }
)

public class OpenAPIConfiguration {
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("REST API")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder()
                .group("Actuator")
                .pathsToMatch("/actuator/**")
                .build();
    }
}
