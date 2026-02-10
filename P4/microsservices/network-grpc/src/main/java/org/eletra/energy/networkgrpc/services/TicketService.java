package org.eletra.energy.networkgrpc.services;

import lombok.RequiredArgsConstructor;
import org.eletra.energy.networkgrpc.models.entities.Ticket;
import org.eletra.energy.networkgrpc.models.entities.TicketProcess;
import org.eletra.energy.networkgrpc.models.enums.ProcessStatus;
import org.eletra.energy.networkgrpc.models.enums.ProcessType;
import org.eletra.energy.networkgrpc.models.enums.TicketStatus;
import org.eletra.energy.networkgrpc.repositories.TicketProcessRepository;
import org.eletra.energy.networkgrpc.repositories.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public String createTicket(String jsonPayload) {

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPayload(jsonPayload);
        ticket.setCreatedAt(Instant.now());

        ticketRepository.save(ticket);

        return ticket.getId().toString();
    }
}
