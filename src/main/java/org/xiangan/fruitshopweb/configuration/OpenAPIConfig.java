package org.xiangan.fruitshopweb.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openApiConfig(){
        return new OpenAPI()
            .info(
                new Info()
                        .title("XiangAnFruit Api")
                        .version("1.0")
                        .description("""
                            <pre>
                            api Demo 順序為: Consignor -> Product -> Purchase -> Wastage
                                            Revenue、Miscellaneous
                            </pre>
                            """)
                        .contact(
                            new Contact()
                                .name("kyle")
                                .email("xxx123@gmail.com")
                        )
            )
	        .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
	        .schemaRequirement("JWT Authentication",
                    new SecurityScheme()
                        .name("JWT Authentication")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
            );
    }
}
