package org.eletra.energy.business.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.eletra.energy.business.configs.TestcontainersConfig.artemisContainer;
import static org.eletra.energy.business.configs.TestcontainersConfig.postgresContainer;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JsonFormatServiceTests {

    @Autowired
    private JsonFormatService jsonFormatService;

    @MockitoSpyBean
    private JmsTemplate jmsTemplate;

    @MockitoBean
    private Clock clock;

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

    @Test
    public void jsonShouldBeConverted() {
        // Given
        Instant fixedInstant = Instant.parse("2026-01-27T12:05:34Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        String message = """
                {
                    "user":
                        {
                            "id":"b16404b4-f690-44dc-8db0-8f48ec568590",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"01-27-2026T12:05:04.001Z",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

        // When
        Assertions.assertDoesNotThrow(() -> {
            jsonFormatService.execute(message);
        });

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(
                Mockito.eq("training-converter.send_as_json"),
                messageCaptor.capture());

        final String result = messageCaptor.getValue();

        assertEquals("{\"username\":\"b16404b4-f690-44dc-8db0-8f48ec568590\",\"createdAt\":\"2026-01-27 12:05:34\",\"sentAt\":\"2026-01-27 12:05:04\",\"message\":\"No. Interestingly enough, her leaf blower picked up.\"}", result, "Expected a JSON conversion result");
    }

    @Test
    public void convertShouldThrowExceptionOnIdMissing() {
        // Given
        String message = """
                {
                    "user":
                        {
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"01-27-2026T12:05:04.001Z",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid user ID in received message"), "Expected an invalid user ID exception");
    }

    @Test
    public void convertShouldThrowExceptionOnIdEmpty() {
        // Given
        String message = """
                {
                    "user":
                        {
                            "id":"",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"01-27-2026T12:05:04.001Z",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid user ID in received message"), "Expected an invalid user ID exception");
    }

    @Test
    public void convertShouldThrowExceptionOnSentAtMissing() {
        // Given
        String message = """
                {
                    "user":
                        {
                            "id":"b16404b4-f690-44dc-8db0-8f48ec568590",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid sentAt in received message log"), "Expected an invalid sentAt exception");
    }

    @Test
    public void convertShouldThrowExceptionOnSentAtEmpty() {
        // Given
        String message = """
                {
                    "user":
                        {
                            "id":"b16404b4-f690-44dc-8db0-8f48ec568590",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid sentAt in received message log"), "Expected an invalid sentAt exception");
    }

    @Test
    public void convertShouldThrowExceptionOnMessageMissing() {
        // Given
        String message = """
                {
                    "user":
                        {
                            "id":"b16404b4-f690-44dc-8db0-8f48ec568590",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"01-27-2026T12:05:04.001Z",
                            "format":null
                        }
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid message content in received message log"), "Expected an invalid message content exception");
    }

    @Test
    public void convertShouldThrowExceptionOnMessageEmpty() {
        // Given
        String message = """
                {
                    "user":
                        {
                            "id":"b16404b4-f690-44dc-8db0-8f48ec568590",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"01-27-2026T12:05:04.001Z",
                            "message":"",
                            "format":null
                        }
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid message content in received message log"), "Expected an invalid message content exception");
    }

    @Test
    public void convertShouldThrowExceptionOnUserMissing() {
        // Given
        String message = """
                {
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"01-27-2026T12:05:04.001Z",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("User is missing in received message"), "Expected a user missing exception");
    }

    @Test
    public void convertShouldThrowExceptionOnLogMissing() {
        // Given
        String message = """
                {
                    "user":
                        {
                            "id":"b16404b4-f690-44dc-8db0-8f48ec568590",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        }
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Log is missing in received message"), "Expected a log missing exception");
    }
}