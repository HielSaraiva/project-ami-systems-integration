package org.eletra.energy.business.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.eletra.energy.business.models.dtos.ReceivedMessageDTO;
import org.eletra.energy.business.models.dtos.SentMessageDTO;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Log4j2
@RequiredArgsConstructor
@Service
public class JsonFormatService {

    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;
    private final Clock clock;

    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    public void execute(String json) throws Exception {

        ReceivedMessageDTO receivedMessageDTO = objectMapper.readValue(json, ReceivedMessageDTO.class);

        verifyReceivedMessageFormat(receivedMessageDTO);

        SentMessageDTO sentMessageDTO = new SentMessageDTO(
                receivedMessageDTO.getUser().getId(),
                formatter.format(Instant.now(clock)),
                formatSentAt(receivedMessageDTO.getLog().getSentAt()),
                receivedMessageDTO.getLog().getMessage());

        String outputMessage = objectMapper.writeValueAsString(sentMessageDTO);

        log.info("Converted JSON message received from \"training-converter.receive_as_json\" queue:\n{}", outputMessage);
        log.info("Sending JSON message to \"training-converter.send_as_json\" queue...");
        jmsTemplate.convertAndSend("training-converter.send_as_json", outputMessage);
        log.info("Message sent to \"training-converter.send_as_json\"!\n");
    }

    private String formatSentAt(String raw) {

        DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("MM-dd-uuuu'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC);

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
