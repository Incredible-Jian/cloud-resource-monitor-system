from flask import Flask, request, jsonify
import joblib
import pandas as pd
import psutil
from datetime import datetime, timedelta
import os

app = Flask(__name__)

# 加载模型 - 修改为C盘专用模型
cpu_model = joblib.load('models/prophet_cpu.pkl')
mem_model = joblib.load('models/prophet_mem.pkl')
disk_model = joblib.load('models/prophet_disk.pkl')

def get_c_disk_usage():
    """获取C盘使用情况"""
    for partition in psutil.disk_partitions():
        if 'c:' in partition.mountpoint.lower():
            usage = psutil.disk_usage(partition.mountpoint)
            return {
                'total': usage.total,
                'used': usage.used,
                'free': usage.free,
                'percent': usage.percent
            }
    raise Exception("C盘未找到")

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
        hours = min(24, max(1, int(request.args.get("hours", 1))))

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
            "mountpoint": "C:"
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/health", methods=['GET'])
def health():
    return jsonify({"status": "ok", "models_loaded": bool(cpu_model and mem_model and disk_model)})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)