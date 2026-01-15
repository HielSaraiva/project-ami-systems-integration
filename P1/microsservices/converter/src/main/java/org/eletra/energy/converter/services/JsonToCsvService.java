package org.eletra.energy.converter.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JsonToCsvService {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final CsvMapper CSV = new CsvMapper();

    public static String convert(String json) throws Exception {
        JsonNode root = JSON.readTree(json);
        List<Map<String, String>> rows = new ArrayList<>();

        if (root.isArray()) {
            for (JsonNode elem : root) {
                Map<String, String> map = new LinkedHashMap<>();
                flatten(elem, "", map);
                rows.add(map);
            }
        } else if (root.isObject()) {
            Map<String, String> map = new LinkedHashMap<>();
            flatten(root, "", map);
            rows.add(map);
        } else {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", root.isNull() ? "null" : root.asText(""));
            rows.add(map);
        }

        if (rows.isEmpty()) return "";

        LinkedHashSet<String> headers = new LinkedHashSet<>();
        for (Map<String, String> r : rows) {
            headers.addAll(r.keySet());
        }

        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        for (String col : headers) {
            schemaBuilder.addColumn(col);
        }
        CsvSchema schema = schemaBuilder.build().withHeader();

        return CSV.writerFor(new TypeReference<List<Map<String, String>>>() {
                })
                .with(schema)
                .writeValueAsString(rows);
    }

    private static void flatten(JsonNode node, String prefix, Map<String, String> out) {
        if (node == null || node.isNull()) {
            String key = prefix.isEmpty() ? "value" : prefix;
            out.put(key, "null");
            return;
        }

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> f = fields.next();
                String path = prefix.isEmpty() ? f.getKey() : prefix + "." + f.getKey();
                flatten(f.getValue(), path, out);
            }
        } else if (node.isArray()) {
            String key = prefix.isEmpty() ? "value" : prefix;
            boolean allPrimitive = true;
            List<String> parts = new ArrayList<>();
            for (JsonNode e : node) {
                if (e == null || e.isNull()) {
                    parts.add("null");
                } else if (e.isValueNode()) {
                    parts.add(e.asText(""));
                } else {
                    allPrimitive = false;
                    break;
                }
            }
            String value;
            if (allPrimitive) {
                value = String.join(";", parts);
            } else {
                try {
                    value = JSON.writeValueAsString(node);
                } catch (Exception ex) {
                    value = node.toString();
                }
            }
            out.put(key, value);
        } else {
            String key = prefix.isEmpty() ? "value" : prefix;
            out.put(key, node.asText(""));
        }
    }
}
