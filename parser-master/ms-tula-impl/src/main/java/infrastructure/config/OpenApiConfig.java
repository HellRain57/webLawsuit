package infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "security.enabled", havingValue = "true")
@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

  @Bean
  public OpenApiCustomiser openApiCustomiser() {
    String securitySchemeName = "Bearer Authentication";
    return openApi -> {
      Components components = openApi.getComponents();
      openApi
          .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
          .components(components
              .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                  .name(securitySchemeName)
                  .type(Type.HTTP)
                  .scheme("bearer")
                  .bearerFormat("JWT")));
    };
  }
}
