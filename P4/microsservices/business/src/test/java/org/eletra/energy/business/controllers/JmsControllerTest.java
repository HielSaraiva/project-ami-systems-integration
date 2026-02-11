package org.eletra.energy.business.controllers;

import org.eletra.energy.business.configs.TestcontainersConfig;
import org.eletra.energy.business.models.entities.Ticket;
import org.eletra.energy.business.models.enums.TicketStatus;
import org.eletra.energy.business.repositories.TicketRepository;
import org.eletra.energy.business.services.JsonFormatService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class JmsControllerTest {

    @Autowired
    private JmsController jmsController;

    @Autowired
    private TicketRepository ticketRepository;

    @MockitoSpyBean
    private JsonFormatService jsonFormatService;

    @Test
    public void jsonShouldBeSendToConvert() throws Exception {
        // Given
        String payload = """
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

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Assertions.assertDoesNotThrow(() -> {
            jmsController.receiveJson(testTicket.getId().toString());
        });

        // Then
        Mockito.verify(jsonFormatService, Mockito.times(1)).execute(testTicket.getId().toString());
    }

    @Test
    public void sendShouldThrowOnMalformedJson() {
        // Given
        String payload = """
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
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Exception exception = assertThrows(Exception.class, () -> jmsController.receiveJson(testTicket.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Unexpected character"), "Expected a JSON parsing exception");
    }
}
