"""Shared configuration for resource collection and model training."""
import os
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
MODEL_DIR = Path(os.environ.get("MODEL_DIR", str(BASE_DIR / "models"))).resolve()


def database_config():
    """Read credentials when connecting; never supply a default password."""
    password = os.environ.get("DB_PASSWORD")
    if not password:
        raise RuntimeError("Set DB_PASSWORD before connecting to MySQL")
    return {
        "host": os.environ.get("DB_HOST", "localhost"),
        "port": int(os.environ.get("DB_PORT", "3306")),
        "user": os.environ.get("DB_USER", "root"),
        "password": password,
        "db": os.environ.get("DB_NAME", "mymonitor"),
        "charset": "utf8mb4",
    }
