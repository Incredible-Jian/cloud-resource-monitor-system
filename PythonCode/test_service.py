"""API contract checks; no database, real model or stress workload is used."""
import os
import unittest
from types import SimpleNamespace
from unittest.mock import Mock, patch

import pandas as pd
import config
import serve_cpu_mem_disk as service


class ResourceServiceTests(unittest.TestCase):
    def setUp(self):
        self.client = service.app.test_client()

    def test_invalid_prediction_hours(self):
        with patch.object(service, "load_models") as load:
            for hours in ("abc", "0", "25", "1.5", ""):
                with self.subTest(hours=hours):
                    response = self.client.get("/predict", query_string={"hours": hours})
                    self.assertEqual(response.status_code, 400)
            load.assert_not_called()

    def test_missing_models_do_not_prevent_current_usage(self):
        usage = {"total": 100, "used": 60, "free": 40, "percent": 60}
        with patch.object(service, "get_c_disk_usage", return_value=usage), \
             patch.object(service.psutil, "cpu_percent", return_value=20), \
             patch.object(service.psutil, "virtual_memory", return_value=SimpleNamespace(percent=30)), \
             patch.object(service, "load_models", side_effect=FileNotFoundError):
            self.assertEqual(self.client.get("/current-usage").json,
                             {"cpu": 20, "mem": 30, "disk": 60})
            self.assertEqual(self.client.get("/health").status_code, 503)
            self.assertEqual(self.client.get("/predict").status_code, 503)

    def test_healthy_models(self):
        with patch.object(service, "load_models", return_value=(Mock(), Mock(), Mock())):
            self.assertEqual(self.client.get("/health").json,
                             {"status": "ok", "models_loaded": True})

    def test_prediction_uses_requested_horizon(self):
        model = Mock()
        model.predict.return_value = pd.DataFrame({"yhat": [42.3]})
        usage = {"total": 100, "used": 60, "free": 40, "percent": 60}
        with patch.object(service, "load_models", return_value=(model, model, model)), \
             patch.object(service, "get_c_disk_usage", return_value=usage), \
             patch.object(service.psutil, "cpu_percent", return_value=20), \
             patch.object(service.psutil, "virtual_memory", return_value=SimpleNamespace(percent=30)):
            response = self.client.get("/predict?hours=24")
            self.assertEqual(response.status_code, 200)
            self.assertEqual(response.json["cpu"], 42.3)
            frame = model.predict.call_args.args[0]
            self.assertEqual(len(frame), 25)
            self.assertEqual((frame.ds.iloc[-1] - frame.ds.iloc[0]).total_seconds(), 86400)

    def test_configured_disk_is_reported(self):
        with patch.dict(os.environ, {"MONITOR_DISK": "/test-volume"}), \
             patch.object(service.psutil, "disk_usage",
                          return_value=SimpleNamespace(total=100, used=60, free=40, percent=60)) as disk:
            response = self.client.get("/disk-details")
            self.assertEqual(response.status_code, 200)
            self.assertEqual(response.json["mountpoint"], "/test-volume")
            disk.assert_called_once_with("/test-volume")

    def test_database_password_is_required(self):
        with patch.dict(os.environ, {}, clear=True):
            with self.assertRaisesRegex(RuntimeError, "DB_PASSWORD"):
                config.database_config()

    def test_database_environment_overrides(self):
        values = {"DB_PASSWORD": "test-only", "DB_PORT": "3307", "DB_NAME": "test_db"}
        with patch.dict(os.environ, values, clear=True):
            result = config.database_config()
            self.assertEqual(result["port"], 3307)
            self.assertEqual(result["db"], "test_db")
            self.assertEqual(result["password"], "test-only")

    def test_model_paths_use_configured_directory(self):
        service.load_models.cache_clear()
        try:
            with patch.object(service.joblib, "load", return_value=Mock()) as load:
                service.load_models()
                self.assertEqual([call.args[0] for call in load.call_args_list],
                                 [config.MODEL_DIR / f"prophet_{name}.pkl"
                                  for name in ("cpu", "mem", "disk")])
        finally:
            service.load_models.cache_clear()


if __name__ == "__main__":
    unittest.main()
