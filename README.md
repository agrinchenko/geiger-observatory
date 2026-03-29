# Geiger Observatory

Geiger Observatory is a full-stack telemetry dashboard for collecting and visualizing radiation measurements from a Geiger counter. It was built as a hands-on way to learn Spring Boot and React through a real-time system instead of a tutorial CRUD app.

The backend ingests counts-per-minute (CPM) readings from either a simulator or a USB serial device, exposes live and historical APIs, and performs lightweight anomaly detection. The frontend renders a live dashboard with connection status, current CPM, recent history, and simple analysis summaries.

## Why this project exists

This project is meant to demonstrate:

- Spring Boot for real-time ingestion, REST APIs, SSE streaming, configuration, and service design.
- React for asynchronous data fetching, live updates, stateful UI, and dashboard composition.
- Hardware integration via a serial USB device.
- A portfolio project that is easy to run in simulator mode and interesting to discuss in interviews.

## What it does

- Polls a Geiger counter for CPM readings over USB serial.
- Falls back to simulator mode so the project is runnable without hardware.
- Streams live readings to the frontend with Server-Sent Events.
- Keeps recent readings in memory for historical views.
- Calculates moving averages and highlights suspicious spikes.
- Exposes device connection state so the UI can show whether live hardware is available.

## Stack

- Backend: Java 21, Spring Boot, Maven, jSerialComm
- Frontend: React, TypeScript, Vite
- Transport: REST + Server-Sent Events
- Current storage: in-memory ring buffer

## Architecture

- `backend/`
  - `device/`: simulator and serial implementations behind a shared `ReadingDevice` interface
  - `ingest/`: polling, persistence to the in-memory repository, live event publishing
  - `analysis/`: rolling statistics and basic anomaly heuristics
  - `api/`: REST and SSE endpoints
- `frontend/`
  - React dashboard for status, metrics, live charting, and recent readings

## Demo modes

### Simulator mode

Default mode. Generates synthetic CPM data with noise and occasional spikes so anyone can run the project locally.

### Serial mode

Uses the Geiger counter protocol from an earlier Java experiment:

- command written to device: `<GETCPM>>`
- response: 2 bytes representing CPM
- baud rate: `115200`

## API

- `GET /api/health`
- `GET /api/readings?limit=50`
- `GET /api/readings/summary`
- `GET /api/readings/analysis`
- `GET /api/readings/device`
- `GET /api/readings/stream`

## Running locally

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

- Backend: `http://localhost:8080`
- Frontend: `http://localhost:5173`

## Switching to the real device

The app runs in simulator mode by default. To switch to the Geiger counter, edit `backend/src/main/resources/application.yml`:

- set `app.device.mode: serial`
- set `app.device.comm-port` to your local serial device name
- keep `app.device.command: "<GETCPM>>"` unless your device protocol differs

The backend will then poll the serial device and expose its connection state through `GET /api/readings/device`, which the React dashboard shows in the header.

## Current limitations

- Readings are stored in memory only.
- Anomaly detection is intentionally simple.
- Serial device handling is polling-based and assumes a 2-byte CPM response.
- There is no authentication or multi-user support.

## Roadmap

- Persist readings to PostgreSQL or TimescaleDB.
- Add export to CSV.
- Add richer device diagnostics and reconnect behavior.
- Add AI summarization for recent radiation patterns.
- Add Docker Compose for one-command startup.
- Add tests around serial parsing and anomaly detection.

## Why this is portfolio-worthy

This project shows a realistic full-stack flow:

- hardware input
- backend ingestion and live APIs
- frontend visualization
- observability-style metrics
- clear room for production-oriented extensions

It is also practical to demo because simulator mode makes the app runnable even when the physical Geiger counter is not attached.
