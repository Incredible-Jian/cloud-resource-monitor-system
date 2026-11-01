from flask import Flask, request, jsonify
import joblib
import pandas as pd
import psutil
from datetime import datetime, timedelta
import os
from functools import lru_cache
from config import MODEL_DIR

app = Flask(__name__)

@lru_cache(maxsize=1)
def load_models():
    """Resolve model paths independently of the current working directory."""
    return tuple(joblib.load(MODEL_DIR / f"prophet_{name}.pkl")
                 for name in ("cpu", "mem", "disk"))


def disk_path():
    return os.environ.get("MONITOR_DISK", "C:\\" if os.name == "nt" else "/")

def get_c_disk_usage():
    """获取配置目录所在磁盘的使用情况（保留原函数名兼容调用）。"""
    usage = psutil.disk_usage(disk_path())
    return {'total': usage.total, 'used': usage.used,
            'free': usage.free, 'percent': usage.percent}

@app.route("/current-usage", methods=['GET'])
def current_usage():
    """获取C盘当前资源使用率"""
    try:
        return jsonify({
            "cpu": psutil.cpu_percent(interval=1),
            "mem": psutil.virtual_memory().percent,
            "disk": get_c_disk_usage()['percent']
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/predict", methods=['GET'])
def predict():
    """预测C盘未来资源使用"""
    try:
        hours = int(request.args.get("hours", "1"))
    except (ValueError, TypeError):
        return jsonify({"error": "hours must be an integer between 1 and 24"}), 400
    if not 1 <= hours <= 24:
        return jsonify({"error": "hours must be an integer between 1 and 24"}), 400

    try:
        cpu_model, mem_model, disk_model = load_models()
    except Exception:
        app.logger.exception("Cannot load prediction models")
        return jsonify({"error": "Prediction models unavailable; train compatible models first"}), 503

    try:

        # 获取当前C盘数据
        disk_usage = get_c_disk_usage()
        current_data = {
            "cpu": psutil.cpu_percent(interval=1),
            "mem": psutil.virtual_memory().percent,
            "disk": disk_usage['percent']
        }

        # 创建未来时间点
        now = datetime.now()
        future_dates = [now + timedelta(hours=i) for i in range(hours + 1)]

        # 准备预测数据框架
        future_df = pd.DataFrame({
            'ds': future_dates,
            'current_cpu': [current_data['cpu']] + [None] * hours,
            'current_mem': [current_data['mem']] + [None] * hours,
            'current_disk': [current_data['disk']] + [None] * hours
        })

        # 执行预测
        cpu_forecast = cpu_model.predict(future_df)
        mem_forecast = mem_model.predict(future_df)
        disk_forecast = disk_model.predict(future_df)

        # 获取预测结果（最后一个预测值）
        return jsonify({
            "cpu": round(cpu_forecast.tail(1)['yhat'].values[0], 1),
            "mem": round(mem_forecast.tail(1)['yhat'].values[0], 1),
            "disk": round(disk_forecast.tail(1)['yhat'].values[0], 1),
            "current_disk_free": round(disk_usage['free'] / (1024**3), 2)  # 转换为GB
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/disk-details", methods=['GET'])
def disk_details():
    """获取C盘详细使用信息"""
    try:
        usage = get_c_disk_usage()
        return jsonify({
            "total_gb": round(usage['total'] / (1024**3), 2),
            "used_gb": round(usage['used'] / (1024**3), 2),
            "free_gb": round(usage['free'] / (1024**3), 2),
            "percent": usage['percent'],
            "mountpoint": disk_path()
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/health", methods=['GET'])
def health():
    try:
        load_models()
    except Exception:
        app.logger.exception("Prediction model health check failed")
        return jsonify({"status": "unavailable", "models_loaded": False}), 503
    return jsonify({"status": "ok", "models_loaded": True})

if __name__ == '__main__':
    app.run(host=os.environ.get('FLASK_HOST', '127.0.0.1'),
            port=int(os.environ.get('FLASK_PORT', '5000')))
