import pymysql
import random
from datetime import datetime, timedelta
from config import database_config

# ---------- 连接 MySQL ----------
conn = pymysql.connect(
    **database_config(),
    autocommit=False      # 手动批量提交，更快
)
cur = conn.cursor()

# ---------- 获取最后一条时间 ----------
cur.execute("SELECT MAX(mydate) FROM monitor")
last_ts = cur.fetchone()[0]
if last_ts is None:
    base = datetime(2025, 7, 21, 15, 30, 0)
else:
    base = last_ts

rows = []
current = base
for _ in range(5000):
    # 随机步长 30–90 秒
    current += timedelta(seconds=random.randint(30, 90))

    # CPU 各维度（总和≈4–15，空闲=100-总和，这里直接存）
    cpu_us  = round(random.uniform(0.5, 8.0), 1)
    cpu_sys = round(random.uniform(1.0, 8.0), 1)
    cpu_id  = round(cpu_us + cpu_sys + random.uniform(-1, 2), 1)

    # 内存 65%–96% 波动
    mem_total = 16123
    mem_used  = random.randint(10500, 15500)

    # 磁盘 75%–95%
    disk_rate = random.randint(75, 95)

    rows.append((
        current.strftime('%Y-%m-%d %H:%M:%S'),
        '127.0.0.1',
        cpu_us, cpu_sys, cpu_id,
        mem_total, mem_used, disk_rate
    ))

cur.executemany(
    "INSERT INTO monitor (mydate, ip, cpu_us, cpu_sys, cpu_id, mem_total, mem_used, disk_rate) "
    "VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",
    rows
)
conn.commit()
print(f"✅ 已追加 {len(rows)} 条记录，最后时间：{rows[-1][0]}")
cur.close()
conn.close()
