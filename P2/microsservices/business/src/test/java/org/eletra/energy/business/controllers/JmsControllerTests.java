package org.eletra.energy.business.controllers;

import jakarta.jms.*;
import org.eletra.energy.business.models.MessageModel;
import org.eletra.energy.business.services.JsonFormatService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.eletra.energy.business.configs.TestcontainersConfig.artemisContainer;
import static org.eletra.energy.business.configs.TestcontainersConfig.postgresContainer;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JmsControllerTests {

    @Autowired
    private JmsController jmsController;

    @MockitoSpyBean
    private JsonFormatService jsonFormatService;

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
    }

    @Test
    public void jsonShouldBeSendToConvert() throws Exception {
        // Given
        MessageModel messageModel = new MessageModel();
        messageModel.setBody("""
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
                }""");

        // When
        Assertions.assertDoesNotThrow(() -> {
            jmsController.receiveJson(messageModel);
        });

        // Then
        Mockito.verify(jsonFormatService, Mockito.times(1)).execute(messageModel.getBody());
    }

    @Test
    public void sendShouldThrowOnMalformedJson() {
        // Given
        MessageModel messageModel = new MessageModel();
        messageModel.setBody("""
                {
                    "user"
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
                }""");

        // When
        Exception exception = assertThrows(Exception.class, () -> jmsController.receiveJson(messageModel));

        // Then
        assertTrue(exception.getMessage().contains("Unexpected character"), "Expected a JSON parsing exception");
    }
}
