CREATE TYPE ticket_status AS ENUM (
    'OPEN',
    'IN_PROGRESS',
    'DONE',
    'FAILED'
);

CREATE TYPE process_status AS ENUM (
    'PENDING',
    'PROCESSING',
    'SUCCESS',
    'ERROR'
);
