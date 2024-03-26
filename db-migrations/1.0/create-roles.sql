CREATE ROLE team_sheets_service NOLOGIN;

CREATE ROLE team_sheets_service_user
    IN ROLE team_sheets_service
    LOGIN
    PASSWORD '${db.user.password}';

GRANT CONNECT, TEMPORARY ON DATABASE ${db.name}
    TO "team_sheets_service";

REVOKE CREATE, CONNECT, TEMPORARY ON DATABASE ${db.name} FROM PUBLIC;