package org.eletra.energy.network.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eletra.energy.network.models.entities.Ticket;
import org.eletra.energy.network.models.enums.TicketStatus;
import org.eletra.energy.network.repositories.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public void finishTicket(String ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(UUID.fromString(ticketId));
        if (ticket.isPresent()) {
            ticket.get().setStatus(TicketStatus.DONE);
            ticketRepository.save(ticket.get());
            log.info("Ticket with ID: {} updated to DONE\n", ticketId);
        }
    }
}
