CREATE TABLE IF NOT EXISTS event_journal(
    slice INT NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    persistence_id VARCHAR(255) NOT NULL,
    seq_nr BIGINT NOT NULL,
    db_timestamp timestamp with time zone NOT NULL,

    event_ser_id INTEGER NOT NULL,
    event_ser_manifest VARCHAR(255) NOT NULL,
    event_payload BYTEA NOT NULL,

    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    writer VARCHAR(255) NOT NULL,
    adapter_manifest VARCHAR(255),
    tags TEXT ARRAY,

    meta_ser_id INTEGER,
    meta_ser_manifest VARCHAR(255),
    meta_payload BYTEA,

    PRIMARY KEY(persistence_id, seq_nr)
);

-- `event_journal_slice_idx` is only needed if the slice based queries are used
CREATE INDEX IF NOT EXISTS event_journal_slice_idx ON event_journal(slice, entity_type, db_timestamp, seq_nr);

GRANT
    SELECT, INSERT, UPDATE, DELETE
ON TABLE
    event_journal
TO
    team_sheets_service;