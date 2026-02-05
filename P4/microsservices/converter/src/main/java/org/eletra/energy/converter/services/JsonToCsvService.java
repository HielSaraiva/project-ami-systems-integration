package org.eletra.energy.converter.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class JsonToCsvService {

    private final ObjectMapper objectMapper;
    private final CsvMapper csvMapper;
    private final JmsTemplate jmsTemplate;

    public void execute(String json) throws Exception {

        JsonNode root = objectMapper.readTree(json);
        List<Map<String, String>> rows = new ArrayList<>();

        if (root.isObject()) {
            validateRequiredFields(root);
            Map<String, String> m = mapExpectedFields(root);
            rows.add(m);
        } else {
            throw new Exception("Invalid JSON input format!");
        }

        LinkedHashSet<String> headers = new LinkedHashSet<>();
        headers.add("user");
        headers.add("time");
        headers.add("message");

        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        for (String col : headers) {
            schemaBuilder.addColumn(col);
        }
        CsvSchema schema = schemaBuilder.build().withHeader();

        final String result = csvMapper.writerFor(new TypeReference<List<Map<String, String>>>() {})
                .with(schema)
                .writeValueAsString(rows);

        final String trimmed = result.stripTrailing();

        log.info("Converted JSON message:\n{}", trimmed);

        log.info("Sending CSV message to \"training-converter.send_as_csv\" queue...");
        jmsTemplate.convertAndSend("training-converter.send_as_csv", trimmed);
        log.info("Message sent to \"training-converter.send_as_csv\"!\n");
    }

    private void validateRequiredFields(JsonNode node) throws Exception {
        boolean hasUsername = node.has("username");
        boolean hasSentAt = node.has("sentAt");
        boolean hasMessage = node.has("message");

        if (!(hasUsername && hasSentAt && hasMessage)) {
            throw new Exception("Invalid JSON input format!");
        }
    }

    private Map<String, String> mapExpectedFields(JsonNode node) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("user", node.path("username").asText(""));
        m.put("time", node.path("sentAt").asText(""));
        m.put("message", node.path("message").asText(""));
        return m;
    }
}
