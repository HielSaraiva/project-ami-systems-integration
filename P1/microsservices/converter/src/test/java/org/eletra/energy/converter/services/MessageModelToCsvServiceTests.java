package org.eletra.energy.converter.services;

import org.eletra.energy.converter.controllers.JmsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MessageModelToCsvServiceTests {

    @Autowired
    private JsonToCsvService jsonToCsvService;

    @Test
    public void convertShouldThrowOnMalformedJson() {
        // Arrange
        String badJson = "{\"user\":\"id\":\"5e08d080\"}";

        // Act
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.convert(badJson));

        // Assert
        assertTrue(exception.getMessage().contains("Unexpected character"), "Expected a JSON parsing exception");
    }

    @Test
    public void convertEmptyArrayReturnsEmptyString() throws Exception {
        // Arrange
        String json = "[]";

        // Act
        String csv = jsonToCsvService.convert(json);

        // Assert
        assertEquals("", csv, "Expected empty CSV for empty JSON array");
    }

    @Test
    public void convertPrimitiveNumberProducesCsv() throws Exception {
        // Arrange
        String json = "123";

        // Act
        String csv = jsonToCsvService.convert(json);

        // Assert
        assertTrue(csv.contains("value"), "Expected CSV to contain 'value' header");
        assertTrue(csv.contains("123"), "Expected CSV to contain the number '123'");
    }

    @Test
    public void convertLiteralNullProducesNullString() throws Exception {
        // Arrange
        String json = "null";

        // Act
        String csv = jsonToCsvService.convert(json);

        // Assert
        assertTrue(csv.contains("value"), "Expected CSV to contain 'value' header");
        assertTrue(csv.contains("null"), "Expected CSV to contain the string 'null'");
    }

    @Test
    public void convertObjectNestedFlattenProducesCsv() throws Exception {
        // Arrange
        String json = "{\"user\":{\"id\":\"1\",\"name\":\"X\"},\"status\":null}";

        // Act
        String csv = jsonToCsvService.convert(json);

        // Assert
        assertTrue(csv.contains("user.id"), "Expected CSV to contain 'user.id' header");
        assertTrue(csv.contains("user.name"), "Expected CSV to contain 'user.name' header");
        assertTrue(csv.contains("status"), "Expected CSV to contain 'status' header");
        assertTrue(csv.contains("1"), "Expected CSV to contain the value '1'");
        assertTrue(csv.contains("X"), "Expected CSV to contain the value 'X'");
        assertTrue(csv.contains("null"), "Expected CSV to contain the string 'null'");
    }

    @Test
    public void convertArrayOfObjectsProducesCsv() throws Exception {
        // Arrange
        String json = "[{\"a\":1},{\"a\":2}]";

        // Act
        String csv = jsonToCsvService.convert(json);

        // Assert
        assertTrue(csv.contains("a"), "Expected CSV to contain 'a' header");
        assertTrue(csv.contains("1"), "Expected CSV to contain the value '1'");
        assertTrue(csv.contains("2"), "Expected CSV to contain the value '2'");
    }

    @Test
    public void convertArrayOfPrimitivesJoinsValues() throws Exception {
        // Arrange
        String json = "[[1, \"x\", null]]";

        // Act
        String csv = jsonToCsvService.convert(json);

        // Assert
        assertTrue(csv.contains("value"), "Expected CSV to contain 'value' header");
        assertTrue(csv.contains("1"), "Expected CSV to contain the value '1'");
        assertTrue(csv.contains("x"), "Expected CSV to contain the value 'x'");
        assertTrue(csv.contains("null"), "Expected CSV to contain the string 'null'");
    }
}
