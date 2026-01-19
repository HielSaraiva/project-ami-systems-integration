package org.eletra.energy.converter.controllers;

import org.eletra.energy.converter.models.MessageModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JmsControllerTests {

    @Autowired
    private JmsController jmsController;

    @Test
    public void jsonShouldBeConverted() throws Exception {
        // Arrange
        MessageModel messageModel = new MessageModel();
        messageModel.setBody("{\"user\":{\"id\":\"5e08d080-baff-4962-9df2-a4a96576fc28\",\"username\":\"meire.dapenha\",\"firstName\":\"Samuel\",\"lastName\":\"Gonçalves\",\"employeeCode\":\"857494\",\"position\":\"scientist\",\"cpf\":\"434.637.707-68\"},\"log\":{\"id\":\"a1d4b004-e67d-4b92-9e0a-c0a7f6540820\",\"sentAt\":\"01-19-2026T12:05:42.000Z\",\"message\":\"Ross, just for my own peace of mind, you’re not married to any more of us are you?\",\"format\":null}}");

        // Act
        jmsController.receiveJson(messageModel);

        // Assert
        // No exception should be thrown during the conversion
    }

    @Test
    public void jsonShouldNotBeNull() {
        // Arrange
        // No specific arrangement needed for this test

        // Act
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            jmsController.receiveJson(null);
        });

        // Assert
        assertEquals("Cannot invoke \"org.eletra.energy.converter.models.MessageModel.getBody()\" because \"json\" is null", exception.getMessage(), "Expected NullPointerException when passing null MessageModel");
    }

    @Test
    public void convertShouldThrowOnMalformedJson() {
        // Arrange
        MessageModel messageModel = new MessageModel();
        messageModel.setBody("{\"user\":\"id\":\"5e08d080-baff-4962-9df2-a4a96576fc28\",\"username\":\"meire.dapenha\",\"firstName\":\"Samuel\",\"lastName\":\"Gonçalves\",\"employeeCode\":\"857494\",\"position\":\"scientist\",\"cpf\":\"434.637.707-68\"},\"log\":{\"id\":\"a1d4b004-e67d-4b92-9e0a-c0a7f6540820\",\"sentAt\":\"01-19-2026T12:05:42.000Z\",\"message\":\"Ross, just for my own peace of mind, you’re not married to any more of us are you?\",\"format\":null}}");

        // Act
        Exception ex = assertThrows(Exception.class, () -> jmsController.receiveJson(messageModel));

        // Assert
        assertTrue(ex.getMessage().contains("Unexpected character"), "Expected a JSON parsing exception");
    }
}
