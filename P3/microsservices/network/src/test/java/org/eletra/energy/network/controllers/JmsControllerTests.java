package org.eletra.energy.network.controllers;

import org.eletra.energy.network.services.CsvFtpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.eletra.energy.network.configs.TestcontainersConfig.artemisContainer;
import static org.eletra.energy.network.configs.TestcontainersConfig.postgresContainer;
import static org.eletra.energy.network.configs.TestcontainersConfig.ftpContainer;


@SpringBootTest
public class JmsControllerTests {

    @Autowired
    private JmsController jmsController;

    @MockitoSpyBean
    private CsvFtpService csvFtpService;

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
    public void test1() throws Exception {
        // Given
        String message = """
                user,time,message
                "d1a1b3ca-0884-4701-a219-6ada5c638812","2026-02-02 12:34:21","Until I was 25 I thought the only response to ‘I love you’ was ‘Oh crap!'"
                """;

        // When
        Assertions.assertDoesNotThrow(() -> {
           jmsController.receiveCsv(message);
        });

        // Then
        Mockito.verify(csvFtpService, Mockito.times(1)).execute(message);
    }
}
