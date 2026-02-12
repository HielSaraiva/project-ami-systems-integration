package org.eletra.energy.converter.controllers;

import org.eletra.energy.converter.configs.TestcontainersConfig;
import org.eletra.energy.converter.models.entities.Ticket;
import org.eletra.energy.converter.models.entities.TicketProcess;
import org.eletra.energy.converter.models.enums.ProcessStatus;
import org.eletra.energy.converter.models.enums.ProcessType;
import org.eletra.energy.converter.models.enums.TicketStatus;
import org.eletra.energy.converter.repositories.TicketProcessRepository;
import org.eletra.energy.converter.repositories.TicketRepository;
import org.eletra.energy.converter.services.JsonToCsvService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class JmsControllerTest {

    @Autowired
    private JmsController jmsController;

    @MockitoSpyBean
    private JsonToCsvService jsonToCsvService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketProcessRepository ticketProcessRepository;

    @Test
    public void jsonShouldBeSendToConvert() throws Exception {
        // Given
        String payload = """
                {
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00",
                    "message": "yipe hey, yipe ho... e uma garrafa de rum!"
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.BUSINESS);
        testProcess.setPayload(payload);
        ticketProcessRepository.save(testProcess);

        // When
        Assertions.assertDoesNotThrow(() -> {
            jmsController.receiveJson(testProcess.getId().toString());
        });

        // Then
        Mockito.verify(jsonToCsvService, Mockito.times(1)).execute(testProcess.getId().toString());
    }

    @Test
    public void sendShouldThrowOnMalformedJson() {
        // Given
        String payload = """
                {
                    "username" "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00",
                    "message": "yipe hey, yipe ho... e uma garrafa de rum!"
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.BUSINESS);
        testProcess.setPayload(payload);
        ticketProcessRepository.save(testProcess);

        // When
        Exception exception = assertThrows(Exception.class, () -> jmsController.receiveJson(testProcess.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Unexpected character"), "Expected a JSON parsing exception");
    }
}
