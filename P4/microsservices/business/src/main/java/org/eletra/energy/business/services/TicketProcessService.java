package org.eletra.energy.business.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eletra.energy.business.models.entities.Ticket;
import org.eletra.energy.business.models.entities.TicketProcess;
import org.eletra.energy.business.models.enums.ProcessStatus;
import org.eletra.energy.business.models.enums.ProcessType;
import org.eletra.energy.business.repositories.TicketProcessRepository;
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

    public void sendTicketProcessId(String processId) {
        jmsTemplate.convertAndSend("training-converter.send_as_json", processId);
        log.info("Ticket Process ID sent to \"training-converter.send_as_json\"! queue: {}\n", processId);
    }
}
