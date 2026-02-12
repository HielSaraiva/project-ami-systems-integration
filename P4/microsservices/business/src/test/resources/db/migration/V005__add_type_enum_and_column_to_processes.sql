CREATE TYPE process_type AS ENUM (
    'BUSINESS',
    'CONVERTER',
    'NETWORK'
);

ALTER TABLE processes
    ADD COLUMN type process_type NOT NULL;