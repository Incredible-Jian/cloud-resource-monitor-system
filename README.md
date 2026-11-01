# 云资源监控与异常访问控制系统

基于 Spring Boot、MyBatis 和 Flask 的资源监控课程项目，采集 CPU、内存与磁盘使用情况，通过 Prophet 时间序列模型预测资源变化，并提供访问日志与 IP 封禁管理。

## 已实现功能

- CPU、内存、磁盘资源查询及页面展示。
- Flask 提供当前使用率、磁盘详情及未来 1–24 小时的预测接口。
- Prophet 模型训练，使用参数网格与时间序列交叉验证选择参数。
- 访问日志记录、访问频率检测、IP 封禁与解封。
- 用户登录及用户管理。

当前源码没有提供 LSTM、Drools、Docker 部署或邮件/钉钉告警实现。预测结果用于演示，未提供独立测试集上的精度报告。

## 目录与技术

```text
IdeaCode/                   Spring Boot 2.7.3 / Java 8 / Maven
  src/main/java/            控制器、服务、MyBatis Mapper 与实体
  src/main/resources/       application.yml、模板与静态文件
PythonCode/                 Flask API、采集脚本与 Prophet 训练
  config.py                 环境变量与模型路径配置
  serve_cpu_mem_disk.py     资源查询与预测服务
  train.py                  从 MySQL 读取历史记录并训练
  insertdata.py             向数据库写入模拟数据（手动执行）
  real_random_stress.py     本机压力采集实验（手动执行）
  models/                   本地模型文件
```

## 运行准备

需要 JDK 8、Maven、MySQL，以及可安装上述 Python 依赖的 Python 环境。

**当前仓库没有完整数据库建表与初始化脚本，因此不能从空数据库直接启动完整系统。** 需先从原开发环境导出脱敏后的表结构及必要演示数据。Mapper 使用 `monitor`、`myuser` 等表，请以源码 SQL 为依据核对；不要凭名称自行推测完整表结构。

### 1. 配置数据库

在 PowerShell 中设置环境变量，启动 Java 和训练脚本的终端都需要配置：

```powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "mymonitor"
$env:DB_USER = "root"
$env:DB_PASSWORD = "你的本地数据库密码"
```

[.env.example](.env.example) 仅是配置参考，程序不会自动加载 `.env`。

### 2. 启动 Python 服务

从仓库根目录执行：

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -r PythonCode/requirements.txt
python PythonCode/serve_cpu_mem_disk.py
```

默认监听 `127.0.0.1:5000`。磁盘默认选择 Windows 的 C 盘，其他系统选择根目录，可用 `MONITOR_DISK` 指定路径。模型从脚本目录下的 `models/` 读取，也可用 `MODEL_DIR` 指定绝对目录。

当前目录中的 `.pkl` 是本地模型产物，忽略规则会排除未被 Git 跟踪的模型；远程已有模型仍需在发布时单独核对。只加载可信来源的模型；运行环境不兼容或模型缺失时，在准备好历史数据后执行 `python PythonCode/train.py` 重新训练。交叉验证至少需要覆盖 6 天训练窗口和 1 天预测窗口，实际还需足够的数据用于划分。

### 3. 启动 Java 服务

```powershell
cd IdeaCode
mvn compile spring-boot:run
```

访问 <http://localhost:8080>。Java 默认访问 `http://localhost:5000`，可用 `PYTHON_API_URL` 修改。项目还保留 Gauss JDBC 依赖，若 Maven 无法解析，需要原课程环境的驱动仓库或本地安装配置。

## 接口

| 服务 | 方法与路径 | 用途 |
| --- | --- | --- |
| Flask | `GET /health` | 检查模型能否加载；不可用时返回 503 |
| Flask | `GET /current-usage` | 当前 CPU、内存、磁盘使用率 |
| Flask | `GET /disk-details` | 磁盘容量及占用 |
| Flask | `GET /predict?hours=1` | 资源预测，hours 为 1–24 的整数 |
| Spring Boot | `GET /api/resources/health` | Python 服务健康检查 |
| Spring Boot | `GET /api/resources/current` | 转发资源查询 |
| Spring Boot | `GET /api/resources/predict?hours=1` | 转发预测请求 |

`insertdata.py` 会写入数据库，`real_random_stress.py` 会持续产生 CPU/内存负载，二者均不是日常启动步骤。

## 项目边界

这是课程演示代码。登录仍使用直接密码匹配，生产使用前还需完善密码哈希、身份鉴权、权限校验和部署配置。数据库初始化脚本、模型复现实验及界面截图仍待补充。

仓库尚未指定许可证；第三方依赖沿用各自许可证。
