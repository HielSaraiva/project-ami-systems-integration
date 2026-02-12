package org.eletra.energy.converter.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eletra.energy.converter.models.entities.Ticket;
import org.eletra.energy.converter.models.entities.TicketProcess;
import org.eletra.energy.converter.models.enums.ProcessStatus;
import org.eletra.energy.converter.models.enums.ProcessType;
import org.eletra.energy.converter.repositories.TicketProcessRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class JsonToCsvService {

    private final ObjectMapper objectMapper;
    private final CsvMapper csvMapper;
    private final TicketProcessRepository ticketProcessRepository;
    private final TicketProcessService ticketProcessService;

    public void execute(String processIdInput) throws Exception {

        Optional<TicketProcess> ticketProcess = ticketProcessRepository.findById(UUID.fromString(processIdInput));
        String json;
        if (ticketProcess.isPresent()) {
            json = ticketProcess.get().getPayload();

            String processId = ticketProcessService.createProcess(ticketProcess.get().getTicket(), ProcessStatus.PROCESSING, ProcessType.CONVERTER);

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

            log.info("Converted JSON message received from \"training-converter.send_as_json\" queue:\n{}", trimmed);
            ticketProcessService.finishProcess(processId, trimmed);
            ticketProcessService.sendTicketProcessId(processId);
        }
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
