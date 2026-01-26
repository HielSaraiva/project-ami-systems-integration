package org.eletra.energy.business.controllers;

import jakarta.jms.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.activemq.ArtemisContainer;

@SpringBootTest
public class JmsControllerTests {

    private static final ArtemisContainer artemisContainer = new ArtemisContainer("apache/activemq-artemis:latest")
            .withUser("test")
            .withPassword("test")
            .withExposedPorts(61616, 8161);

    @MockitoSpyBean
    private JmsTemplate jmsTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.artemis.broker-url", artemisContainer::getBrokerUrl);
        registry.add("spring.artemis.user", artemisContainer::getUser);
        registry.add("spring.artemis.password", artemisContainer::getPassword);
    }

    @BeforeAll
    public static void beforeAll() {
        artemisContainer.start();
    }

    @Test
    public void test1() {
        System.out.println("test1");
        jmsTemplate.convertAndSend("training-converter.receive_as_json", "mensagem de teste");
    }
}
