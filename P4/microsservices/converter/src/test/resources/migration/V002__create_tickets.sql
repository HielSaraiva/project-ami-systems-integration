CREATE TABLE tickets
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status     ticket_status NOT NULL,
    payload    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);