package id.perumdamts.kepegawaian.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Configuration
@EnableWebMvc
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                                .addSecuritySchemes("BearerAuthentication",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT"))
                )
                .security(List.of(
                        new SecurityRequirement()
                                .addList("BearerAuthentication")
                ))
                .info(new Info()
                        .title("Kepegawaian Rest API")
                        .description("Need JWT Token for execute this API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Kent-Os")
                                .email("kentoes.pdam@gmail.com")
                        )
                        .license(new License()
                                .name("License of API")
                                .url("API license URL")
                        )
                );
    }

    @Bean
    public GroupedOpenApi masterApi() {
        return GroupedOpenApi.builder()
                .group("master")
                .pathsToMatch("/master/**")
                .build();
    }

    @Bean
    public GroupedOpenApi profileApi() {
        return GroupedOpenApi.builder()
                .group("profil")
                .pathsToMatch("/profil/**")
                .build();
    }

    @Bean
    public GroupedOpenApi pegawaiApi() {
        return GroupedOpenApi.builder()
                .group("pegawai")
                .pathsToMatch("/pegawai/**")
                .build();
    }

    @Bean
    public GroupedOpenApi kepegawaianApi() {
        return GroupedOpenApi.builder()
                .group("kepegawaian")
                .pathsToMatch("/kepegawaian/**")
                .build();
    }

    @Bean
    public GroupedOpenApi penggajianApi() {
        return GroupedOpenApi.builder()
                .group("penggajian")
                .pathsToMatch("/penggajian/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("System")
                .pathsToMatch("/system/**")
                .build();
    }

    @Bean
    public GroupedOpenApi laporanKepegawaianApi() {
        return GroupedOpenApi.builder()
                .group("Laporan Kepegawaian")
                .pathsToMatch("/laporan/kepegawaian/**")
                .build();
    }
}
