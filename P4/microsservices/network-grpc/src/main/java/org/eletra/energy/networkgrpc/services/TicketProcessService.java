package org.eletra.energy.networkgrpc.services;

import lombok.RequiredArgsConstructor;
import org.eletra.energy.networkgrpc.models.entities.Ticket;
import org.eletra.energy.networkgrpc.models.entities.TicketProcess;
import org.eletra.energy.networkgrpc.models.enums.ProcessStatus;
import org.eletra.energy.networkgrpc.models.enums.ProcessType;
import org.eletra.energy.networkgrpc.repositories.TicketProcessRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TicketProcessService {

    private final TicketProcessRepository ticketProcessRepository;

    private void createProcess(Ticket ticket, ProcessType type) {

        TicketProcess process = new TicketProcess();

        process.setTicket(ticket);
        process.setStatus(ProcessStatus.PENDING);
        process.setType(type);
        process.setStartedAt(Instant.now());

        ticketProcessRepository.save(process);
    }
}
