package org.eletra.energy.network.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.eletra.energy.network.models.entities.TicketProcess;
import org.eletra.energy.network.models.enums.ProcessStatus;
import org.eletra.energy.network.models.enums.ProcessType;
import org.eletra.energy.network.repositories.TicketProcessRepository;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpSession;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Log4j2
@RequiredArgsConstructor
@Service
public class CsvFtpService {

    private final DefaultFtpSessionFactory ftpSessionFactory;
    private final TicketProcessRepository ticketProcessRepository;
    private final TicketProcessService ticketProcessService;
    private final TicketService ticketService;

    public void execute(String processIdInput) throws Exception {

        Optional<TicketProcess> ticketProcess = ticketProcessRepository.findById(UUID.fromString(processIdInput));
        String csv;
        if (ticketProcess.isPresent()) {
            csv = ticketProcess.get().getPayload();

            String processId = ticketProcessService.createProcess(ticketProcess.get().getTicket(), ProcessStatus.PROCESSING, ProcessType.NETWORK);

            FtpSession session = ftpSessionFactory.getSession();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
            session.write(inputStream, "data_" + LocalDateTime.now().toString().replace(":", "-") + ".csv");
            session.close();

            ticketProcessService.finishProcess(processId, csv);
            ticketService.finishTicket(ticketProcess.get().getTicket().getId().toString());
        }
    }
}
