package org.eletra.energy.network;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.eletra.energy.network.configs.TestcontainersConfig.*;
import static org.eletra.energy.network.configs.TestcontainersConfig.ftpContainer;

@SpringBootTest
class NetworkApplicationTest {

    @DynamicPropertySource
    static void ftpProperties(DynamicPropertyRegistry registry) {
        registry.add("test.ftp.host", ftpContainer::getHost);
        registry.add("test.ftp.port", () -> ftpContainer.getMappedPort(21));
        registry.add("test.ftp.user", () -> "test");
        registry.add("test.ftp.pass", () -> "test");
        registry.add("test.ftp.clientMode", () -> 2);
    }

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
        ftpContainer.start();
    }

    @Test
    void contextLoads() {
    }
}
