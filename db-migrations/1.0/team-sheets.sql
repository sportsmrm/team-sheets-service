CREATE TABLE team_sheets (
    id UUID NOT NULL,
    sequence_nr BIGINT NOT NULL,
    date DATE NOT NULL,
    team_id UUID not null,
    team_name VARCHAR(255) NOT NULL,
    opponent_id UUID NOT NULL,
    opponent_name VARCHAR(255) NOT NULL,
    primary key (id)
);

GRANT
    SELECT, INSERT, UPDATE, DELETE
ON TABLE
    team_sheets
TO
    team_sheets_service;