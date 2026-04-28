# 实施计划：WiseCloud 移动端 App

## 概述

基于 MVVM 架构，使用 Kotlin + Jetpack + Retrofit + OkHttp 构建 WiseCloud 设备管理 Android 客户端。按照分层架构自底向上实现：先搭建项目基础与网络层，再实现数据模型与工具类，然后逐模块完成 ViewModel 与 UI 层，最后集成联调。

## 任务

- [ ] 1. 搭建项目基础结构与依赖注入
  - [ ] 1.1 创建 Android 项目并配置 Gradle 依赖
    - 创建 `com.wisecloud.app` 包结构（di/、data/、ui/、util/、widget/）
    - 配置 build.gradle：Kotlin、Hilt、Retrofit 2、OkHttp 4、Gson、Coroutines、Navigation Component、MPAndroidChart、Coil、Material Design 3、Kotest、MockK、Espresso
    - 创建 `WiseCloudApp.kt` Application 入口类，添加 `@HiltAndroidApp` 注解
    - _需求: 全局_

  - [ ] 1.2 实现 Hilt 依赖注入模块
    - 创建 `di/NetworkModule.kt`：提供 OkHttpClient（含超时配置 connect=15s, read=30s, write=30s）、Retrofit 实例、MdmApiService、HttpLoggingInterceptor
    - 创建 `di/RepositoryModule.kt`：绑定 AuthRepository、DeviceRepository、TaskRepository、ApplicationRepository
    - _需求: 全局_

- [ ] 2. 实现数据层：API 接口与数据模型
  - [ ] 2.1 定义 API 请求与响应数据模型
    - 在 `data/model/request/` 下创建：LoginRequest、SendCodeRequest、InstallTaskRequest、UninstallTaskRequest、OtaTaskRequest、InstructionTaskRequest、WiseOSSettingRequest、FilePushRequest
    - 在 `data/model/response/` 下创建：ApiResponse<T>、PagedResponse<T>、LoginResponse、DeviceOverviewResponse、DeviceSummary、DeviceDetailResponse、InstalledApp、ApplicationInfo、AppVersion、AppUploadResponse、TaskCreateResponse、TaskSummary、TaskDetailResponse、TaskDeviceStatus
    - _需求: 1.9, 2.6, 3.1, 4.1, 5.10, 6.4, 7.5, 8.4, 9.4, 10.4, 11.6, 12.4_

  - [ ] 2.2 实现 Retrofit API 接口 MdmApiService
    - 定义全部 18 个端点：login、sendVerificationCode、getDeviceOverview、searchDevices、getDeviceList、getDeviceDetail、getApplicationList、uploadApk、createInstallTask、createUninstallTask、createOtaTask、createInstructionTask、createWiseOSSettingTask、createFilePushTask、getTaskList、getTaskDetails
    - _需求: 1.9, 2.3, 3.1, 4.1, 5.10, 6.4, 7.5, 8.4, 9.4, 10.4, 11.2, 12.4_

- [ ] 3. 实现 Token 管理与网络拦截器
  - [ ] 3.1 实现 TokenManager
    - 基于 SharedPreferences 实现 saveToken/getToken/clearToken/isLoggedIn
    - 实现 saveCredentials/getSavedCredentials/clearCredentials（Remember Password 功能）
    - _需求: 1.8, 1.9_

  - [ ]* 3.2 编写 TokenManager 属性测试
    - **Property 2: 凭证存储 Round-Trip**
    - 对任意非空邮箱和密码字符串对，saveCredentials 后 getSavedCredentials 返回完全相同的值
    - **验证: 需求 1.8**

  - [ ] 3.3 实现 AuthInterceptor
    - 非 auth 路径请求自动附加 `Authorization: Bearer {token}` 头
    - 401 响应时清除 Token 并发送 TokenExpiredEvent
    - _需求: 1.9, 1.10_

- [ ] 4. 实现 Result 封装与 BaseRepository
  - [ ] 4.1 创建 Result<T> 密封类与 BaseRepository
    - 实现 Result.Success / Result.Error / Result.NetworkError / Result.Loading
    - 实现 BaseRepository.safeApiCall()：统一捕获 UnknownHostException、SocketTimeoutException、IOException、HttpException
    - _需求: 1.10, 1.11_

  - [ ] 4.2 实现四个 Repository
    - AuthRepository：login、sendCode、logout
    - DeviceRepository：getOverview、searchDevices、getDeviceList、getDeviceDetail
    - TaskRepository：createInstallTask、createUninstallTask、createOtaTask、createInstructionTask、createWiseOSSettingTask、createFilePushTask、getTaskList、getTaskDetails
    - ApplicationRepository：getApplicationList、uploadApk
    - _需求: 1.9, 2.3, 3.1, 5.10, 6.4, 7.5, 8.4, 9.4, 10.4, 11.2, 12.4_

- [ ] 5. 实现工具类
  - [ ] 5.1 实现 InputValidator
    - isValidEmail：RFC 标准邮箱正则校验
    - isValidPassword：至少 8 位
    - isValidMfaCode：6 位纯数字
    - isValidVerificationCode：非空
    - _需求: 1.1, 1.5_

  - [ ]* 5.2 编写 InputValidator 属性测试
    - **Property 1: 邮箱格式校验正确分类**
    - 对任意字符串，含 `@` 和有效域名后缀返回 true，不含 `@` 或域名无效返回 false
    - **验证: 需求 1.1**

  - [ ] 5.3 实现 DeviceFilterUtil
    - filterByOnlineStatus：按 ALL/ONLINE/OFFLINE 筛选设备列表
    - _需求: 3.3_

  - [ ]* 5.4 编写 DeviceFilterUtil 属性测试
    - **Property 4: 设备列表在线状态筛选完整性**
    - ALL 返回原始列表；ONLINE 结果全部 onlineStatus==1 且无遗漏；OFFLINE 同理
    - **验证: 需求 3.3**

  - [ ] 5.5 实现 TaskFilterUtil
    - filterByType：按任务类型筛选，null 返回全部
    - _需求: 11.5_

  - [ ]* 5.6 编写 TaskFilterUtil 属性测试
    - **Property 6: 任务类型筛选完整性**
    - type 为 null 返回原始列表；type 非 null 时结果全部匹配且无遗漏
    - **验证: 需求 11.5**

  - [ ] 5.7 实现 TaskProgressUtil
    - calculateProgress：计算 completedCount/failedCount/executingCount/preparingCount/totalCount/progressPercent/progressText
    - groupByStatus：按 instructionStatus 分为四组
    - isAllTerminal：全部为 3 或 4 时返回 true
    - _需求: 11.6, 12.3, 12.10, 12.11_

  - [ ]* 5.8 编写 TaskProgressUtil 属性测试（进度计算）
    - **Property 7: 任务进度计算正确性**
    - 四组计数之和等于总数；progressPercent = (completed+failed)/total；progressText 格式正确
    - **验证: 需求 11.6**

  - [ ]* 5.9 编写 TaskProgressUtil 属性测试（状态分组与终态检测）
    - **Property 8: 任务状态分组与终态检测**
    - 四组互不相交且并集等于原始列表；各组 instructionStatus 值正确；isAllTerminal 当且仅当 executing 和 preparing 为空
    - **验证: 需求 12.3, 12.4, 12.10, 12.11**

  - [ ] 5.10 实现 BatteryColorUtil
    - getColor：0~20 红色、21~50 黄色、51~100 绿色
    - _需求: 4.3_

  - [ ]* 5.11 编写 BatteryColorUtil 属性测试
    - **Property 5: 电量颜色映射正确性**
    - 对 0~100 的任意整数，颜色映射符合阈值规则且幂等
    - **验证: 需求 4.3**

  - [ ] 5.12 实现 Extensions.kt
    - 常用 Kotlin 扩展函数（日期格式化、Toast 快捷方法、View 可见性切换等）
    - _需求: 全局_

- [ ] 6. 检查点 — 确保所有测试通过
  - 确保所有测试通过，如有疑问请向用户确认。

- [ ] 7. 实现登录模块
  - [ ] 7.1 实现 LoginViewModel
    - 管理 LoginUiState（Idle/Loading/Success/Error/NetworkError）状态流转
    - 实现 login()、sendVerificationCode()（含 60 秒倒计时）、switchVerifyMethod()
    - 实现 loadSavedCredentials() 和 saveCredentials()
    - _需求: 1.1, 1.3, 1.4, 1.5, 1.7, 1.8, 1.9, 1.10, 1.11_

  - [ ] 7.2 实现 LoginFragment 与布局
    - 邮箱输入框（格式校验）、密码输入框（眼睛图标切换明文/密文）
    - 邮箱验证码输入框 + "Get Code" 按钮（60 秒倒计时）
    - MFA 6 位数字输入（自定义 MfaInputView：自动跳转、退格回退）
    - "Switch to MFA" / "Switch to Email" 切换链接
    - "Remember Password" 复选框
    - 错误提示（保留邮箱）、网络异常提示
    - _需求: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.10, 1.11_

  - [ ] 7.3 实现 MfaInputView 自定义控件
    - 6 个独立数字输入框，每框仅接受 1 位数字
    - 输入后自动跳转下一个输入框
    - 退格键当前框为空时焦点移至前一框并清除内容
    - _需求: 1.5, 1.6_

  - [ ]* 7.4 编写 LoginViewModel 单元测试
    - 测试登录状态流转、倒计时逻辑、凭证保存/加载、验证方式切换
    - _需求: 1.1, 1.4, 1.7, 1.8, 1.9, 1.10_

- [ ] 8. 实现首页仪表盘模块
  - [ ] 8.1 实现 DashboardViewModel
    - 加载设备概览统计（总设备数、在线数、在线率）
    - 300ms 防抖搜索（MutableStateFlow + debounce）
    - 加载最近 7 天活动数据
    - _需求: 2.1, 2.3, 2.4, 2.6, 2.7_

  - [ ]* 8.2 编写 DashboardViewModel 属性测试（在线率计算）
    - **Property 3: 在线率计算正确性**
    - onlineCount 等于 onlineStatus==1 的设备数；onlineRate 百分比正确；totalCount 为 0 时 onlineRate 为 "0%"
    - **验证: 需求 2.6**

  - [ ] 8.3 实现 DashboardFragment 与布局
    - 顶部欢迎信息（用户名）、任务管理入口卡片
    - 设备搜索框 + 搜索结果下拉列表（SN、型号、在线状态）
    - 设备概览统计卡片（总设备数、在线数、在线率百分比）
    - 最近 7 天活动柱状图（MPAndroidChart）
    - _需求: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7_

  - [ ] 8.4 实现 BatchMenuBottomSheet
    - 右下角浮动按钮（脉冲动画）
    - 半透明遮罩 + 底部滑出菜单（App Install、OTA Update、App Uninstall、Push Instruction、WiseOS Setting）
    - 点击选项跳转对应向导页面；点击遮罩关闭菜单
    - _需求: 2.8, 2.9, 2.10, 2.11, 2.12_

- [ ] 9. 实现设备列表模块
  - [ ] 9.1 实现 DeviceListViewModel
    - 分页加载设备列表（每页 20 条）
    - SN 模糊搜索
    - 在线状态筛选（全部/在线/离线）
    - 下拉刷新
    - _需求: 3.1, 3.2, 3.3, 3.4, 3.5_

  - [ ] 9.2 实现 DeviceListFragment、DeviceListAdapter 与布局
    - RecyclerView 列表（SN、型号、在线状态、最后在线时间）
    - 搜索框、筛选标签栏
    - 下拉刷新（SwipeRefreshLayout）、无限滚动分页
    - 加载指示器、空状态提示
    - 点击设备跳转 DeviceDetailPage
    - _需求: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8_

- [ ] 10. 实现设备详情模块
  - [ ] 10.1 实现 DeviceDetailViewModel
    - 加载设备详情数据
    - 管理远程操作（Lock/Unlock 确认逻辑）
    - _需求: 4.1, 4.9, 4.10_

  - [ ] 10.2 实现 DeviceDetailFragment 与布局
    - 渐变色头部（SN、型号、在线状态标签、设备图片）
    - 基本信息卡片（Android 版本、ROM、SP、DMS、OEM 版本）
    - 设备状态卡片（电池电量进度条 + 颜色、网络信号、最后在线时间）
    - 已安装应用 4 列网格 + 展开抽屉（BottomSheet 列表：名称、包名、版本号）
    - 运行状态图表（Today/7Days/30Days 切换，MPAndroidChart 柱状图）
    - Leaflet 地图（WebView 嵌入，坐标、精度、更新时间）
    - 远程操作 FAB + 气泡菜单（WiseViewer、Lock、Unlock）+ 确认对话框
    - 返回按钮
    - _需求: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8, 4.9, 4.10, 4.11, 4.12_

  - [ ] 10.3 实现 InstalledAppAdapter
    - 网格模式适配器（图标 + 名称）
    - 列表模式适配器（名称、包名、版本号，用于展开抽屉）
    - _需求: 4.4, 4.5_

- [ ] 11. 检查点 — 确保所有测试通过
  - 确保所有测试通过，如有疑问请向用户确认。

- [ ] 12. 实现向导公共组件
  - [ ] 12.1 实现 StepIndicatorView 自定义控件
    - 3 步骤进度指示器：当前步骤高亮，已完成步骤绿色对勾
    - _需求: 5.1_

  - [ ] 12.2 实现 DeviceTagSelectFragment（可复用）
    - 设备标签多选列表（标签名称 + 关联设备数量）
    - 搜索功能（按标签名称搜索）
    - _需求: 5.7, 5.8_

- [ ] 13. 实现批量应用安装向导
  - [ ] 13.1 实现 BatchInstallViewModel
    - 管理三步骤状态（TaskConfig → SelectApp → SelectDevices）
    - Step 1：任务名称、WiFi Only、Idle Time 开关及时间范围
    - Step 2：应用列表加载、单选应用、版本选择
    - Step 3：设备标签多选
    - 提交安装任务（调用 createInstallTask），处理成功/失败
    - _需求: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 5.9, 5.10, 5.11, 5.12_

  - [ ] 13.2 实现 BatchInstallWizardFragment 与布局
    - Step 1 表单：任务名称输入框（预填默认名称）、WiFi Only 开关、Idle Time 开关 + 时间范围选择器
    - Step 2：应用列表（图标、名称、描述）+ 右侧滑出版本选择面板 + 已选版本摘要
    - Step 3：复用 DeviceTagSelectFragment
    - 底部导航按钮（Next / Previous / Submit）
    - 成功提示跳转 TaskDetailPage；失败保留数据
    - _需求: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 5.8, 5.9, 5.10, 5.11, 5.12_

- [ ] 14. 实现批量应用卸载向导
  - [ ] 14.1 实现 BatchUninstallViewModel 与 BatchUninstallWizardFragment
    - 步骤式向导（TaskConfig → SelectApp → SelectDevices）
    - 从设备已安装应用列表加载可卸载应用（图标、名称、包名）
    - 支持多选应用
    - 提交卸载任务（调用 createUninstallTask），成功跳转 TaskDetailPage
    - _需求: 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ] 15. 实现批量 OTA 更新向导
  - [ ] 15.1 实现 OtaUpdateViewModel 与 OtaUpdateWizardFragment
    - 步骤式向导（TaskConfig → SelectFirmware → SelectDevices）
    - Task Config：任务名称、WiFi Only、Idle Time
    - 固件版本列表（版本号、发布日期、更新说明）
    - 设备标签多选
    - 提交 OTA 任务，成功跳转 TaskDetailPage
    - _需求: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6_

- [ ] 16. 实现批量推送指令向导
  - [ ] 16.1 实现 PushInstructionViewModel 与 PushInstructionWizardFragment
    - 步骤式向导（TaskConfig → SelectInstruction → SelectDevices）
    - 指令类型列表（名称、功能描述）
    - 选择指令后动态显示参数配置表单
    - 提交推送指令任务，成功跳转 TaskDetailPage
    - _需求: 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 17. 实现批量 WiseOS 系统设置向导
  - [ ] 17.1 实现 WiseOSSettingViewModel 与 WiseOSSettingWizardFragment
    - 步骤式向导（TaskConfig → ConfigureSettings → SelectDevices）
    - 分组卡片形式展示设置项（网络、安全、显示），提供开关/下拉/文本输入控件
    - 提交系统设置任务，成功跳转 TaskDetailPage
    - _需求: 9.1, 9.2, 9.3, 9.4, 9.5_

- [ ] 18. 实现批量文件推送向导
  - [ ] 18.1 实现 FilePushViewModel 与 FilePushWizardFragment
    - 步骤式向导（TaskConfig → SelectFile → SelectDevices）
    - 文件上传功能（选择本地文件，显示文件名、大小、类型）
    - 目标路径输入框
    - 提交文件推送任务，成功跳转 TaskDetailPage
    - _需求: 10.1, 10.2, 10.3, 10.4, 10.5_

- [ ] 19. 检查点 — 确保所有测试通过
  - 确保所有测试通过，如有疑问请向用户确认。

- [ ] 20. 实现任务管理模块
  - [ ] 20.1 实现 TaskListViewModel
    - 加载任务列表
    - 搜索（按任务名称）
    - 类型筛选（All/OTA update/Push app/Push instruction/Add device/File push/App uninstall/Suspend or resume）
    - 导航标签页切换（All task/My following/Partner's tasks）
    - _需求: 11.1, 11.2, 11.3, 11.4, 11.5, 11.8_

  - [ ] 20.2 实现 TaskListFragment、TaskListAdapter 与布局
    - 顶部返回按钮 + 标题
    - 搜索栏 + 筛选按钮（展开/收起类型标签行）
    - 三个导航标签页
    - 任务卡片列表（任务名称、类型标签颜色区分、进度条）
    - 加载状态（Spinner + "Loading tasks..."）
    - 空状态提示（图标 + "No tasks found"）
    - 点击卡片跳转 TaskDetailPage
    - _需求: 11.1, 11.2, 11.3, 11.4, 11.5, 11.6, 11.7, 11.8, 11.9, 11.10_

- [ ] 21. 实现任务详情模块
  - [ ] 21.1 实现 TaskDetailViewModel
    - 加载任务详情
    - 四标签页切换（Completed/Failed/Executing/Preparing，按 instructionStatus 筛选）
    - 10 秒轮询逻辑：全部到终态后自动停止
    - SN 搜索、分页
    - _需求: 12.1, 12.3, 12.4, 12.7, 12.9, 12.10, 12.11_

  - [ ] 21.2 实现 TaskDetailFragment、TaskDeviceAdapter 与布局
    - 粘性头部（返回按钮、任务名称、更多操作按钮）
    - 任务元信息（创建者、状态标签、创建时间、管理员、关注按钮）
    - 应用信息卡片（图标、名称、版本号、Update at leisure 开关、WiFi only 开关）
    - 设备统计摘要（总设备数、有效/无效设备数、添加记录链接）
    - 四个设备状态标签页（含设备数量）
    - 设备列表（SN、更新时间、状态标签颜色）+ SN 搜索框
    - 分页信息 + 页码点指示器
    - _需求: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6, 12.7, 12.8, 12.9_

  - [ ]* 21.3 编写 TaskDetailViewModel 单元测试
    - 测试轮询启停逻辑、标签页切换、终态自动停止轮询
    - _需求: 12.10, 12.11_

- [ ] 22. 实现 Navigation 导航图与全局集成
  - [ ] 22.1 配置 Navigation Component 导航图
    - 定义所有 Fragment 目的地和导航动作
    - LoginFragment → DashboardFragment（登录成功）
    - DashboardFragment → DeviceDetailFragment / DeviceListFragment / TaskListFragment / 各向导 Fragment
    - DeviceListFragment → DeviceDetailFragment
    - TaskListFragment → TaskDetailFragment
    - 各向导 Fragment → TaskDetailFragment（提交成功）
    - _需求: 1.9, 2.2, 2.5, 2.11, 3.6, 5.11, 6.5, 7.6, 8.5, 9.5, 10.5, 11.7_

  - [ ] 22.2 实现 Token 过期全局处理
    - 监听 TokenExpiredEvent，自动跳转 LoginFragment 并清除回退栈
    - _需求: 1.9_

- [ ] 23. 检查点 — 确保所有测试通过
  - 确保所有测试通过，如有疑问请向用户确认。

- [ ]* 24. 编写集成测试
  - [ ]* 24.1 编写登录流程集成测试
    - 使用 MockWebServer 模拟后端 API
    - 测试完整登录流程（含 Token 存储和自动附加）
    - 测试 401 Token 过期自动跳转登录
    - _需求: 1.9, 1.10, 1.11_

  - [ ]* 24.2 编写核心业务流程集成测试
    - 设备搜索 → 详情查看流程
    - 批量安装向导完整流程
    - 任务轮询启停流程
    - _需求: 2.4, 2.5, 4.1, 5.10, 5.11, 12.10, 12.11_

- [ ] 25. 最终检查点 — 确保所有测试通过
  - 确保所有测试通过，如有疑问请向用户确认。

## 备注

- 标记 `*` 的任务为可选任务，可跳过以加速 MVP 交付
- 每个任务均引用了对应的需求编号，确保可追溯性
- 检查点任务用于阶段性验证，确保增量开发的正确性
- 属性测试验证设计文档中定义的 8 个正确性属性
- 单元测试验证具体示例和边界条件
- 使用 Kotest Property Testing 进行属性测试，MockK 进行 mock，Espresso 进行 UI 测试
