package org.eletra.energy.network.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpSession;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


@Log4j2
@RequiredArgsConstructor
@Service
public class CsvFtpService {

    private final DefaultFtpSessionFactory ftpSessionFactory;

    public void execute(String csv) throws Exception {

        FtpSession session = ftpSessionFactory.getSession();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        session.write(inputStream, "data_" + LocalDateTime.now().toString().replace(":", "-") + ".csv");
        session.close();
    }
}
