package org.xiangan.fruitshopweb.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 設定。
 * <p>
 * 負責設定 OpenAPI 規範，讓應用程式能夠產生 API 文件（Swagger UI）。
 * 包含 API 的基本資訊、聯絡方式，以及 JWT 身份驗證的安全性設定。
 * </p>
 *
 * <h2>設定內容：</h2>
 * <ul>
 *   <li>API 標題、版本、描述</li>
 *   <li>聯絡人資訊</li>
 *   <li>Bearer Token（JWT）身份驗證</li>
 *   <li>API 存取權限</li>
 * </ul>
 *
 * <h3>安全性設定：</h3>
 * 採用 JWT 作為身份驗證機制，透過 `BearerAuth` 保護 API 端點。<br>
 * 註冊 `SecurityScheme` 並應用於 `SecurityRequirement`。
 *
 * @author kyle
 */
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
	        .addSecurityItem(
		        new SecurityRequirement().addList("BearerAuth")
	        )
	        .components(new Components()  // 使用 Components 註冊 SecurityScheme
		        .addSecuritySchemes("BearerAuth",
			        new SecurityScheme()
				        .name("BearerAuth") // 與上面 SecurityRequirement 一致
				        .type(SecurityScheme.Type.HTTP)
				        .scheme("bearer")
				        .bearerFormat("JWT")
		        )
	        );
    }
}
