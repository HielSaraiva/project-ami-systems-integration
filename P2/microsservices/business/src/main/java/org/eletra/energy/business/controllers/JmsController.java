package org.eletra.energy.business.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eletra.energy.business.services.JsonFormatService;
import org.slf4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class JmsController {
    private final JsonFormatService jsonFormatService;
    private final Logger logger;

    @JmsListener(destination = "training-converter.receive_as_json")
    public void receiveJson(String message) throws Exception {
        logger.info("Received JSON message:\n{}", message);
        jsonFormatService.execute(message);
    }
}
