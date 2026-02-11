package org.eletra.energy.business.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.eletra.energy.business.models.dtos.ReceivedMessageDTO;
import org.eletra.energy.business.models.dtos.SentMessageDTO;
import org.eletra.energy.business.models.entities.Ticket;
import org.eletra.energy.business.models.entities.TicketProcess;
import org.eletra.energy.business.models.enums.ProcessStatus;
import org.eletra.energy.business.models.enums.ProcessType;
import org.eletra.energy.business.repositories.TicketProcessRepository;
import org.eletra.energy.business.repositories.TicketRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Service
public class JsonFormatService {

    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final TicketRepository ticketRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
    private final TicketProcessService ticketProcessService;

    public void execute(String ticketId) throws Exception {

        Optional<Ticket> ticket = ticketRepository.findById(UUID.fromString(ticketId));
        String json = null;
        if (ticket.isPresent()) {
            json = ticket.get().getPayload();

            String processId = ticketProcessService.createProcess(ticket.get(), ProcessStatus.PROCESSING, ProcessType.BUSINESS);

            ReceivedMessageDTO receivedMessageDTO = objectMapper.readValue(json, ReceivedMessageDTO.class);

            verifyReceivedMessageFormat(receivedMessageDTO);

            SentMessageDTO sentMessageDTO = new SentMessageDTO(receivedMessageDTO.getUser().getId(), formatter.format(Instant.now(clock)), formatSentAt(receivedMessageDTO.getLog().getSentAt()), receivedMessageDTO.getLog().getMessage());

            String outputMessage = objectMapper.writeValueAsString(sentMessageDTO);

            log.info("Converted JSON message received from \"training-converter.receive_as_json\" queue:\n{}", outputMessage);
            ticketProcessService.finishProcess(processId, outputMessage);
            ticketProcessService.sendTicketProcessId(processId);
        }
    }

    private String formatSentAt(String raw) {

        DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("MM-dd-uuuu'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

        Instant inst = inFmt.parse(raw, Instant::from);
        return outFmt.format(inst);
    }

    private void verifyReceivedMessageFormat(ReceivedMessageDTO dto) throws Exception {
        if (dto.getUser() == null) {
            log.error("User is missing in received message");
            throw new Exception("User is missing in received message");
        }
        if (dto.getLog() == null) {
            log.error("Log is missing in received message");
            throw new Exception("Log is missing in received message");
        }
        if (dto.getUser().getId() == null || dto.getUser().getId().isEmpty()) {
            log.error("Invalid user ID in received message");
            throw new Exception("Invalid user ID in received message");
        }
        if (dto.getLog().getSentAt() == null || dto.getLog().getSentAt().isEmpty()) {
            log.error("Invalid sentAt in received message log");
            throw new Exception("Invalid sentAt in received message log");
        }
        if (dto.getLog().getMessage() == null || dto.getLog().getMessage().isEmpty()) {
            log.error("Invalid message content in received message log");
            throw new Exception("Invalid message content in received message log");
        }
    }
}
