package org.eletra.energy.network.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eletra.energy.network.services.CsvFtpService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;

@Log4j2
@RequiredArgsConstructor
@Controller
public class JmsController {
    private final CsvFtpService csvFtpService;

    @JmsListener(destination = "training-converter.send_as_csv")
    public void receiveCsv(String message) throws Exception {
        log.info("Received CSV message from \"training-converter.send_as_csv\" queue:\n{}", message);
        csvFtpService.execute(message);
    }
}
