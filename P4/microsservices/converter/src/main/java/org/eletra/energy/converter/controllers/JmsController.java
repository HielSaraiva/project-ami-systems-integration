package org.eletra.energy.converter.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eletra.energy.converter.models.MessageModel;
import org.eletra.energy.converter.services.JsonToCsvService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;

@Log4j2
@RequiredArgsConstructor
@Controller
public class JmsController {
    private final JsonToCsvService jsonToCsvService;

    @JmsListener(destination = "training-converter.send_as_json")
    public void receiveJson(MessageModel messageModel) throws  Exception {
        log.info("Received CSV message from \"training-converter.send_as_json\" queue:\n{}", messageModel.getBody());
        jsonToCsvService.execute(messageModel.getBody());
    }
}
