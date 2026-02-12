package org.eletra.energy.network.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eletra.energy.network.models.entities.Ticket;
import org.eletra.energy.network.models.entities.TicketProcess;
import org.eletra.energy.network.models.enums.ProcessStatus;
import org.eletra.energy.network.models.enums.ProcessType;
import org.eletra.energy.network.repositories.TicketProcessRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class TicketProcessService {

    private final TicketProcessRepository ticketProcessRepository;
    private final JmsTemplate jmsTemplate;

    public String createProcess(Ticket ticket, ProcessStatus status, ProcessType type) {
        TicketProcess ticketProcess = new TicketProcess(ticket, status, type);
        ticketProcessRepository.save(ticketProcess);
        log.info("TicketProcess created and persisted: {}", ticketProcess);
        return ticketProcess.getId().toString();
    }

    public void finishProcess(String processId, String payload) {
        Optional<TicketProcess> process = ticketProcessRepository.findById(UUID.fromString(processId));
        if (process.isPresent()) {
            process.get().setStatus(ProcessStatus.SUCCESS);
            process.get().setPayload(payload);
            ticketProcessRepository.save(process.get());
            log.info("TicketProcess with ID: {} updated to SUCCESS with payload: {}", processId, payload);
        }
    }
}
