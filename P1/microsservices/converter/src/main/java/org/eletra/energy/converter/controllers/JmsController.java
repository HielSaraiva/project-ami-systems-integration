package org.eletra.energy.converter.controllers;

import lombok.RequiredArgsConstructor;
import org.eletra.energy.converter.models.MessageModel;
import org.eletra.energy.converter.services.JsonToCsvService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class JmsController {
    private final JsonToCsvService jsonToCsvService;

    @JmsListener(destination = "training-converter.receive_as_json")
    public void receiveJson(MessageModel messageModel) throws  Exception {
        System.out.println("Received JSON message:\n" + messageModel.getBody());
        String csv = jsonToCsvService.convert(messageModel.getBody());
        System.out.println("Converted JSON message:\n" + csv);
    }
}
