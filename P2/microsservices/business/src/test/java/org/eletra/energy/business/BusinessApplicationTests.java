package org.eletra.energy.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.eletra.energy.business.configs.TestcontainersConfig.artemisContainer;
import static org.eletra.energy.business.configs.TestcontainersConfig.postgresContainer;

@SpringBootTest
class BusinessApplicationTests {

    @DynamicPropertySource
    static void artemisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.artemis.broker-url", artemisContainer::getBrokerUrl);
        registry.add("spring.artemis.user", artemisContainer::getUser);
        registry.add("spring.artemis.password", artemisContainer::getPassword);
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @BeforeAll
    public static void beforeAll() {
        artemisContainer.start();
        postgresContainer.start();
    }

    @Test
    void contextLoads() {

    }
}
