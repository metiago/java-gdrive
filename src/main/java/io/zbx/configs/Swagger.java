package io.zbx.configs;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class Swagger {

    public static final String AUTHORIZATION_HEADER = "Bearer";

    public static final String DEFAULT_INCLUDE_PATTERN = "/.*";

    private static final String SETTINGS_PKG = "io.zbx.endpoints.files";

    @Bean
    public Docket swaggerSettingsEndpoint() {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("files")
                .select()
                .apis(RequestHandlerSelectors.basePackage(SETTINGS_PKG))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()))
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Mr. Souza", "https://metiago.github.io", "tiagotg.ribeiro@gmail.com");
        ApiInfo apiInfo = new ApiInfo("ZBX2", "This is the ZBX2 API",
                "0.1.0", "Terms of service",
                contact,
                "Apache 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
        return apiInfo;
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("Bearer", authorizationScopes));
    }

}
