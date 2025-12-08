import time
import random
import threading
import numpy as np
import psutil
import pymysql
from datetime import datetime

# ---------- 数据库 ----------
conn = pymysql.connect(host='localhost', user='root', password='123456', db='mymonitor', charset='utf8mb4')
cur = conn.cursor()

# ---------- 随机压力函数 ----------
def stress_once():
    cpu_sec = random.randint(3, 20)        # 持续 3-20 秒
    mem_mb  = random.randint(100, 2000)    # 占 100-2000 MB
    threads = []

    # CPU 压力
    def cpu_task():
        end = time.time() + cpu_sec
        while time.time() < end:
            _ = np.linalg.svd(np.random.rand(500, 500))

    # 内存压力
    def mem_task():
        data = [0] * (mem_mb * 1024 * 1024 // 8)
        time.sleep(cpu_sec - 1)            # 与 CPU 同时结束
        del data

    threads.append(threading.Thread(target=cpu_task))
    threads.append(threading.Thread(target=mem_task))
    for t in threads: t.start()
    for t in threads: t.join()

# ---------- 主循环 ----------
while True:
    stress_once()

    # 采集并写入
    now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    cpu = psutil.cpu_times_percent(interval=1)
    mem = psutil.virtual_memory()
    disk = psutil.disk_usage('C:\\')

    cur.execute(
        "INSERT INTO monitor (mydate,ip,cpu_us,cpu_sys,cpu_id,mem_total,mem_used,disk_rate) "
        "VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",
        (now, '127.0.0.1', cpu.user, cpu.system, 100 - cpu.idle,
         mem.total // 1024 // 1024, mem.used // 1024 // 1024, int(disk.percent))
    )
    conn.commit()
    print(f"[{now}] 随机压力完成")

    # 随机休息 30-180 秒（模拟人类/业务随机）
    time.sleep(random.randint(30, 60))