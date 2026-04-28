# WiseCloud MDM — 移动设备管理平台

基于 WiseCloud 开放 API 的移动设备管理应用后端，面向商用 Android 设备（POS 终端等）的远程管理场景。

## 核心功能

- **用户认证** — 注册、登录、JWT Token 鉴权
- **设备管理** — SN 批量导入、设备概览（在线率统计）、SN 模糊搜索、单设备实时详情
- **应用管理** — APK 上传到 WiseCloud 应用库、已上传应用列表查询
- **任务下发** — 批量安装/卸载指令推送、任务列表（含进度）、任务详情（按执行状态分组）
- **WiseCloud SDK 封装** — 自动分批（≤300/批）、统一异常处理、Sandbox 模式

## 技术栈

- Java 24 + Spring Boot 3.4.4
- Spring Security + JWT（jjwt 0.12.5）
- Spring Data JPA + Hibernate
- MySQL 8.x（生产） / H2（本地开发）
- WiseCloud OpenAPI Java SDK
- jqwik（属性测试）

## 项目结构

```
workshop/wisecloud-project/
├── src/main/java/com/wisecloud/mdm/
│   ├── config/          # SecurityConfig, WiseCloudConfig, CorsConfig
│   ├── controller/      # AuthController, DeviceController, ApplicationController, TaskController
│   ├── service/         # AuthService, DeviceService, ApplicationService, TaskService, WiseCloudService
│   ├── repository/      # JPA Repository 接口
│   ├── entity/          # User, Device, Application, Task
│   ├── dto/             # 请求/响应 DTO
│   ├── security/        # JwtTokenProvider, JwtAuthenticationFilter
│   ├── exception/       # GlobalExceptionHandler, BusinessException, WiseCloudApiException
│   └── MdmApplication.java
├── src/main/resources/
│   ├── application.yml       # 主配置（MySQL + 环境变量注入）
│   ├── application-dev.yml   # 开发配置（H2 内存数据库）
│   └── application-local.yml # 本地配置模板（已 gitignore）
├── k8s/                 # Kubernetes 部署文件
├── Dockerfile           # 多阶段构建
└── pom.xml
```

## 快速启动

### 环境要求

- Java 17+（推荐 24）
- Maven 3.8+
- MySQL 8.x（生产环境）或无需数据库（dev profile 使用 H2）

### 本地开发（H2 内存数据库，无需 MySQL）

```bash
cd workshop/wisecloud-project

# 打包（跳过测试）
mvn package -Dmaven.test.skip=true

# 启动（dev profile 使用 H2 内存数据库）
java -jar target/mdm-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

服务启动后：
- API 地址：http://localhost:8080
- H2 控制台：http://localhost:8080/h2-console（JDBC URL: `jdbc:h2:mem:wisecloud_mdm`，用户名 `sa`，密码为空）
- 健康检查：http://localhost:8080/actuator/health

### 生产环境（MySQL）

1. 创建 MySQL 数据库：
```sql
CREATE DATABASE wisecloud_mdm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 设置环境变量：
```bash
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export JWT_SECRET=your-jwt-secret-key-must-be-at-least-256-bits-long
export WISECLOUD_ACCESS_KEY_ID=your_ak
export WISECLOUD_ACCESS_KEY_SECRET=your_sk
```

3. 启动：
```bash
java -jar target/mdm-0.0.1-SNAPSHOT.jar
```

### WiseCloud SDK

项目使用 stub 类模拟 SDK 接口。接入真实 SDK 时，将 JAR 安装到本地 Maven 仓库：

```bash
mvn install:install-file \
  -Dfile=path/to/wisecloud-openapi-sdk-1.0.0.jar \
  -DgroupId=com.lianwuzizai \
  -DartifactId=wisecloud-openapi-sdk \
  -Dversion=1.0.0 \
  -Dpackaging=jar
```

然后取消 `pom.xml` 中 SDK 依赖的注释，并删除 `src/main/java/com/wiseasy/` 目录下的 stub 类。

## API 概览

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 否 |
| POST | `/api/auth/login` | 用户登录 | 否 |
| POST | `/api/devices/import` | 批量导入设备 | 是 |
| GET | `/api/devices/overview` | 设备概览统计 | 是 |
| GET | `/api/devices/search?keyword=xxx` | SN 模糊搜索 | 是 |
| GET | `/api/devices/{sn}/detail` | 单设备实时详情 | 是 |
| POST | `/api/applications/upload` | 上传 APK | 是 |
| GET | `/api/applications` | 已上传应用列表 | 是 |
| POST | `/api/tasks/install` | 下发安装任务 | 是 |
| POST | `/api/tasks/uninstall` | 下发卸载任务 | 是 |
| GET | `/api/tasks` | 任务列表（含进度） | 是 |
| GET | `/api/tasks/{traceId}/details` | 任务执行详情 | 是 |

认证接口需在请求头携带：`Authorization: Bearer <token>`

## Docker 部署

```bash
cd workshop/wisecloud-project
docker build -t wisecloud-mdm .
docker run -p 8080:8080 \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=changeme \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/wisecloud_mdm \
  -e JWT_SECRET=your-secret \
  -e WISECLOUD_ACCESS_KEY_ID=your_ak \
  -e WISECLOUD_ACCESS_KEY_SECRET=your_sk \
  wisecloud-mdm
```

Kubernetes 部署文件在 `k8s/` 目录下。
