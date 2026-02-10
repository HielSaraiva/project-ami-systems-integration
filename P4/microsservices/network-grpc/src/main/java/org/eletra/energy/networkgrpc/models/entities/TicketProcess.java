package org.eletra.energy.networkgrpc.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eletra.energy.networkgrpc.models.enums.ProcessStatus;
import org.eletra.energy.networkgrpc.models.enums.ProcessType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processes")
@Data
@NoArgsConstructor
public class TicketProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @CreationTimestamp
    @Column(name = "started_at")
    private Instant startedAt;

    @UpdateTimestamp
    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private ProcessType type;
}
