package org.eletra.energy.converter.services;

import org.eletra.energy.converter.configs.TestcontainersConfig;
import org.eletra.energy.converter.models.entities.Ticket;
import org.eletra.energy.converter.models.entities.TicketProcess;
import org.eletra.energy.converter.models.enums.ProcessStatus;
import org.eletra.energy.converter.models.enums.ProcessType;
import org.eletra.energy.converter.models.enums.TicketStatus;
import org.eletra.energy.converter.repositories.TicketProcessRepository;
import org.eletra.energy.converter.repositories.TicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;
import java.util.UUID;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class TicketProcessServiceTest {

    @Autowired
    private TicketProcessService ticketProcessService;

    @MockitoSpyBean
    private TicketProcessRepository ticketProcessRepository;

    @MockitoSpyBean
    private TicketRepository ticketRepository;

    @Test
    public void processShouldNotBePresent() {
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

        Mockito.doReturn(Optional.empty()).when(ticketProcessRepository).findById(Mockito.any(UUID.class));

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.CONVERTER);
        ticketProcessRepository.save(testProcess);

        // When
        Assertions.assertDoesNotThrow(() -> {
            ticketProcessService.finishProcess(testProcess.getId().toString(), payload);
        });

        // Then
        Mockito.verify(ticketProcessRepository, Mockito.times(1)).findById(Mockito.any());
    }
}
