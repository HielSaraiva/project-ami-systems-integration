package org.eletra.energy.converter.controllers;

import org.eletra.energy.converter.models.Json;
import org.eletra.energy.converter.utils.JsonToCsvUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;

@Controller
public class JmsController {

    @JmsListener(destination = "training-converter.receive_as_json")
    public void receiveJson(Json json) throws  Exception {
        System.out.println("Received JSON message:\n" + json.getBody());
        String csv = JsonToCsvUtils.convert(json.getBody());
        System.out.println("Converted JSON message:\n" + csv);
    }
}
