package org.eletra.energy.converter.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JsonToCsvServiceTests {

    @Autowired
    private JsonToCsvService jsonToCsvService;

    @MockitoSpyBean
    private JmsTemplate jmsTemplate;

    @Test
    public void jsonShouldBeConverted() {
        // Given
        String message = """
                {
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00",
                    "message": "yipe hey, yipe ho... e uma garrafa de rum!"
                }""";

        // When
        Assertions.assertDoesNotThrow(() -> {
            jsonToCsvService.execute(message);
        });

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(
                Mockito.eq("training-converter.send_as_csv"),
                messageCaptor.capture());

        final String result = messageCaptor.getValue();

        assertEquals("user,time,message\n" +
                "Tereza,\"2026-08-24 13:59:00\",\"yipe hey, yipe ho... e uma garrafa de rum!\"", result, "Expected a CSV conversion result");
    }

    @Test
    public void convertShouldThrowExceptionOnArrayJsonFormat(){
        // Given
        String message = """
                [{
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00",
                    "message": "yipe hey, yipe ho... e uma garrafa de rum!"
                }]""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }

    @Test
    public void convertShouldThrowExceptionOnKeyMissing1(){
        // Given
        String message = """
                {
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00"
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }

    @Test
    public void convertShouldThrowExceptionOnKeyMissing2(){
        // Given
        String message = """
                {
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00"
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }

    @Test
    public void convertShouldThrowExceptionOnKeyMissing3(){
        // Given
        String message = """
                {
                    "createdAt": "2026-08-24 14:00:00"
                }""";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }

    @Test
    public void convertShouldThrowExceptionOnEmptyJsonFormat(){
        // Given
        String message = "{}";

        // When
        Exception exception = assertThrows(Exception.class, () -> jsonToCsvService.execute(message));

        // Then
        assertTrue(exception.getMessage().contains("Invalid JSON input format!"), "Expected an invalid format exception");
    }
}
