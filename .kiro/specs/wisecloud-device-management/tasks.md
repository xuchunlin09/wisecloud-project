# 实施计划：WiseCloud 移动设备管理应用（MDM）

## 概述

本实施计划将设计文档拆分为可执行的编码任务，按照从项目脚手架搭建到功能集成的顺序递进实现。后端使用 Spring Boot 3.x + Java，Android 客户端使用 Kotlin + Retrofit，属性测试使用 jqwik。每个任务构建在前序任务之上，确保无孤立代码。

## 任务

- [x] 1. 搭建 Spring Boot 后端项目脚手架与基础配置
  - [x] 1.1 初始化 Spring Boot 项目结构与 Maven 依赖
    - 使用 Spring Initializr 创建 Spring Boot 3.x 项目（workshop/wisecloud-project）
    - 添加依赖：Spring Web、Spring Security、Spring Data JPA、MySQL Driver、jjwt、Lombok、Validation
    - 安装 WiseCloud SDK JAR 到本地 Maven 仓库并添加 pom.xml 依赖（含 httpclient、jackson 等传递依赖）
    - 添加 jqwik 测试依赖
    - 创建包结构：config、controller、service、repository、entity、dto（request/response）、security、exception、util
    - _需求: 9.1, 9.2_

  - [x] 1.2 配置数据库与 WiseCloud SDK
    - 创建 application.yml 配置文件：MySQL 数据源、JPA/Hibernate DDL 策略、WiseCloud AK/SK（环境变量注入）
    - 创建 application-local.yml 用于本地开发（固定 AK/SK），加入 .gitignore
    - 创建 WiseCloudConfig 配置类：初始化 OpenApiClient Bean，设置 Sandbox 模式（Config.setSandBox(true)）、HTTP 连接池与超时
    - 创建 CorsConfig 配置类允许跨域请求
    - _需求: 9.2, 9.3_

  - [x] 1.3 创建 JPA 实体与 Repository
    - 创建 User 实体（id, username, email, passwordHash, createdAt, updatedAt）
    - 创建 Device 实体（id, sn, deviceName, deviceType, status, activationTime, merchantNo, merchantName, storeName, importedAt, importedBy）
    - 创建 Application 实体（id, appName, appPackage, versionNumber, versionName, versionMd5, appAlias, appDescription, uploadedAt, uploadedBy）
    - 创建 Task 实体（id, taskName, traceId, taskType, targetApp, deviceCount, createdAt, createdBy）
    - 创建对应的 JPA Repository 接口（UserRepository、DeviceRepository、ApplicationRepository、TaskRepository）
    - DeviceRepository 添加 findBySnContaining(String keyword) 和 existsBySn(String sn) 方法
    - _需求: 1.1, 3.1, 6.3, 8.1_

  - [x] 1.4 创建统一响应包装与全局异常处理
    - 创建 ApiResponse<T> 记录类（code, message, data），含 success() 和 error() 静态工厂方法
    - 创建 BusinessException 自定义异常类（含 httpStatus 和 message）
    - 创建 WiseCloudApiException 自定义异常类（封装 SDK 错误码和消息）
    - 创建 GlobalExceptionHandler（@RestControllerAdvice）：处理 BusinessException、WiseCloudApiException、MethodArgumentNotValidException、兜底 Exception
    - WiseCloudApiException 处理时记录日志但不向客户端泄露 SDK 内部堆栈
    - _需求: 9.4, 9.5_


- [x] 2. 实现 JWT 认证与用户注册登录
  - [x] 2.1 实现 JWT 工具类与安全过滤器
    - 创建 JwtTokenProvider：generateToken(userId)、validateToken(token)、getUserIdFromToken(token)，使用 HMAC-SHA256 签名，过期时间可配置
    - 创建 JwtAuthenticationFilter（OncePerRequestFilter）：从 Authorization Header 提取 Bearer Token，验证后设置 SecurityContext
    - 创建 SecurityConfig（@Configuration）：配置 HttpSecurity，放行 /api/auth/register 和 /api/auth/login，其余接口要求认证
    - _需求: 2.1, 2.3, 2.4_

  - [x] 2.2 实现 AuthService 与 AuthController
    - 创建 RegisterRequest DTO（username, email, password），添加 Bean Validation 注解（邮箱格式 @Email、密码长度 @Size(min=8)）
    - 创建 LoginRequest DTO（username, password）
    - 创建 LoginResponse DTO（token, expiresIn）
    - 实现 AuthService.register()：校验用户名唯一性 → BCrypt 哈希密码 → 保存 User 实体
    - 实现 AuthService.login()：查找用户 → BCrypt 验证密码 → 签发 JWT Token
    - 实现 AuthController：POST /api/auth/register（注册）、POST /api/auth/login（登录）
    - 用户名已存在返回 409，邮箱格式错误返回 400，密码长度不足返回 400，凭证错误返回 401
    - _需求: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2_

  - [ ]* 2.3 编写注册输入校验属性测试
    - **Property 1: 注册输入校验拒绝非法输入**
    - 使用 jqwik 生成随机注册请求（含已存在用户名、非法邮箱、短密码），验证均被拒绝且数据库无新增记录
    - **验证: 需求 1.2, 1.3, 1.4**

  - [ ]* 2.4 编写认证拒绝无效凭证属性测试
    - **Property 3: 认证拒绝无效凭证**
    - 使用 jqwik 生成随机用户名/密码组合，验证不存在的用户名或错误密码均返回 401 且不签发 Token
    - **验证: 需求 2.2**

  - [ ]* 2.5 编写 JWT 校验属性测试
    - **Property 4: JWT 校验拒绝无效 Token**
    - 使用 jqwik 生成过期 Token 和随机字符串 Token，验证访问受保护接口均返回 401
    - **验证: 需求 2.4**

- [x] 3. 检查点 — 确保认证模块测试通过
  - 确保所有测试通过，如有疑问请询问用户。

- [x] 4. 实现 WiseCloudService — SDK 调用统一封装
  - [x] 4.1 实现 WiseCloudService 核心方法
    - 创建 WiseCloudService（@Service），注入 OpenApiClient
    - 实现 splitIntoBatches() 静态工具方法：将 SN 列表按 BATCH_SIZE=300 分批
    - 实现 verifySn(List<String> snList)：自动分批调用 verify/sn 接口，合并 sucList 和 errList
    - 实现 queryDeviceDetails(List<String> snList)：自动分批调用 device/detailList 接口，合并结果
    - 实现 uploadApplication()：调用 application/upload/add 接口上传 APK
    - 实现 pushInstallInstruction()：调用 instruction/task/push 接口（instructionKey=apkInstall, type=2）
    - 实现 pushUninstallInstruction()：调用 instruction/task/push 接口（instructionKey=uninstallApp, type=2）
    - 实现 queryTaskDetails(String traceId)：调用 instruction/task/details 接口
    - 统一异常处理：捕获 OpenApiClientException 转为 WiseCloudApiException，检查 response.isSuccess() 处理业务错误
    - _需求: 9.1, 9.3, 9.4, 9.5, 3.2, 4.3_

  - [ ]* 4.2 编写 SN 列表分批完整性属性测试
    - **Property 5: SN 列表分批保持完整性**
    - 使用 jqwik 生成长度 1~2000 的随机 SN 列表，验证分批后批次数 = ⌈N/300⌉、每批 ≤300、并集与原列表一致
    - **验证: 需求 3.2, 4.3**

  - [ ]* 4.3 编写 SDK 异常统一错误处理属性测试
    - **Property 12: SDK 异常统一错误处理**
    - 使用 jqwik 生成 -1001 至 -1006 范围内的错误码，mock OpenApiClient 抛出 OpenApiClientException，验证返回统一错误响应且不泄露堆栈
    - **验证: 需求 9.4**


- [x] 5. 实现设备管理功能（导入、概览、搜索、详情）
  - [x] 5.1 实现 DeviceService 业务逻辑
    - 实现 importDevices(List<String> snList)：调用 WiseCloudService.verifySn() 校验 → 过滤已存在于 DeviceRepository 的 SN（标注"已导入"）→ 调用 queryDeviceDetails() 拉取详情 → 持久化到 DeviceRepository → 返回 DeviceImportResponse（successCount, failCount, successList, failList）
    - 实现 getOverview()：从 DeviceRepository 获取所有 SN → 调用 WiseCloudService.queryDeviceDetails() 查实时状态 → 统计 onlineStatus=1 的数量 → 计算在线率百分比 → 返回 DeviceOverviewResponse
    - 实现 searchBySn(String keyword)：调用 DeviceRepository.findBySnContaining() 模糊搜索
    - 实现 getDeviceDetail(String sn)：调用 WiseCloudService.queryDeviceDetails() 查询单设备实时详情
    - _需求: 3.1, 3.2, 3.3, 3.4, 3.5, 4.2, 4.3, 5.1, 5.3_

  - [x] 5.2 实现 DeviceController REST 接口
    - 创建请求/响应 DTO：DeviceImportRequest、DeviceImportResponse、DeviceOverviewResponse、DeviceSummary、DeviceDetailResponse、SnFailInfo
    - 实现 POST /api/devices/import（设备导入）
    - 实现 GET /api/devices/overview（首页概览）
    - 实现 GET /api/devices/search?keyword=xxx（SN 模糊搜索）
    - 实现 GET /api/devices/{sn}/detail（单设备实时详情）
    - _需求: 3.1, 3.3, 3.7, 4.1, 4.2, 5.1, 5.3, 5.4_

  - [ ]* 5.3 编写 SN 校验响应分区属性测试
    - **Property 6: SN 校验响应正确分区**
    - 使用 jqwik 生成随机 SN 列表，验证 successList 和 failList 无交集、并集等于原始列表、failList 每项含失败原因
    - **验证: 需求 3.3**

  - [ ]* 5.4 编写设备导入去重属性测试
    - **Property 7: 设备导入去重**
    - 使用 jqwik 生成含重复 SN 的列表，验证已存在 SN 被跳过、响应标注"已导入"、数据库中该 SN 记录数保持为 1
    - **验证: 需求 3.4**

  - [ ]* 5.5 编写在线率计算正确性属性测试
    - **Property 8: 在线率计算正确性**
    - 使用 jqwik 生成随机设备状态列表（onlineStatus 为 1 或 2），验证 onlineCount 等于 status=1 的数量、onlineRate = onlineCount/totalCount
    - **验证: 需求 4.2**

  - [ ]* 5.6 编写 SN 模糊搜索完整性属性测试
    - **Property 9: SN 模糊搜索完整性**
    - 使用 jqwik 生成随机关键词和 SN 集合，验证搜索结果包含所有含关键词子串的 SN、不包含不含关键词的 SN
    - **验证: 需求 5.1**

- [x] 6. 检查点 — 确保设备管理模块测试通过
  - 确保所有测试通过，如有疑问请询问用户。

- [x] 7. 实现应用管理与任务下发功能
  - [x] 7.1 实现 ApplicationService 与 ApplicationController
    - 实现 ApplicationService.uploadApk()：接收 MultipartFile → 调用 WiseCloudService.uploadApplication() → 将 versionMD5、versionNumber、appName、appPackage 等存入 ApplicationRepository
    - 实现 ApplicationService.listApplications()：从 ApplicationRepository 查询已上传应用列表
    - 创建请求/响应 DTO：AppUploadResponse、ApplicationInfo
    - 实现 ApplicationController：POST /api/applications/upload（上传 APK）、GET /api/applications（应用列表）
    - 配置 Spring multipart 上传限制（max-file-size: 100MB）
    - _需求: 6.2, 6.3, 6.4, 6.8_

  - [x] 7.2 实现 TaskService 与 TaskController
    - 实现 TaskService.createInstallTask()：调用 WiseCloudService.pushInstallInstruction() → 将 traceId、taskName、taskType=install、targetApp、deviceCount 存入 TaskRepository
    - 实现 TaskService.createUninstallTask()：调用 WiseCloudService.pushUninstallInstruction() → 将 traceId、taskType=uninstall 存入 TaskRepository
    - 实现 TaskService.listTasks()：从 TaskRepository 查询所有任务 → 对每条任务调用 WiseCloudService.queryTaskDetails() → 聚合计算各状态设备数量 → 返回 TaskSummary 列表（含 progress 如 "7/12 Completed"）
    - 实现 TaskService.getTaskDetails(String traceId)：调用 WiseCloudService.queryTaskDetails() → 按 instructionStatus 分组为 completed/failed/executing/preparing 四个列表 → 返回 TaskDetailResponse
    - 创建请求/响应 DTO：InstallTaskRequest、UninstallTaskRequest、TaskCreateResponse、TaskSummary、TaskDetailResponse、TaskDeviceStatus
    - 实现 TaskController：POST /api/tasks/install、POST /api/tasks/uninstall、GET /api/tasks、GET /api/tasks/{traceId}/details
    - _需求: 6.5, 6.6, 6.7, 7.3, 7.4, 8.1, 8.2, 8.3, 8.4_

  - [ ]* 7.3 编写设备已安装应用交集属性测试
    - **Property 10: 设备已安装应用交集正确性**
    - 使用 jqwik 生成多台设备的 installApps 列表，验证计算的应用交集仅包含所有设备都已安装的应用（按 appPackage 判断）
    - **验证: 需求 7.2**

  - [ ]* 7.4 编写任务状态分组与终态检测属性测试
    - **Property 11: 任务状态分组与终态检测**
    - 使用 jqwik 生成随机任务执行记录列表（instructionStatus 为 1/2/3/4），验证四个分组互不相交、并集等于原列表、isAllTerminal() 当且仅当 executing 和 preparing 均为空时返回 true
    - **验证: 需求 8.1, 8.2, 8.6**

- [x] 8. 检查点 — 确保后端所有功能与测试通过
  - 确保所有测试通过，如有疑问请询问用户。


- [x] 9. 搭建 Android 客户端项目脚手架与网络层
  - [x] 9.1 初始化 Android 项目结构与依赖配置
    - 在 workshop/wisecloud-app 创建 Android 项目（Kotlin、minSdk 24、targetSdk 34）
    - 添加依赖：Retrofit + OkHttp + Gson Converter、Jetpack Navigation、ViewModel + LiveData、Coroutines、Material Design Components
    - 创建包结构：data（api、model、repository、local）、ui（auth、home、device、install、uninstall、task）、util
    - _需求: 1.5, 2.5_

  - [x] 9.2 实现网络层与 Token 管理
    - 创建 TokenManager：基于 SharedPreferences 存储/读取/清除 JWT Token
    - 创建 AuthInterceptor（OkHttp Interceptor）：自动在请求头添加 Bearer Token，401 响应时清除 Token 并发送 TokenExpiredEvent
    - 创建 MdmApiService（Retrofit 接口）：定义所有后端 API 调用方法（register、login、importDevices、getOverview、searchDevices、getDeviceDetail、uploadApk、listApplications、createInstallTask、createUninstallTask、listTasks、getTaskDetails）
    - 创建 RetrofitClient 单例：配置 baseUrl、OkHttp Client（含 AuthInterceptor）、Gson Converter
    - 创建数据模型类（对应后端 DTO）
    - _需求: 2.6, 2.7_

- [x] 10. 实现 Android 登录注册页面
  - [x] 10.1 实现登录与注册 UI 及 ViewModel
    - 创建 LoginFragment：用户名、密码输入框和登录按钮
    - 创建 RegisterFragment：用户名、邮箱、密码、确认密码输入框和注册按钮
    - 创建 AuthViewModel：处理登录/注册逻辑，调用 MdmApiService
    - 注册时本地校验两次密码输入一致，不一致则阻止提交
    - 登录成功后通过 TokenManager 存储 JWT Token，跳转到首页
    - 实现 TokenExpiredEvent 监听：401 时自动跳转登录页
    - _需求: 1.5, 1.6, 2.5, 2.6, 2.7_

  - [ ]* 10.2 编写客户端密码一致性校验属性测试
    - **Property 2: 客户端密码一致性校验**
    - 使用 Kotlin 测试框架生成两个不相等的字符串，验证客户端阻止表单提交、不发起网络请求
    - **验证: 需求 1.6**

- [ ] 11. 实现 Android 设备管理页面
  - [x] 11.1 实现首页概览与搜索
    - 创建 HomeFragment：展示 Device Overview 区域（设备总数、在线设备数、在线率百分比）、SN 搜索输入框、搜索结果列表
    - 创建 HomeViewModel：调用 getOverview() 和 searchDevices() 接口
    - 数据加载中展示加载指示器
    - 点击搜索结果中的设备跳转到设备详情页
    - _需求: 4.1, 4.4, 5.1, 5.2_

  - [ ] 11.2 实现设备详情页
    - 创建 DeviceDetailFragment：展示设备型号、ROM 版本、电池电量、在线状态（在线绿色/离线灰色标签）、WiFi 状态、移动网络状态、经纬度位置、已安装应用列表
    - 创建 DeviceDetailViewModel：调用 getDeviceDetail() 接口
    - _需求: 5.3, 5.4, 5.5, 5.6_

  - [ ] 11.3 实现设备导入页面
    - 创建 DeviceImportFragment：文本输入区域（支持多行粘贴 SN）和扫码按钮
    - 创建 DeviceImportViewModel：调用 importDevices() 接口
    - 导入完成后展示结果摘要：成功数量、失败数量及失败 SN 的具体原因
    - _需求: 3.6, 3.7_

- [ ] 12. 实现 Android 应用安装与卸载向导
  - [ ] 12.1 实现批量安装向导
    - 创建三步骤向导 UI：Step 1 任务配置（输入任务名称）→ Step 2 选择应用和版本（含上传 APK 入口）→ Step 3 选择目标设备
    - 创建 InstallWizardViewModel：管理向导状态，调用 listApplications()、uploadApk()、listDevices()、createInstallTask() 接口
    - Step 2 提供上传 APK 入口，上传失败展示错误信息并允许重试
    - 安装任务下发成功后展示成功提示并提供跳转到任务详情页的入口
    - _需求: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7, 6.8_

  - [ ] 12.2 实现批量卸载流程
    - 创建两步骤流程 UI：Step 1 选择目标设备 → Step 2 从设备已安装应用交集中选择要卸载的应用
    - 创建 UninstallViewModel：管理卸载流程状态，调用 getDeviceDetail()（获取 installApps 交集）、createUninstallTask() 接口
    - 卸载任务下发成功后展示成功提示并提供跳转到任务详情页的入口
    - _需求: 7.1, 7.2, 7.3, 7.4_

- [ ] 13. 实现 Android 任务管理页面
  - [ ] 13.1 实现任务列表与任务详情页
    - 创建 TaskListFragment：展示任务列表（含进度信息如 "7/12 Completed"）
    - 创建 TaskListViewModel：调用 listTasks() 接口
    - 创建 TaskDetailFragment：展示四个 Tab（Completed、Failed、Executing、Preparing），Failed Tab 额外展示 executeCode 和 executeMessage
    - 创建 TaskDetailViewModel：调用 getTaskDetails() 接口，实现轮询逻辑（每 5~10 秒刷新），所有设备到终态（instructionStatus 为 3 或 4）后停止轮询
    - 点击任务列表中的任务跳转到任务详情页
    - _需求: 8.1, 8.2, 8.3, 8.4, 8.5, 8.6_

- [ ] 14. 检查点 — 确保 Android 客户端所有页面功能正常
  - 确保所有测试通过，如有疑问请询问用户。

- [ ] 15. 集成测试与端到端验证
  - [ ]* 15.1 编写后端集成测试
    - 使用 Spring Boot Test + TestContainers（MySQL）+ WireMock（模拟 WiseCloud API）
    - 测试完整注册 → 登录 → 获取 Token 流程
    - 测试设备导入端到端流程（含 WiseCloud API mock）
    - 测试 APK 上传 → 安装任务下发流程
    - 测试 JWT 认证过滤器对受保护接口的拦截
    - _需求: 1.1, 2.1, 2.3, 3.1, 6.3, 6.6_

- [x] 16. 容器化部署配置（可选）
  - [x] 16.1 创建 Docker 与 Kubernetes 部署文件
    - 创建 Dockerfile：多阶段构建，将 Spring Boot 应用打包为 Docker 镜像
    - 创建 Kubernetes Deployment YAML：配置副本数、资源限制、健康检查
    - 创建 Kubernetes Service YAML：暴露服务端口
    - 创建 Kubernetes Secret YAML：管理数据库连接信息和 WiseCloud AK/SK
    - 创建 Ingress YAML：对外暴露 HTTP 服务
    - _需求: 10.1, 10.2, 10.3, 10.4_

- [x] 17. 最终检查点 — 确保所有模块集成完毕
  - 确保所有测试通过，如有疑问请询问用户。

## 备注

- 标记 `*` 的任务为可选任务，可跳过以加速 MVP 交付
- 每个任务引用了具体的需求编号以确保可追溯性
- 检查点任务确保增量验证，及时发现问题
- 属性测试验证 12 个正确性属性的普遍正确性
- 单元测试验证具体示例和边界情况
- 后端使用 Java（Spring Boot 3.x），Android 客户端使用 Kotlin
- 属性测试使用 jqwik 库，每个属性至少 100 次迭代
