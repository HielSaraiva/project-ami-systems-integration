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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class JsonToCsvServiceTest {

    @Autowired
    private JsonToCsvService jsonToCsvService;

    @MockitoSpyBean
    private JmsTemplate jmsTemplate;

    @Autowired
    private TicketRepository ticketRepository;

    @MockitoSpyBean
    private TicketProcessRepository ticketProcessRepository;

    @Test
    public void ticketProcessShouldNotBePresent() {
        // Given
        String payload = """
                {
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00",
                    "message": "yipe hey, yipe ho... e uma garrafa de rum!"
                }""";

        Mockito.doReturn(Optional.empty()).when(ticketProcessRepository).findById(Mockito.any(UUID.class));
        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.BUSINESS);
        testProcess.setPayload(payload);
        ticketProcessRepository.save(testProcess);

        // When
        Assertions.assertDoesNotThrow(() -> {
            jsonToCsvService.execute(testProcess.getId().toString());
        });

        // Then
        Mockito.verify(ticketProcessRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    public void jsonShouldBeConverted() {
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
            jsonToCsvService.execute(testProcess.getId().toString());
        });

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(
                Mockito.eq("training-converter.send_as_csv"),
                messageCaptor.capture());
    }

    @Test
    public void convertShouldThrowExceptionOnArrayJsonFormat(){
        // Given
        String payload = """
                [{
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00",
                    "message": "yipe hey, yipe ho... e uma garrafa de rum!"
                }]""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.BUSINESS);
        testProcess.setPayload(payload);
        ticketProcessRepository.save(testProcess);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(testProcess.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }

    @Test
    public void convertShouldThrowExceptionOnKeyMissing1(){
        // Given
        String payload = """
                {
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00"
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.BUSINESS);
        testProcess.setPayload(payload);
        ticketProcessRepository.save(testProcess);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(testProcess.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }

    @Test
    public void convertShouldThrowExceptionOnKeyMissing2(){
        // Given
        String payload = """
                {
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00"
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.BUSINESS);
        testProcess.setPayload(payload);
        ticketProcessRepository.save(testProcess);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(testProcess.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }

    @Test
    public void convertShouldThrowExceptionOnKeyMissing3(){
        // Given
        String payload = """
                {
                    "createdAt": "2026-08-24 14:00:00"
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.BUSINESS);
        testProcess.setPayload(payload);
        ticketProcessRepository.save(testProcess);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(testProcess.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }

    @Test
    public void convertShouldThrowExceptionOnEmptyJsonFormat(){
        // Given
        String payload = "{}";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);
        TicketProcess testProcess = new TicketProcess(testTicket, ProcessStatus.PROCESSING, ProcessType.BUSINESS);
        testProcess.setPayload(payload);
        ticketProcessRepository.save(testProcess);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(testProcess.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }
}
