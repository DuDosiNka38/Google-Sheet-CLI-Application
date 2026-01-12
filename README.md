# Issue Track System CLI

A Java-based command-line application for managing issues using Google Sheets as a storage backend.  
Built with Spring Boot and distributed as a Docker container.

## Features

- Create issues with text descriptions
- Optional parent issue support
- Update issue status (OPEN, IN_PROGRESS, CLOSED)
- List issues by status
- Persistent storage via Google Sheets
- Interactive CLI execution

## Requirements

- Java 17+
- Docker
- Google Sheets API service account credentials (JSON)

> The credentials file is not included in the repository and must be provided at runtime.

## Build

./gradlew clean bootJar
docker build -t issue-tracker-cli .
## Run
docker run --rm -it \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e app.googleSheets.credentialsPath=/app/config.json \
  -v /path/to/credentials.json:/app/config.json:ro \
  issue-tracker-cli


Replace /path/to/credentials.json with the path to your Google service account key.

## Usage
create "Issue description" [parentId]
update <issueId> <OPEN|IN_PROGRESS|CLOSED>
list <OPEN|IN_PROGRESS|CLOSED>
exit

## Example
create "Fix ETL pipeline"
update ISSUE-1 IN_PROGRESS
list OPEN


## Notes
 - Multi-word descriptions must be wrapped in quotes.
- Google Sheets access is abstracted via a repository layer.
- Credentials are injected via environment variables and Docker volume mounting for security.
