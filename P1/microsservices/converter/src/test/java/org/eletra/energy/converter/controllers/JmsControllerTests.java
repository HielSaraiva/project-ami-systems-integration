package org.eletra.energy.converter.controllers;

import org.eletra.energy.converter.models.MessageModel;
import org.eletra.energy.converter.services.JsonToCsvService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JmsControllerTests {

    @Autowired
    private JmsController jmsController;

    @MockitoSpyBean
    private JsonToCsvService jsonToCsvService;

    @Test
    public void jsonShouldBeSendToConvert() throws Exception {
        // Given
        MessageModel messageModel = new MessageModel();
        messageModel.setBody("""
                {
                    "username": "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00",
                    "message": "yipe hey, yipe ho... e uma garrafa de rum!"
                }""");

        // When
        Assertions.assertDoesNotThrow(() -> {
            jmsController.receiveJson(messageModel);
        });

        // Then
        Mockito.verify(jsonToCsvService, Mockito.times(1)).execute(messageModel.getBody());
    }

    @Test
    public void sendShouldThrowOnMalformedJson() {
        // Given
        MessageModel messageModel = new MessageModel();
        messageModel.setBody("""
                {
                    "username" "Tereza",
                    "createdAt": "2026-08-24 14:00:00",
                    "sentAt": "2026-08-24 13:59:00",
                    "message": "yipe hey, yipe ho... e uma garrafa de rum!"
                }""");

        // When
        Exception exception = assertThrows(Exception.class, () -> jmsController.receiveJson(messageModel));

        // Then
        assertTrue(exception.getMessage().contains("Unexpected character"), "Expected a JSON parsing exception");
    }
}
