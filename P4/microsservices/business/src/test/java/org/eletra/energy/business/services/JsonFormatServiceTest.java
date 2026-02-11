package org.eletra.energy.business.services;

import org.eletra.energy.business.configs.TestcontainersConfig;
import org.eletra.energy.business.models.entities.Ticket;
import org.eletra.energy.business.models.enums.TicketStatus;
import org.eletra.energy.business.repositories.TicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class JsonFormatServiceTest {

    @Autowired
    private JsonFormatService jsonFormatService;

    @MockitoSpyBean
    private TicketRepository ticketRepository;

    @MockitoSpyBean
    private JmsTemplate jmsTemplate;

    @MockitoBean
    private Clock clock;

    @Test
    public void ticketShouldNotBePresent() {
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

        Mockito.doReturn(Optional.empty()).when(ticketRepository).findById(Mockito.any(UUID.class));
        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Assertions.assertDoesNotThrow(() -> {
            jsonFormatService.execute(testTicket.getId().toString());
        });

        // Then
        Mockito.verify(ticketRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    public void jsonShouldBeConverted() {
        // Given
        Instant fixedInstant = Instant.parse("2026-01-27T12:05:34Z");
        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);

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
            jsonFormatService.execute(testTicket.getId().toString());
        });

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(
                Mockito.eq("training-converter.send_as_json"),
                messageCaptor.capture());
    }

    @Test
    public void convertShouldThrowExceptionOnIdMissing() {
        // Given
        String payload = """
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

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(testTicket.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid user ID in received message"), "Expected an invalid user ID exception");
    }

    @Test
    public void convertShouldThrowExceptionOnIdEmpty() {
        // Given
        String payload = """
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

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(testTicket.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid user ID in received message"), "Expected an invalid user ID exception");
    }

    @Test
    public void convertShouldThrowExceptionOnSentAtMissing() {
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
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";
        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(testTicket.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid sentAt in received message log"), "Expected an invalid sentAt exception");
    }

    @Test
    public void convertShouldThrowExceptionOnSentAtEmpty() {
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
                            "sentAt":"",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(testTicket.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid sentAt in received message log"), "Expected an invalid sentAt exception");
    }

    @Test
    public void convertShouldThrowExceptionOnMessageMissing() {
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
                            "format":null
                        }
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(testTicket.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid message content in received message log"), "Expected an invalid message content exception");
    }

    @Test
    public void convertShouldThrowExceptionOnMessageEmpty() {
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
                            "message":"",
                            "format":null
                        }
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(testTicket.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Invalid message content in received message log"), "Expected an invalid message content exception");
    }

    @Test
    public void convertShouldThrowExceptionOnUserMissing() {
        // Given
        String payload = """
                {
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
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(testTicket.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("User is missing in received message"), "Expected a user missing exception");
    }

    @Test
    public void convertShouldThrowExceptionOnLogMissing() {
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
                        }
                }""";

        Ticket testTicket = new Ticket(TicketStatus.IN_PROGRESS, payload);
        ticketRepository.save(testTicket);

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonFormatService.execute(testTicket.getId().toString()));

        // Then
        assertTrue(exception.getMessage().contains("Log is missing in received message"), "Expected a log missing exception");
    }
}