package io.zbx.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableTransactionManagement
@ComponentScan({"io.zbx.*"})
//@EnableJpaRepositories("io.github.metiago.*")
//@EntityScan("io.github.metiago.domain")
public class AppConfig implements WebMvcConfigurer {

}
