package org.eletra.energy.business.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eletra.energy.business.services.JsonFormatService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;

@Log4j2
@RequiredArgsConstructor
@Controller
public class JmsController {
    private final JsonFormatService jsonFormatService;

    @JmsListener(destination = "training-converter.receive_as_json")
    public void receiveJson(String message) throws Exception {
        log.info("Received JSON message from \"training-converter.receive_as_json\" queue:\n{}", message);
        jsonFormatService.execute(message);
    }
}
