import os
import pandas as pd
from prophet import Prophet
from prophet.diagnostics import cross_validation, performance_metrics
from sklearn.model_selection import ParameterGrid
import joblib
import pymysql

# 确保模型目录存在
os.makedirs('models', exist_ok=True)

# 数据库连接
conn = pymysql.connect(host='localhost', user='root', password='123456', db='mymonitor', charset='utf8mb4')

# 参数网格
param_grid = {
    'changepoint_prior_scale': [0.01, 0.05, 0.1],
    'seasonality_prior_scale': [0.1, 1.0, 10.0]
}

# 交叉验证参数
cv_params = {
    'initial': '144 hours',  # 初始训练集大小：6 天
    'period': '24 hours',    # 每次移动的步长：1 天
    'horizon': '24 hours'    # 预测范围：1 天
}

# 训练模型并选择最优参数
def train_model(query, model_name, y_column):
    df = pd.read_sql(query, conn)
    df['ds'] = pd.to_datetime(df['ds'])

    best_params = None
    best_mae = float('inf')

    for params in ParameterGrid(param_grid):
        model = Prophet(**params)
        model.fit(df)
        df_cv = cross_validation(model, **cv_params)
        df_p = performance_metrics(df_cv)
        if df_p['mae'].mean() < best_mae:
            best_mae = df_p['mae'].mean()
            best_params = params

    print(f"Best parameters for {model_name}: {best_params}")
    model = Prophet(**best_params)
    model.fit(df)
    joblib.dump(model, f'models/{model_name}.pkl')

# 训练 CPU 模型
train_model("SELECT mydate AS ds, cpu_id AS y FROM monitor ORDER BY mydate", 'prophet_cpu', 'cpu_id')

# 训练 内存 模型
train_model("SELECT mydate AS ds, ROUND(mem_used*100.0/mem_total,2) AS y FROM monitor ORDER BY mydate", 'prophet_mem', 'mem_used')

# 训练 磁盘 模型
train_model("SELECT mydate AS ds, disk_rate AS y FROM monitor ORDER BY mydate", 'prophet_disk', 'disk_rate')

print("✅ 所有模型训练完成，共 12000+ 条")