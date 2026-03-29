package com.dealers.inventory.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI inventoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dealer & Vehicle Inventory API")
                        .version("1.0")
                        .description(
                                """
                                Modular monolith inventory module. Tenant isolation is enforced via the \
                                **X-Tenant-Id** header on `/dealers` and `/vehicles`.

                                **Demo credentials**
                                - Tenant APIs: HTTP Basic user `tenant_user` / `password`
                                - Admin API: HTTP Basic user `global_admin` / `password`"""))
                .components(new Components()
                        .addSecuritySchemes(
                                "basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic"))
                        .addSecuritySchemes(
                                "tenantHeader",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Tenant-Id")));
    }
}
