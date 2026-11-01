# Cloud Resource Monitoring and Anomalous Access Control

[简体中文](README.md) | [English](README.en.md)

![Java](https://img.shields.io/badge/Java-8-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.3-6DB33F?logo=springboot&logoColor=white)
![Flask](https://img.shields.io/badge/API-Flask-000000?logo=flask)
![Prophet](https://img.shields.io/badge/Forecasting-Prophet-3776AB)

A resource monitoring course project built with Spring Boot, MyBatis, and Flask. It collects CPU, memory, and disk usage, forecasts resource trends with Prophet time-series models, and provides access logs and IP blocking management.

## Features

- Query and display CPU, memory, and disk metrics.
- Flask endpoints for current usage, disk details, and forecasts 1–24 hours ahead.
- Prophet training with parameter grids and time-series cross-validation.
- Access logging, request-frequency detection, IP blocking, and unblocking.
- User login and management.

Forecasting uses Prophet; access control uses request-frequency rules. Predictions are intended for demonstration. No accuracy report on an independent test set is provided.

## Structure and stack

```text
IdeaCode/                   Spring Boot 2.7.3 / Java 8 / Maven
  src/main/java/            Controllers, services, MyBatis mappers, entities
  src/main/resources/       application.yml, templates, static assets
PythonCode/                 Flask API, collection scripts, Prophet training
  config.py                 Environment variables and model paths
  serve_cpu_mem_disk.py     Resource queries and prediction service
  train.py                  Read historical MySQL data and train models
  insertdata.py             Insert simulated data (run manually)
  real_random_stress.py     Local stress and collection experiment (manual)
  models/                   Local model files
```

## Prerequisites

You need JDK 8, Maven, MySQL, and a Python environment that supports the dependencies in [`requirements.txt`](PythonCode/requirements.txt).

**A complete database schema and initialization script is currently missing, so the full application cannot start against an empty database.** Export sanitized table definitions and required demonstration data from the original development environment first. Mappers reference tables including `monitor` and `myuser`; use their SQL to check the schema rather than inferring complete definitions from table names.

### 1. Configure MySQL

Set environment variables in PowerShell. Configure them in each terminal used to start Java or database-dependent Python scripts:

```powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "mymonitor"
$env:DB_USER = "root"
$env:DB_PASSWORD = "your-local-database-password"
```

[.env.example](.env.example) is a reference template. The application does not automatically load `.env` files.

### 2. Start the Python service

From the repository root:

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -r PythonCode/requirements.txt
python PythonCode/serve_cpu_mem_disk.py
```

The service listens on `127.0.0.1:5000` by default. It monitors the C drive on Windows and the root filesystem on other systems; use `MONITOR_DISK` to select a path. Models are loaded from `models/` beside the script; set `MODEL_DIR` to use an absolute directory elsewhere.

The `.pkl` files are local training artifacts. Ignore rules exclude untracked models; models already tracked remotely still need separate review when publishing. Only load models from trusted sources. If models are missing or incompatible with your environment, prepare historical data and run `python PythonCode/train.py`. Cross-validation requires at least the six-day initial training window and one-day forecast window, plus enough data to create valid splits.

### 3. Start the Java application

```powershell
cd IdeaCode
mvn compile spring-boot:run
```

Open <http://localhost:8080>. Java connects to `http://localhost:5000` by default; override it with `PYTHON_API_URL`. The project retains a Gauss JDBC dependency. If Maven cannot resolve it, the original course driver repository or local installation configuration is needed.

## API

| Service | Method and path | Purpose |
| --- | --- | --- |
| Flask | `GET /health` | Check model loading; return 503 if unavailable |
| Flask | `GET /current-usage` | Current CPU, memory, and disk usage |
| Flask | `GET /disk-details` | Disk capacity and usage |
| Flask | `GET /predict?hours=1` | Forecast; hours must be an integer from 1 to 24 |
| Spring Boot | `GET /api/resources/health` | Python service health check |
| Spring Boot | `GET /api/resources/current` | Forward a resource query |
| Spring Boot | `GET /api/resources/predict?hours=1` | Forward a prediction request |

`insertdata.py` writes to the database. `real_random_stress.py` continuously generates CPU and memory load. Neither script is part of normal startup.

## Verification

From the repository root:

```powershell
python -m unittest discover -s PythonCode -p test_service.py -v
```

The eight existing tests use mocked models and resource readings. They cover input validation, unavailable models, health checks, forecast horizons, disk paths, database environment variables, and model file paths. They passed during the repository cleanup. A complete Java build, database integration, and real-model accuracy have not been verified.

## Project scope

This is course demonstration code. Login still compares passwords directly; production use requires password hashing, authentication and authorization review, and deployment configuration. Database initialization scripts, reproducible model experiments, and interface screenshots remain to be added.

## License

No repository license has been specified. Third-party dependencies retain their respective licenses.
