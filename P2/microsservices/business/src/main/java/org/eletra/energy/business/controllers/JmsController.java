package org.eletra.energy.business.controllers;

import lombok.RequiredArgsConstructor;
import org.eletra.energy.business.services.JsonFormatService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class JmsController {
    private final JsonFormatService jsonFormatService;

    @JmsListener(destination = "training-converter.receive_as_json")
    public void receiveJson(String message) throws Exception {
        System.out.println("Received JSON message:\n" + message);
        jsonFormatService.execute(message);
    }
}
