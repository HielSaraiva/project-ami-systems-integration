package org.eletra.energy.converter.controllers;

import org.eletra.energy.converter.models.Json;
import org.eletra.energy.converter.services.JsonToCsvService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;

@Controller
public class JmsController {

    @JmsListener(destination = "training-converter.receive_as_json")
    public void receiveJson(Json json) {
        System.out.println("Received JSON message:\n" + json.getBody());
        try {
            String csv = JsonToCsvService.convert(json.getBody());
            System.out.println("Converted JSON message:\n" + csv);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
