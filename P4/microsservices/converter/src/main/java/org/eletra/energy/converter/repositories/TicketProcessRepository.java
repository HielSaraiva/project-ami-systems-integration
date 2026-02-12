package org.eletra.energy.converter.repositories;

import org.eletra.energy.converter.models.entities.TicketProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketProcessRepository extends JpaRepository<TicketProcess, UUID> {

    List<TicketProcess> findByTicketId(UUID ticketId);
}
