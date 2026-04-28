# 需求文档 — WiseCloud 移动设备管理应用（MDM）

## 简介

本项目基于 WiseCloud 开放 API 开发一个移动设备管理应用，包含 Spring Boot 后端和 Android 客户端两部分。该应用面向商用 Android 设备（如 POS 终端）的远程管理场景，核心功能涵盖：用户登录注册、设备导入与状态监控、APK 应用的远程安装与卸载、任务执行状态的实时追踪。后端通过 WiseCloud Java SDK 对接开放 API 并提供 RESTful 接口，Android 客户端通过 Retrofit 调用后端接口提供操作界面。

## 术语表

- **MDM_Backend**：Spring Boot 后端服务，负责用户鉴权、业务逻辑处理、WiseCloud API 调用及数据持久化
- **MDM_App**：Android 客户端应用，提供用户操作界面，通过 Retrofit/OkHttp 调用 MDM_Backend 的 RESTful 接口
- **WiseCloud_API**：WiseCloud 开放平台提供的设备管理 API，包括设备校验、设备详情查询、应用上传、指令推送、任务查询等
- **WiseCloud_SDK**：WiseCloud 官方 Java SDK，封装了签名、加密、HTTP 请求和响应解析逻辑
- **SN**：设备序列号（Serial Number），WiseCloud 平台中设备的唯一标识
- **JWT**：JSON Web Token，用于 MDM_Backend 的用户身份认证
- **traceId**：WiseCloud 指令推送后返回的任务流水号，用于追踪任务执行状态
- **versionMD5**：WiseCloud 应用上传后返回的应用版本唯一标识，用于后续推送安装指令
- **instructionStatus**：WiseCloud 指令执行状态码，1=preparing、2=executing、3=successful、4=failed
- **Device_Repository**：本地 MySQL 数据库中的设备表，存储已导入设备的 SN 及基本信息
- **Task_Repository**：本地 MySQL 数据库中的任务表，存储已下发任务的 traceId、类型、创建时间等元数据
- **Application_Repository**：本地 MySQL 数据库中的应用表，存储已上传到 WiseCloud 的应用信息（versionMD5、包名、版本号等）

## 需求

### 需求 1：用户注册

**用户故事：** 作为管理员，我希望注册一个账号，以便获得系统访问权限。

#### 验收标准

1. WHEN 用户提交包含用户名、邮箱和密码的注册请求, THE MDM_Backend SHALL 校验用户名唯一性和邮箱格式合法性，校验通过后使用 BCrypt 对密码进行哈希处理并将用户信息存入数据库，返回注册成功响应
2. IF 用户名已被注册, THEN THE MDM_Backend SHALL 返回 HTTP 409 状态码及 "用户名已存在" 错误信息
3. IF 邮箱格式不合法, THEN THE MDM_Backend SHALL 返回 HTTP 400 状态码及 "邮箱格式错误" 错误信息
4. IF 密码长度少于 8 个字符, THEN THE MDM_Backend SHALL 返回 HTTP 400 状态码及 "密码长度不足" 错误信息
5. WHEN MDM_App 打开注册页面, THE MDM_App SHALL 展示用户名、邮箱、密码和确认密码四个输入框以及注册按钮
6. WHEN 用户在 MDM_App 点击注册按钮, THE MDM_App SHALL 在本地校验两次密码输入一致后调用 `POST /api/auth/register` 接口提交注册请求

### 需求 2：用户登录

**用户故事：** 作为管理员，我希望使用账号密码登录系统，以便获取 JWT Token 进行后续操作。

#### 验收标准

1. WHEN 用户提交包含用户名和密码的登录请求, THE MDM_Backend SHALL 验证凭证正确性，验证通过后签发包含用户 ID 和过期时间的 JWT Token 并返回
2. IF 用户名不存在或密码错误, THEN THE MDM_Backend SHALL 返回 HTTP 401 状态码及 "用户名或密码错误" 错误信息
3. THE MDM_Backend SHALL 对除 `/api/auth/register` 和 `/api/auth/login` 之外的所有接口要求请求头携带有效的 JWT Token
4. IF 请求未携带 JWT Token 或 Token 已过期, THEN THE MDM_Backend SHALL 返回 HTTP 401 状态码及 "未授权" 错误信息
5. WHEN MDM_App 打开登录页面, THE MDM_App SHALL 展示用户名、密码输入框和登录按钮
6. WHEN 用户在 MDM_App 登录成功, THE MDM_App SHALL 将 JWT Token 存储到本地，并在后续所有 HTTP 请求的 Authorization Header 中携带该 Token
7. WHEN JWT Token 过期导致请求返回 401, THE MDM_App SHALL 自动跳转到登录页面

### 需求 3：设备导入

**用户故事：** 作为管理员，我希望通过输入或扫码一批设备 SN 将设备导入到本地管理系统，以便后续对这些设备进行统一管理。

#### 验收标准

1. WHEN 管理员提交一批 SN 列表, THE MDM_Backend SHALL 调用 WiseCloud_API `verify/sn` 接口校验 SN 合法性，将校验成功的 SN 列表传入 `device/detailList` 接口拉取设备详情，并将设备信息持久化到 Device_Repository
2. IF 提交的 SN 列表超过 300 个, THEN THE MDM_Backend SHALL 将 SN 列表按每组最多 300 个进行分批处理
3. WHEN SN 校验完成, THE MDM_Backend SHALL 在响应中返回校验成功的 SN 列表和校验失败的 SN 列表（含失败原因）
4. IF 提交的 SN 列表中存在已导入到 Device_Repository 的 SN, THEN THE MDM_Backend SHALL 跳过重复 SN 并在响应中标注 "已导入"
5. IF WiseCloud_API 调用失败（SDK 错误码 -1004 至 -1006）, THEN THE MDM_Backend SHALL 返回 HTTP 502 状态码及 "WiseCloud 服务不可用" 错误信息
6. WHEN MDM_App 打开设备导入页面, THE MDM_App SHALL 提供文本输入区域（支持多行粘贴 SN）和扫码按钮两种输入方式
7. WHEN 设备导入完成, THE MDM_App SHALL 展示导入结果摘要，包括成功数量、失败数量及失败 SN 的具体原因

### 需求 4：设备信息查询 — 首页概览

**用户故事：** 作为管理员，我希望在首页看到设备总量和在线率统计，以便快速了解设备整体状态。

#### 验收标准

1. WHEN MDM_App 打开首页, THE MDM_App SHALL 展示 Device Overview 区域，包含设备总数、在线设备数和在线率百分比
2. WHEN MDM_App 请求首页概览数据, THE MDM_Backend SHALL 从 Device_Repository 获取所有已导入设备的 SN 列表，调用 WiseCloud_API `device/detailList` 接口查询实时状态，统计 onlineStatus=1 的设备数量并计算在线率
3. IF Device_Repository 中已导入设备数量超过 300, THEN THE MDM_Backend SHALL 将 SN 列表按每组最多 300 个分批调用 WiseCloud_API 并合并结果
4. WHEN 首页概览数据加载中, THE MDM_App SHALL 展示加载指示器

### 需求 5：设备信息查询 — 搜索与详情

**用户故事：** 作为管理员，我希望按 SN 搜索设备并查看设备详细信息，以便了解单台设备的运行状态。

#### 验收标准

1. WHEN 管理员在首页输入 SN 关键词进行搜索, THE MDM_Backend SHALL 在 Device_Repository 中按 SN 进行模糊匹配，返回匹配的设备列表
2. WHEN 管理员在 MDM_App 点击搜索结果中的某台设备, THE MDM_App SHALL 跳转到设备详情页
3. WHEN MDM_App 打开设备详情页, THE MDM_Backend SHALL 调用 WiseCloud_API `device/detailList` 接口查询该设备的实时详情并返回
4. THE MDM_App 设备详情页 SHALL 展示以下信息：设备型号（deviceName）、ROM 版本（otaVersionName）、电池电量（electricRate）、在线状态（onlineStatus）、WiFi 状态（wifiStatus）、移动网络状态（gprsStatus）、经纬度位置（longitude/latitude）、已安装应用列表（installApps 中的 appName、appPackage、versionName）
5. IF WiseCloud_API 返回的设备在线状态为离线（onlineStatus=2）, THEN THE MDM_App SHALL 在设备详情页以灰色标签展示 "离线" 状态
6. IF WiseCloud_API 返回的设备在线状态为在线（onlineStatus=1）, THEN THE MDM_App SHALL 在设备详情页以绿色标签展示 "在线" 状态

### 需求 6：批量应用安装

**用户故事：** 作为管理员，我希望通过向导式流程将 APK 应用批量安装到多台设备上，以便高效地分发应用。

#### 验收标准

1. WHEN 管理员在 MDM_App 进入批量安装向导, THE MDM_App SHALL 展示三步骤流程：Step 1 任务配置（输入任务名称）→ Step 2 选择应用和版本 → Step 3 选择目标设备
2. WHEN 管理员需要安装一个新应用且该应用尚未上传到 WiseCloud, THE MDM_App SHALL 在 Step 2 提供上传 APK 的入口
3. WHEN 管理员上传 APK 文件, THE MDM_Backend SHALL 调用 WiseCloud_API `application/upload/add` 接口上传 APK，将返回的 versionMD5 和 versionNumber 连同应用名称、包名存入 Application_Repository
4. WHEN 管理员在 Step 2 选择应用, THE MDM_Backend SHALL 从 Application_Repository 查询已上传的应用列表供选择
5. WHEN 管理员在 Step 3 选择目标设备, THE MDM_Backend SHALL 从 Device_Repository 查询已导入的设备列表供选择
6. WHEN 管理员确认提交安装任务, THE MDM_Backend SHALL 调用 WiseCloud_API `instruction/task/push` 接口，使用 instructionKey="apkInstall"、type=2（批量推送）、target 为选中设备的 SN 列表、param 包含 versionMD5 和 versionNumber，将返回的 traceId 连同任务名称、类型（install）、创建时间存入 Task_Repository
7. WHEN 安装任务下发成功, THE MDM_App SHALL 展示任务创建成功提示并提供跳转到任务详情页的入口
8. IF WiseCloud_API `application/upload/add` 调用失败, THEN THE MDM_Backend SHALL 返回具体错误信息，MDM_App 展示上传失败提示并允许重试

### 需求 7：批量应用卸载

**用户故事：** 作为管理员，我希望从多台设备上批量卸载指定应用，以便清理不再需要的应用。

#### 验收标准

1. WHEN 管理员在 MDM_App 进入批量卸载流程, THE MDM_App SHALL 展示两步骤流程：Step 1 选择目标设备 → Step 2 从设备已安装应用列表中选择要卸载的应用
2. WHEN 管理员在 Step 1 选择目标设备后, THE MDM_Backend SHALL 调用 WiseCloud_API `device/detailList` 接口查询所选设备的已安装应用列表（installApps），取所有设备的应用交集供管理员选择
3. WHEN 管理员确认提交卸载任务, THE MDM_Backend SHALL 调用 WiseCloud_API `instruction/task/push` 接口，使用 instructionKey="uninstallApp"、type=2（批量推送）、target 为选中设备的 SN 列表、param 包含 pkgName（应用包名），将返回的 traceId 连同任务类型（uninstall）、创建时间存入 Task_Repository
4. WHEN 卸载任务下发成功, THE MDM_App SHALL 展示任务创建成功提示并提供跳转到任务详情页的入口

### 需求 8：任务执行结果查询

**用户故事：** 作为管理员，我希望查看任务在各设备上的执行状态，以便追踪任务进度和排查失败原因。

#### 验收标准

1. WHEN MDM_App 打开任务列表页, THE MDM_Backend SHALL 从 Task_Repository 查询所有任务记录，对每条任务调用 WiseCloud_API `instruction/task/details` 接口按 traceId 查询执行状态，聚合计算各状态设备数量并返回任务列表（含进度信息如 "7/12 Completed"）
2. WHEN 管理员在 MDM_App 点击某条任务, THE MDM_App SHALL 跳转到任务详情页，展示四个 Tab：Completed（instructionStatus=3）、Failed（instructionStatus=4）、Executing（instructionStatus=2）、Preparing（instructionStatus=1）
3. WHEN MDM_App 打开任务详情页, THE MDM_Backend SHALL 调用 WiseCloud_API `instruction/task/details` 接口查询该 traceId 下所有设备的执行记录，按 instructionStatus 分组返回
4. THE MDM_App 任务详情页每个 Tab SHALL 展示对应状态的设备 SN 列表，Failed Tab 额外展示 executeCode 和 executeMessage
5. WHEN 任务刚下发后, THE MDM_App SHALL 每隔 5 至 10 秒自动轮询任务详情接口刷新执行状态
6. WHEN 任务下所有设备的 instructionStatus 均为 3（successful）或 4（failed）, THE MDM_App SHALL 停止自动轮询

### 需求 9：WiseCloud SDK 集成

**用户故事：** 作为开发者，我希望后端通过 WiseCloud Java SDK 调用开放 API，以便自动处理签名、加密和 HTTP 通信。

#### 验收标准

1. THE MDM_Backend SHALL 使用 WiseCloud_SDK 的 `OpenApiClient.execute()` 方法调用所有 WiseCloud_API 接口
2. THE MDM_Backend SHALL 通过 Spring 配置文件管理 WiseCloud accessKeyId 和 accessKeySecret，支持通过环境变量注入
3. THE MDM_Backend SHALL 将 WiseCloud_SDK 的 OpenApiClient 配置为 Sandbox 模式（`Config.setSandBox(true)`）
4. IF WiseCloud_SDK 抛出 OpenApiClientException（错误码 -1001 至 -1006）, THEN THE MDM_Backend SHALL 记录错误日志并返回统一格式的错误响应
5. IF WiseCloud_API 返回业务错误（`response.isSuccess()` 为 false）, THEN THE MDM_Backend SHALL 记录业务错误码和错误信息并返回统一格式的错误响应

### 需求 10：后端部署到 AWS EKS（可选）

**用户故事：** 作为运维人员，我希望将 Spring Boot 后端容器化部署到 AWS EKS，以便提供可扩展的生产环境。

#### 验收标准

1. WHERE 选择 EKS 部署方式, THE MDM_Backend SHALL 提供 Dockerfile 将 Spring Boot 应用打包为 Docker 镜像
2. WHERE 选择 EKS 部署方式, THE MDM_Backend SHALL 提供 Kubernetes Deployment 和 Service YAML 配置文件
3. WHERE 选择 EKS 部署方式, THE MDM_Backend SHALL 通过 Kubernetes Secret 管理数据库连接信息和 WiseCloud AK/SK
4. WHERE 选择 EKS 部署方式, THE MDM_Backend SHALL 提供 Ingress 配置以对外暴露 HTTP 服务
