# 寰途 (Huantu) — 智能旅行路线规划 & 本地旅游社区

> 🚀 AI 驱动的旅行路线规划 App，结合腾讯地图 POI 数据与本地旅游社区，打造一站式的旅行伴侣。

---

## 📋 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
  - [1. 数据库初始化](#1-数据库初始化)
  - [2. 后端启动](#2-后端启动)
  - [3. 前端管理后台启动](#3-前端管理后台启动)
  - [4. Android 客户端启动](#4-android-客户端启动)
- [API 接口概览](#api-接口概览)
- [默认账号](#默认账号)
- [配置说明](#配置说明)
- [开发计划](#开发计划)

---

## 项目简介

**寰途**是一款面向国内旅行者的智能旅行 App，核心功能包括：

| 模块 | 功能 |
|------|------|
| 🧠 **AI 路线生成** | 输入目的地 + 偏好 → AI 自动规划多日行程 |
| 🗺️ **景点探索** | 腾讯地图 POI 搜索、周边发现、必玩榜单、天气查询 |
| 👥 **本地社区** | 游记发布、点赞/收藏/有用互动 |
| ⭐ **收藏夹** | 收藏路线、帖子、景点，随时查看 |
| 📍 **足迹打卡** | GPS 签到，旅行地图可视化 |
| 🛠️ **管理后台** | 景点同步、帖子审核、榜单配置 |

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **后端框架** | Spring Boot | 4.0.7 (Spring Framework 7.0.8) |
| **ORM** | MyBatis | 4.0.1 |
| **数据库** | MySQL | 8.0 |
| **缓存/会话** | Redis | 7.x |
| **地图服务** | 腾讯地图 WebService API | — |
| **密码加密** | BCrypt (spring-security-crypto) | — |
| **AI 模型** | 通义千问 / 文心一言 (可选) | — |
| **管理后台** | Vue 3 + Vite + Element Plus | Vue 3.5 / Element Plus 2.14 |
| **Android** | Java + Hilt + Retrofit | Android Gradle Plugin 8.x |
| **构建工具** | Maven (后端) / Gradle (Android) / npm (前端) | — |
| **语言版本** | Java 17 / JavaScript ES2022 | — |

---

## 项目结构

```
huantu/
├── README.md                        # ← 你正在看的文件
├── .gitignore                       # Git 忽略规则
│
├── backend/                         # Spring Boot 后端服务
│   ├── pom.xml                      # Maven 依赖配置
│   ├── mvnw / mvnw.cmd              # Maven Wrapper（无需安装 Maven）
│   ├── sql/
│   │   └── init.sql                 # 数据库初始化脚本（建表 + 示例数据）
│   └── src/main/
│       ├── java/com/huantu/
│       │   ├── HuantuApplication.java     # 启动入口
│       │   ├── common/                    # 公共类（Result, 异常, 状态码）
│       │   ├── config/                    # 配置类（Redis, WebMvc, 初始化器）
│       │   ├── controller/                # REST 控制器（7个）
│       │   ├── dto/                       # 请求/响应 DTO
│       │   ├── entity/                    # 数据库实体
│       │   ├── interceptor/               # 登录拦截器
│       │   ├── mapper/                    # MyBatis Mapper 接口
│       │   └── service/                   # 业务逻辑层
│       └── resources/
│           ├── application-example.yml    # 配置文件模板（可提交到 Git）
│           ├── application.yml            # 本地配置（含密钥，已 Git 忽略）
│           └── mapper/                    # MyBatis XML 映射文件
│
├── frontend/                        # Vue 3 管理后台
│   ├── package.json                 # npm 依赖
│   ├── vite.config.js               # Vite 构建配置
│   └── src/
│       ├── api/index.js             # Axios HTTP 封装
│       ├── router/index.js          # 路由配置
│       ├── layouts/AdminLayout.vue  # 后台布局
│       └── views/                   # 页面组件
│           ├── Dashboard.vue        # 数据看板
│           ├── ScenicList.vue       # 景点管理
│           ├── PostAudit.vue        # 帖子审核
│           └── RankingConfig.vue    # 榜单配置
│
├── android/                         # Android 原生客户端
│   ├── build.gradle.kts             # Gradle 构建脚本
│   ├── settings.gradle.kts          # 项目设置
│   └── app/src/main/java/com/gdpt/huantu/
│       ├── MainActivity.java        # 主 Activity
│       ├── core/                    # 网络、模型、工具类
│       └── feature/                 # 功能模块
│           ├── auth/                # 登录/注册
│           ├── home/                # 首页
│           ├── trip/                # 行程规划
│           ├── community/           # 社区
│           ├── profile/             # 个人中心
│           └── search/              # 搜索
│
└── docs/                            # 文档（待补充）
```

---

## 环境要求

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| **JDK** | 17+ | 后端运行环境 |
| **Maven** | 3.8+ | 后端构建（或用自带的 `mvnw`） |
| **MySQL** | 8.0+ | 数据库 |
| **Redis** | 6.0+ | 会话缓存，[Windows 版下载](https://github.com/tporadowski/redis/releases) |
| **Node.js** | 18+ | 前端构建 |
| **Android Studio** | Hedgehog+ | Android 开发（可选） |
| **Git** | 2.30+ | 版本管理 |

---

## 快速开始

### 1. 数据库初始化

用 Navicat 或其他 MySQL 客户端执行初始化脚本：

```bash
# 方式一：命令行导入
mysql -u root -p < backend/sql/init.sql

# 方式二：用 Navicat 打开 backend/sql/init.sql，直接运行
```

脚本会自动创建 `huantu` 数据库、8 张表、以及 8 条广州/深圳示例景点数据。

### 2. 后端启动

> ⚠️ **重要**：首次启动前需要创建本地配置文件！

```bash
# 进入后端目录
cd backend

# 复制配置文件模板
cp src/main/resources/application-example.yml src/main/resources/application.yml

# 编辑 application.yml，修改以下两项必填配置：
#   1. spring.datasource.password  → 你的 MySQL 密码
#   2. tencent.map.api-key         → 你的腾讯地图 Key
#      （申请地址：https://lbs.qq.com → 创建应用 → WebService → 获取 Key）

# 启动 Redis（Windows 需手动启动）
# 假设 Redis 安装在 C:\Users\你的用户名\Desktop\redis-windows\
# 在该目录下运行：
redis-server.exe

# 启动后端（在 backend 目录下）
# Windows (Git Bash):
./mvnw spring-boot:run

# macOS / Linux:
./mvnw spring-boot:run

# 验证：访问 http://localhost:8080
```

后端启动成功后：
- 服务运行在 `http://localhost:8080`
- 首次启动会自动创建管理员账号（见下方）
- 日志输出在 `backend/logs/huantu.log`

### 3. 前端管理后台启动

```bash
# 进入前端目录
cd frontend

# 安装依赖（仅首次）
npm install

# 启动开发服务器
npm run dev

# 访问管理后台
# 默认地址：http://localhost:5173
```

管理后台功能：
- **数据看板**：系统概览、腾讯地图同步工具
- **景点管理**：查看/搜索景点、从腾讯地图同步
- **帖子审核**：审核社区内容
- **榜单配置**：配置城市必玩榜单

### 4. Android 客户端启动

```bash
# 用 Android Studio 打开 android/ 目录
# 等待 Gradle 同步完成
# 连接设备或启动模拟器
# 点击 Run 按钮

# 注意：需要在 android/app/src/main/java/com/gdpt/huantu/core/util/Constants.java
# 中修改后端服务地址（BASE_URL）
```

---

## API 接口概览

> 基础路径：`http://localhost:8080`

### 用户模块 `/api/user`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/user/register` | ❌ | 用户注册 |
| POST | `/api/user/login` | ❌ | 用户登录（返回 sessionId） |
| POST | `/api/user/logout` | ✅ | 退出登录 |
| GET | `/api/user/profile` | ✅ | 获取个人信息 |
| PUT | `/api/user/profile` | ✅ | 修改个人信息 |

### 路线模块 `/api/route`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/route/generate` | ✅ | AI 生成旅行路线 |
| GET | `/api/route/list` | ✅ | 我的路线列表 |
| GET | `/api/route/{id}` | ✅ | 路线详情 |
| PUT | `/api/route/{id}` | ✅ | 修改路线 |
| DELETE | `/api/route/{id}` | ✅ | 删除路线 |

### 探索模块 `/api/explore`（公开访问）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/explore/nearby?city=&limit=` | 周边热门景点 |
| GET | `/api/explore/weather?cityCode=` | 天气查询 |
| GET | `/api/explore/rankings?city=&tag=&limit=` | 城市必玩榜单 |
| GET | `/api/explore/search?keyword=&cityCode=&poiType=` | 搜索景点 |
| GET | `/api/explore/nearby-gps?lng=&lat=&limit=` | GPS 周边搜索 |

### 社区模块 `/api/post`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/post/publish` | ✅ | 发布帖子 |
| GET | `/api/post/list` | ❌ | 帖子列表（公开查看） |
| GET | `/api/post/{id}` | ❌ | 帖子详情（公开查看） |
| POST | `/api/post/{id}/interact` | ✅ | 点赞/收藏/有用 |

### 收藏模块 `/api/favorite`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/favorite/add` | ✅ | 添加收藏 |
| DELETE | `/api/favorite/remove` | ✅ | 取消收藏 |
| GET | `/api/favorite/list` | ✅ | 收藏列表 |

### 足迹模块 `/api/footprint`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/footprint/checkin` | ✅ | 签到打卡 |
| GET | `/api/footprint/list` | ✅ | 足迹列表 |
| GET | `/api/footprint/map` | ✅ | 旅行地图 |

### 认证方式

登录成功后返回 `sessionId`，后续请求通过以下任一方式携带：

```http
# 方式一：Header（推荐）
X-Session-Id: your-session-id

# 方式二：Cookie
sessionId=your-session-id
```

Session 有效期：**7 天**（存储在 Redis 中）。

---

## 默认账号

### 管理后台

| 字段 | 值 |
|------|-----|
| 账号 | `admin` |
| 密码 | `123456` |

> 管理员账号在**后端首次启动时自动创建**，无需手动插入数据库。

### 测试账号

使用接口注册或调用 `/api/user/register`：

```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","nickname":"测试用户","password":"123456"}'
```

---

## 配置说明

### 后端配置文件

| 文件 | 用途 | Git 状态 |
|------|------|----------|
| `application-example.yml` | 配置模板，含详细注释 | ✅ 已提交 |
| `application.yml` | 你的本地配置，含真实密钥 | 🔒 已忽略 |

### 必填配置

```yaml
# 数据库密码
spring.datasource.password: YOUR_DB_PASSWORD

# 腾讯地图 Key（申请地址：https://lbs.qq.com）
tencent.map.api-key: YOUR_TENCENT_MAP_KEY
```

### 可选配置

```yaml
# 阿里云 OSS（图片上传，不填则不支持上传）
aliyun.oss.access-key-id: YOUR_OSS_KEY
aliyun.oss.access-key-secret: YOUR_OSS_SECRET

# AI 大模型（路线生成，不填则使用本地规则生成）
ai.api-key: YOUR_AI_KEY
```

### 环境变量覆盖

所有配置项都可以通过环境变量覆盖，便于生产部署：

```bash
export TENCENT_MAP_KEY=your-key-here
export MYSQL_PASSWORD=your-password-here
```

---

## 开发计划

| 阶段 | 内容 | 状态 |
|------|------|------|
| ✅ 基础设施 | 项目骨架、数据库、配置、认证 | 已完成 |
| ✅ 用户系统 | 注册/登录/个人信息 | 已完成 |
| ✅ 路线核心 | AI 路线生成、CRUD | 已完成 |
| ✅ 景点探索 | 腾讯地图 POI、天气、周边搜索 | 已完成 |
| ✅ 社区模块 | 帖子发布、互动、收藏 | 已完成 |
| ✅ 足迹打卡 | 签到、旅行地图 | 已完成 |
| ✅ 管理后台 | 数据看板、景点同步、帖子审核 | 已完成 |
| ✅ Android 客户端 | 完整 App 开发 | 已完成 |
| 🔲 文档补充 | API 文档、数据库设计文档 | 待完成 |
| 🔲 部署上线 | 阿里云 ECS 部署、HTTPS、域名 | 待完成 |
| 🔲 CI/CD | 自动构建、测试、部署流水线 | 待完成 |

---

## 常见问题

### Q: 启动后端报 "port 8080 already in use"？

```bash
# Windows 查看占用端口进程
netstat -ano | findstr :8080

# 强制结束进程（替换 PID）
powershell -Command "Stop-Process -Id PID -Force"
```

### Q: Redis 连接失败？

```bash
# 确保 Redis 已启动，Windows 上需手动运行：
redis-server.exe

# 测试连接：
redis-cli.exe ping
# 返回 PONG 表示正常
```

### Q: 腾讯地图 API 返回空数据或超配额？

- 检查 `application.yml` 中的 `tencent.map.api-key` 是否有效
- 检查腾讯地图控制台的配额余额：https://lbs.qq.com/dev/console/quota
- WebService API 每日有调用次数限制

### Q: 配置文件不生效？

确保 `application.yml` 放在正确位置：
```
backend/src/main/resources/application.yml
```

---

## 许可证

本项目仅用于学习和个人用途。
