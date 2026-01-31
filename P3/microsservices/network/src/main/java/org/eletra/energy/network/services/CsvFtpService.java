package org.eletra.energy.network.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.net.ftp.FTPFile;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpSession;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;


@Log4j2
@RequiredArgsConstructor
@Service
public class CsvFtpService {

    private final DefaultFtpSessionFactory ftpSessionFactory;

    public void execute(String csv) throws Exception {

        FtpSession session = ftpSessionFactory.getSession();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());
        session.write(inputStream, "data_" + LocalDateTime.now().toString().replace(":", "-") + ".csv");
        session.close();
    }
}
