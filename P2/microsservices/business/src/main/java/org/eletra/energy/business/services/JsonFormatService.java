package org.eletra.energy.business.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eletra.energy.business.models.dtos.ReceivedMessageDTO;
import org.eletra.energy.business.models.dtos.SentMessageDTO;
import org.slf4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class JsonFormatService {

    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;
    private final Clock clock;
    private final Logger logger;

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

        logger.info("Converted JSON message:\n{}", outputMessage);
        logger.info("Sending JSON message to training-converter.send_as_json");
        jmsTemplate.convertAndSend("training-converter.send_as_json", outputMessage);
        logger.info("Message sent!\n");
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
            throw new Exception("User is missing in received message");
        }
        if (dto.getLog() == null) {
            throw new Exception("Log is missing in received message");
        }
        if (dto.getUser().getId() == null || dto.getUser().getId().isEmpty()) {
            throw new Exception("Invalid user ID in received message");
        }
        if (dto.getLog().getSentAt() == null || dto.getLog().getSentAt().isEmpty()) {
            throw new Exception("Invalid sentAt in received message log");
        }
        if (dto.getLog().getMessage() == null || dto.getLog().getMessage().isEmpty()) {
            throw new Exception("Invalid message content in received message log");
        }
    }
}
