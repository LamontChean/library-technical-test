package org.library.test.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal Spring Boot configuration for @WebMvcTest.
 * This provides @SpringBootConfiguration without JPA dependencies.
 */
@SpringBootApplication(
    scanBasePackages = {"org.library.web", "org.library.web.response"},
    exclude = {
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
        org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration.class
    }
)
public class TestApplication {
}
