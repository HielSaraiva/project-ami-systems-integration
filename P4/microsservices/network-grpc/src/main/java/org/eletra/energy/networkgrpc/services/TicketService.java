package org.eletra.energy.networkgrpc.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eletra.energy.networkgrpc.models.entities.Ticket;
import org.eletra.energy.networkgrpc.models.enums.TicketStatus;
import org.eletra.energy.networkgrpc.repositories.TicketRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final JmsTemplate jmsTemplate;

    public String createTicket(String jsonPayload) {
        Ticket ticket = new Ticket(TicketStatus.OPEN, jsonPayload);
        ticketRepository.save(ticket);
        log.info("Ticket created and persisted: {}", ticket);
        return ticket.getId().toString();
    }

    public void     sendTicketId(String ticketId) {
        jmsTemplate.convertAndSend("training-converter.receive_as_json", ticketId);
        log.info("Ticket ID sent to \"training-converter.receive_as_json\" queue: {}", ticketId);
    }
}
