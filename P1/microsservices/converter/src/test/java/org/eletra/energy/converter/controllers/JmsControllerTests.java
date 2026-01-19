package org.eletra.energy.converter.controllers;

import org.eletra.energy.converter.models.MessageModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JmsControllerTests {

    @Autowired
    private JmsController jmsController;

    @Test
    public void jsonShouldBeConverted() {
        // Given
        MessageModel messageModel = new MessageModel();
        messageModel.setBody("""
        {
           "user": {
              "id": "5e08d080-baff-4962-9df2-a4a96576fc28",
              "username": "meire.dapenha",
              "firstName": "Samuel",
              "lastName": "Gonçalves",
              "employeeCode": "857494",
              "position": "scientist",
              "cpf": "434.637.707-68"
           },
           "log": {
              "id": "a1d4b004-e67d-4b92-9e0a-c0a7f6540820",
              "sentAt": "01-19-2026T12:05:42.000Z",
              "message": "Ross, just for my own peace of mind, you’re not married to any more of us are you?",
              "format": null
           }
        }""");

        // When/Then
        Assertions.assertDoesNotThrow(() -> {
            jmsController.receiveJson(messageModel);
        });
    }

    @Test
    public void convertShouldThrowOnMalformedJson() {
        // Given
        MessageModel messageModel = new MessageModel();
        messageModel.setBody("""
        {
           "user": 
              "id": "5e08d080-baff-4962-9df2-a4a96576fc28",
              "username": "meire.dapenha",
              "firstName": "Samuel",
              "lastName": "Gonçalves",
              "employeeCode": "857494",
              "position": "scientist",
              "cpf": "434.637.707-68"
           },
           "log": {
              "id": "a1d4b004-e67d-4b92-9e0a-c0a7f6540820",
              "sentAt": "01-19-2026T12:05:42.000Z",
              "message": "Ross, just for my own peace of mind, you’re not married to any more of us are you?",
              "format": null
           }
        }""");

        // When
        Exception exception = assertThrows(Exception.class, () -> jmsController.receiveJson(messageModel));

        // Then
        assertTrue(exception.getMessage().contains("Unexpected character"), "Expected a JSON parsing exception");
    }
}
