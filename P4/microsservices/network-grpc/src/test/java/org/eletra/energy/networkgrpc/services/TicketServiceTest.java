package org.eletra.energy.networkgrpc.services;

import org.eletra.energy.networkgrpc.configs.TestcontainersConfig;
import org.eletra.energy.networkgrpc.repositories.TicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @MockitoSpyBean
    private TicketRepository ticketRepository;

    @MockitoSpyBean
    private JmsTemplate jmsTemplate;

    @Test
    public void TicketShouldBeCreatedTest() {
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

        // When
        Assertions.assertDoesNotThrow(() -> {
            ticketService.createTicket(payload);
        });

        // Then
        Mockito.verify(ticketRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void TicketShouldBeSentTest() {
        // Given
        String ticketId = "123e4567-e89b-12d3-a456-426614174000";

        // When
        Assertions.assertDoesNotThrow(() -> {
            ticketService.sendTicketId(ticketId);
        });

        // Then
        Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(Mockito.eq("training-converter.receive_as_json"), Mockito.eq(ticketId));
    }
}
