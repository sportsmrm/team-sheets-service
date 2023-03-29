# Team Sheets Service

This repository contains the Sports Member Relationship Manager's Team Sheets Service.

This service is completely over engineered using the CQRS with event sourcing functionality provided by 
[Apache Pekko's Persistence Module](https://pekko.apache.org/docs/pekko/current/typed/persistence.html).

## Development

### Prerequisites

1. Docker witt the Docker Compose plugin.

### Running Unit Tests

1. `docker compose run dev_env`
2. `test`