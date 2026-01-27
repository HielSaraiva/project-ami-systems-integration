package org.eletra.energy.business.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eletra.energy.business.models.dtos.ReceivedMessageDTO;
import org.eletra.energy.business.models.dtos.SentMessageDTO;
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

    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    public void execute(String json) throws Exception {

        ReceivedMessageDTO receivedMessageDto = objectMapper.readValue(json, ReceivedMessageDTO.class);

        SentMessageDTO sentMessageDto = new SentMessageDTO(
                receivedMessageDto.getUser().getId(),
                formatter.format(Instant.now(clock)),
                formatSentAt(receivedMessageDto.getLog().getSentAt()),
                receivedMessageDto.getLog().getMessage());

        String outputMessage = objectMapper.writeValueAsString(sentMessageDto);

        System.out.println("Converted JSON message:\n" + outputMessage);
        System.out.println("Sending JSON message to training-converter.send_as_json");
        jmsTemplate.convertAndSend("training-converter.send_as_json", outputMessage);
        System.out.println("Message sent!\n");
    }

    private String formatSentAt(String raw) {

        DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("MM-dd-uuuu'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC);

        Instant inst = inFmt.parse(raw, Instant::from);
        return outFmt.format(inst);
    }
}
