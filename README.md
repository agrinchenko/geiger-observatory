# Geiger Observatory

Geiger Observatory is a portfolio-oriented full-stack project for learning Spring Boot and React while building something closer to a real product than a tutorial CRUD app.

The application ingests radiation readings in counts per minute (CPM), stores recent measurements, exposes historical and live APIs, and renders a live dashboard in React. The default mode runs entirely from a simulator so anyone can run the repo without owning the Geiger counter hardware.

## Project goals

- Learn Spring Boot through a real-time ingestion pipeline instead of static CRUD.
- Learn React through a live dashboard with asynchronous updates and stateful UI.
- Keep the system ready for a real USB serial device integration.
- Produce a GitHub repo that is easy to demo and discuss in interviews.

## Architecture

- `backend/`: Spring Boot API, simulator/live device abstraction, in-memory repository, anomaly analysis, REST and Server-Sent Events.
- `frontend/`: React + TypeScript + Vite dashboard consuming the backend.

## MVP features

- Simulator that emits realistic CPM data with occasional spikes.
- REST endpoint for latest reading and recent history.
- SSE endpoint for live readings.
- Rolling statistics and simple anomaly detection.
- React dashboard with status, current reading, live stream, and analysis summary.

## Suggested build sequence

1. Run simulator mode and validate the live dashboard.
2. Replace the simulator with a serial implementation using `jSerialComm`.
3. Add persistence with PostgreSQL or TimescaleDB.
4. Add AI summarization for recent radiation patterns.
5. Containerize with Docker Compose.

## Running locally

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

The backend defaults to `http://localhost:8080` and the frontend defaults to `http://localhost:5173`.

## Switching to the real device

The app runs in simulator mode by default. To talk to the Geiger counter over USB serial, update [/Users/fcmbp/Documents/dev/react/geiger-observatory/backend/src/main/resources/application.yml](/Users/fcmbp/Documents/dev/react/geiger-observatory/backend/src/main/resources/application.yml):

- set `app.device.mode: serial`
- set `app.device.comm-port` to your local serial device name
- keep `app.device.command: "<GETCPM>>"` unless your device protocol differs

The backend will then poll the serial device and expose its connection state through `/api/readings/device`, which the React dashboard displays in the header.

## Next extensions

- Add a serial USB implementation backed by your Geiger counter protocol.
- Persist readings to PostgreSQL.
- Export sessions as CSV.
- Add a lightweight AI summary endpoint over the last N minutes of readings.
