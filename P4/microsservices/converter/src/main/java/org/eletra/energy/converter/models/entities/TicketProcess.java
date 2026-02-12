package org.eletra.energy.converter.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eletra.energy.converter.models.enums.ProcessStatus;
import org.eletra.energy.converter.models.enums.ProcessType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "process_status", nullable = false)
    private ProcessStatus status;

    @CreationTimestamp
    @Column(name = "started_at")
    private Instant startedAt;

    @UpdateTimestamp
    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "process_type", nullable = false)
    private ProcessType type;

    public TicketProcess(Ticket ticket, ProcessStatus status, ProcessType type) {
        this.ticket = ticket;
        this.status = status;
        this.type = type;
    }
}
