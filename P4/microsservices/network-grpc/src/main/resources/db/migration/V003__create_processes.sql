CREATE TABLE processes
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id   UUID           NOT NULL,
    status      process_status NOT NULL,
    started_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,

    CONSTRAINT fk_ticket
        FOREIGN KEY (ticket_id)
            REFERENCES tickets (id)
);