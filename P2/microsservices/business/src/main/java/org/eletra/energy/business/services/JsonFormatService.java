package org.eletra.energy.business.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class JsonFormatService {

    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;

    public void execute(String json) throws Exception {

        JsonNode root = objectMapper.readTree(json);

        if (!root.isObject()) {
            throw new Exception("Invalid JSON input format!");
        }

        validateRequiredFields(root);

        Map<String, String> output = mapExpectedFields(root);

        String result = objectMapper.writeValueAsString(output);
        String trimmed = result.stripTrailing();

        System.out.println("Converted JSON message:\n" + trimmed);
        System.out.println("Sending JSON message to training-converter.send_as_json");
        jmsTemplate.convertAndSend("training-converter.send_as_json", trimmed);
        System.out.println("Message sent!\n");
    }

    private void validateRequiredFields(JsonNode node) throws IllegalArgumentException {
        boolean hasUser = node.has("user") && node.path("user").has("id");
        boolean hasLog = node.has("log") && node.path("log").has("sentAt") && node.path("log").has("message");

        if (!(hasUser && hasLog)) {
            throw new IllegalArgumentException("Invalid JSON input format!");
        }
    }

    private Map<String, String> mapExpectedFields(JsonNode node) throws IllegalArgumentException {

        JsonNode userNode = node.path("user");
        JsonNode logNode = node.path("log");

        String userId = userNode.path("id").asText("");
        String sentAtRaw = logNode.path("sentAt").asText("");
        String message = logNode.path("message").asText("");

        String sentAtFormatted = formatSentAt(sentAtRaw);
        String createdAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());

        Map<String, String> m = new LinkedHashMap<>();
        m.put("username", userId);
        m.put("createdAt", createdAt);
        m.put("sentAt", sentAtFormatted);
        m.put("message", message);
        return m;
    }

    private String formatSentAt(String raw) throws IllegalArgumentException {

        if (raw == null || raw.isEmpty()) return "";

        DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("MM-dd-uuuu'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC);

        try {
            Instant inst = inFmt.parse(raw, Instant::from);
            return outFmt.format(inst);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format in sentAt field!");
        }
    }
}
