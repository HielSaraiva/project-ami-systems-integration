package org.eletra.energy.network.services;

import org.eletra.energy.network.configs.TestcontainersConfig;
import org.eletra.energy.network.models.entities.Ticket;
import org.eletra.energy.network.models.entities.TicketProcess;
import org.eletra.energy.network.models.enums.ProcessStatus;
import org.eletra.energy.network.models.enums.ProcessType;
import org.eletra.energy.network.models.enums.TicketStatus;
import org.eletra.energy.network.repositories.TicketProcessRepository;
import org.eletra.energy.network.repositories.TicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @MockitoSpyBean
    private TicketRepository ticketRepository;

    @MockitoSpyBean
    private TicketProcessRepository ticketProcessRepository;

    @Test
    public void ticketShouldNotBePresent() {
        //Given
        String payload = """
                user,time,message
                "d1a1b3ca-0884-4701-a219-6ada5c638812","2026-02-02 12:34:21","Until I was 25 I thought the only response to ‘I love you’ was ‘Oh crap!'"
                """;

        Mockito.doReturn(Optional.empty()).when(ticketRepository).findById(Mockito.any());
        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.BUSINESS);
        testProcess.setPayload(payload);
        ticketProcessRepository.save(testProcess);

        // When
        Assertions.assertDoesNotThrow(() -> {
            ticketService.finishTicket(testTicket.getId().toString());
        });

        // Then
        Mockito.verify(ticketRepository, Mockito.times(1)).findById(Mockito.any());
    }
}
