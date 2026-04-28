# 需求文档：WiseCloud 移动端 App

## 简介

WiseCloud 移动端 App 是一款面向商用 Android 设备（如 POS 终端）的智能设备管理平台移动客户端。该应用通过 WiseCloud 开放 API 实现设备远程管理，核心功能包括：用户认证与登录、设备信息查询与监控、批量任务管理（应用安装/卸载、OTA 更新、指令推送、系统设置、文件推送）、任务执行状态追踪，以及用户个人中心与消息通知。后端基于 Spring Boot 对接 WiseCloud API 并提供 RESTful 接口，移动端提供操作界面。

## 术语表

- **WiseCloud_App**: WiseCloud 移动端应用程序客户端
- **Login_Module**: 用户认证与登录模块
- **Dashboard**: 首页仪表盘，展示设备概览、搜索入口和快捷操作
- **Device_List_Page**: 设备列表浏览页面，支持筛选、排序和分页
- **Device_Detail_Page**: 单台设备的详细信息展示页面
- **Task_Management_Page**: 批量任务列表管理页面
- **Task_Detail_Page**: 单个任务的详细执行状态页面
- **Batch_App_Install_Wizard**: 批量应用安装三步骤向导流程
- **Batch_App_Uninstall_Wizard**: 批量应用卸载向导流程
- **OTA_Update_Wizard**: 批量 OTA 固件更新向导流程
- **Push_Instruction_Wizard**: 批量推送指令向导流程
- **WiseOS_Setting_Wizard**: 批量 WiseOS 系统设置推送向导流程
- **File_Push_Wizard**: 批量文件推送向导流程
- **Batch_Menu**: 首页底部弹出式批量操作菜单
- **Remote_Action_Menu**: 设备详情页远程操作气泡菜单
- **User_Profile_Page**: 用户个人中心页面
- **Notification_Center**: 消息通知中心
- **SN**: 设备序列号（Serial Number），设备唯一标识
- **MFA**: 多因素认证（Multi-Factor Authentication）
- **OTA**: 空中下载技术（Over-The-Air），用于固件远程更新
- **JWT**: JSON Web Token，用于用户身份鉴权
- **traceId**: 任务流水号，用于追踪批量任务执行状态
- **instructionStatus**: 指令执行状态码（1=preparing, 2=executing, 3=successful, 4=failed）
- **WiseCloud_API**: WiseCloud 开放平台 API 服务

## 需求

### 需求 1：用户登录认证

**用户故事：** 作为设备管理员，我希望通过邮箱密码加验证码的方式安全登录 App，以便访问设备管理功能。

#### 验收标准

1. THE Login_Module SHALL 提供邮箱输入框和密码输入框，邮箱输入框支持标准邮箱格式校验
2. WHEN 用户点击密码输入框右侧的眼睛图标时，THE Login_Module SHALL 在明文和密文之间切换密码显示状态
3. WHEN 用户选择邮箱验证方式时，THE Login_Module SHALL 显示验证码输入框和"Get Code"按钮
4. WHEN 用户点击"Get Code"按钮时，THE Login_Module SHALL 向用户邮箱发送验证码，并启动 60 秒倒计时，倒计时期间按钮显示剩余秒数且不可点击
5. WHEN 用户选择 MFA 验证方式时，THE Login_Module SHALL 显示 6 个独立的数字输入框，每个输入框仅接受 1 位数字，输入后自动跳转到下一个输入框
6. WHEN 用户在 MFA 输入框中按退格键且当前输入框为空时，THE Login_Module SHALL 将焦点移至前一个输入框并清除其内容
7. THE Login_Module SHALL 提供"Switch to MFA"和"Switch to Email"链接，允许用户在邮箱验证码和 MFA 验证方式之间切换
8. WHEN 用户勾选"Remember Password"复选框时，THE Login_Module SHALL 在本地安全存储中保存用户凭证，下次启动时自动填充
9. WHEN 用户提交有效的邮箱、密码和验证码时，THE Login_Module SHALL 调用后端登录 API 获取 JWT Token，并将 Token 安全存储在本地
10. IF 登录请求返回认证失败错误，THEN THE Login_Module SHALL 显示明确的错误提示信息，不清除已输入的邮箱地址
11. IF 网络连接不可用，THEN THE Login_Module SHALL 显示网络异常提示，引导用户检查网络设置

### 需求 2：首页仪表盘

**用户故事：** 作为设备管理员，我希望在首页快速查看设备概览信息和常用操作入口，以便高效管理设备。

#### 验收标准

1. THE Dashboard SHALL 在页面顶部显示欢迎信息，包含当前登录用户的用户名
2. THE Dashboard SHALL 显示任务管理入口卡片，点击后跳转到 Task_Management_Page
3. THE Dashboard SHALL 提供设备搜索框，支持按 SN 号或 SN 末几位进行模糊搜索
4. WHEN 用户在搜索框中输入文本时，THE Dashboard SHALL 在 300 毫秒防抖延迟后显示匹配的设备搜索结果下拉列表，每条结果包含 SN 号、设备型号和在线状态
5. WHEN 用户点击搜索结果中的某台设备时，THE Dashboard SHALL 跳转到该设备的 Device_Detail_Page
6. THE Dashboard SHALL 显示设备概览统计卡片，包含总设备数和在线设备数及在线率百分比
7. THE Dashboard SHALL 显示最近 7 天活动柱状图，每根柱子代表一天的活跃设备数
8. THE Dashboard SHALL 在页面右下角显示浮动批量任务按钮，带有脉冲动画效果
9. WHEN 用户点击浮动批量任务按钮时，THE Dashboard SHALL 显示半透明遮罩层，并从底部滑出 Batch_Menu
10. THE Batch_Menu SHALL 包含以下操作选项：App Install、OTA Update、App Uninstall、Push Instruction、WiseOS Setting
11. WHEN 用户点击 Batch_Menu 中的某个操作选项时，THE Dashboard SHALL 关闭菜单并跳转到对应的批量操作向导页面
12. WHEN 用户点击遮罩层或按下 ESC 键时，THE Batch_Menu SHALL 关闭并恢复页面正常状态

### 需求 3：设备列表页

**用户故事：** 作为设备管理员，我希望浏览和筛选所有已导入的设备，以便快速定位目标设备。

#### 验收标准

1. THE Device_List_Page SHALL 以列表形式展示所有已导入设备，每条记录包含 SN 号、设备型号、在线状态和最后在线时间
2. THE Device_List_Page SHALL 提供搜索框，支持按 SN 号模糊搜索设备
3. THE Device_List_Page SHALL 提供筛选功能，支持按在线状态（全部、在线、离线）筛选设备
4. THE Device_List_Page SHALL 支持下拉刷新操作，触发时重新从后端拉取设备列表数据
5. THE Device_List_Page SHALL 支持分页加载或无限滚动加载，每次加载不超过 20 条记录
6. WHEN 用户点击列表中的某台设备时，THE Device_List_Page SHALL 跳转到该设备的 Device_Detail_Page
7. WHILE 设备列表数据正在加载时，THE Device_List_Page SHALL 显示加载指示器（Spinner）
8. IF 设备列表为空，THEN THE Device_List_Page SHALL 显示空状态提示，包含图标和说明文字

### 需求 4：设备详情

**用户故事：** 作为设备管理员，我希望查看单台设备的完整信息和运行状态，以便进行设备诊断和远程操作。

#### 验收标准

1. THE Device_Detail_Page SHALL 在顶部渐变色头部区域显示设备 SN 号、设备型号、在线状态标签和设备图片
2. THE Device_Detail_Page SHALL 显示基本信息卡片，包含 Android 版本、ROM 版本、SP 版本、DMS 版本和 OEM 版本
3. THE Device_Detail_Page SHALL 显示设备状态卡片，包含电池电量（含进度条，根据电量高低显示绿色/黄色/红色）、网络信号状态和最后在线时间
4. THE Device_Detail_Page SHALL 以 4 列网格形式显示已安装应用列表，每个应用显示图标和名称
5. WHEN 用户点击已安装应用区域的展开图标时，THE Device_Detail_Page SHALL 从底部滑出应用抽屉，以列表形式显示全部已安装应用的名称、包名和版本号
6. THE Device_Detail_Page SHALL 显示运行状态图表卡片，支持 Today、7Days、30Days 三个时间维度切换，以柱状图展示上线和下线次数
7. THE Device_Detail_Page SHALL 使用 Leaflet 地图组件显示设备位置，包含坐标信息、定位精度和最后更新时间
8. THE Device_Detail_Page SHALL 在页面右下角显示远程操作浮动按钮
9. WHEN 用户点击远程操作浮动按钮时，THE Device_Detail_Page SHALL 显示 Remote_Action_Menu 气泡弹窗，包含 WiseViewer（远程屏幕共享）、Lock Device（锁定设备）、Unlock Device（解锁设备）等操作选项
10. WHEN 用户选择 Lock Device 或 Unlock Device 操作时，THE Device_Detail_Page SHALL 弹出确认对话框，用户确认后才执行操作
11. WHEN 用户点击遮罩层或关闭按钮时，THE Remote_Action_Menu SHALL 关闭
12. THE Device_Detail_Page SHALL 在顶部左侧提供返回按钮，点击后返回上一页

### 需求 5：批量应用安装

**用户故事：** 作为设备管理员，我希望通过三步骤向导将应用批量安装到多台设备上，以便高效部署应用。

#### 验收标准

1. THE Batch_App_Install_Wizard SHALL 在页面顶部显示 3 步骤进度指示器（Task Config → Select App → Select Devices），当前步骤高亮显示，已完成步骤显示绿色对勾
2. WHILE 处于 Step 1（Task Config）时，THE Batch_App_Install_Wizard SHALL 显示任务名称输入框（预填默认名称）、WiFi Only 开关和 Idle Time 开关
3. WHEN 用户开启 Idle Time 开关时，THE Batch_App_Install_Wizard SHALL 展开时间范围选择器，提供 From 和 To 两个时间下拉框
4. WHILE 处于 Step 2（Select App）时，THE Batch_App_Install_Wizard SHALL 以列表形式显示可用应用，每个应用显示图标、名称和描述，支持单选
5. WHEN 用户选中某个应用时，THE Batch_App_Install_Wizard SHALL 从右侧滑出版本选择面板，显示该应用的所有可用版本，每个版本包含版本号、发布日期和文件大小
6. WHEN 用户选择某个版本后，THE Batch_App_Install_Wizard SHALL 在应用卡片下方显示已选版本信息摘要
7. WHILE 处于 Step 3（Select Devices）时，THE Batch_App_Install_Wizard SHALL 显示设备标签列表（多选），每个标签显示标签名称和关联设备数量
8. THE Batch_App_Install_Wizard SHALL 在 Step 3 提供搜索功能，支持按标签名称搜索
9. THE Batch_App_Install_Wizard SHALL 在底部显示导航按钮，Step 1 显示 Next，Step 2 和 Step 3 显示 Previous 和 Next/Submit
10. WHEN 用户在 Step 3 点击 Submit 时，THE Batch_App_Install_Wizard SHALL 调用 WiseCloud_API 的 instruction/task/push 接口（instructionKey=apkInstall）下发安装任务，并返回 traceId
11. IF 任务提交成功，THEN THE Batch_App_Install_Wizard SHALL 显示成功提示并跳转到 Task_Detail_Page
12. IF 任务提交失败，THEN THE Batch_App_Install_Wizard SHALL 显示错误信息，保留用户已填写的数据

### 需求 6：批量应用卸载

**用户故事：** 作为设备管理员，我希望批量卸载设备上的指定应用，以便清理不再需要的软件。

#### 验收标准

1. THE Batch_App_Uninstall_Wizard SHALL 采用步骤式向导流程（Task Config → Select App → Select Devices）
2. WHILE 处于选择应用步骤时，THE Batch_App_Uninstall_Wizard SHALL 从设备已安装应用列表中加载可卸载的应用，显示应用图标、名称和包名
3. THE Batch_App_Uninstall_Wizard SHALL 支持选择多个应用进行批量卸载
4. WHEN 用户提交卸载任务时，THE Batch_App_Uninstall_Wizard SHALL 调用 WiseCloud_API 的 instruction/task/push 接口（instructionKey=uninstallApp），为每个选中的应用包名下发卸载指令
5. IF 卸载任务提交成功，THEN THE Batch_App_Uninstall_Wizard SHALL 显示成功提示并跳转到 Task_Detail_Page

### 需求 7：批量 OTA 更新

**用户故事：** 作为设备管理员，我希望批量推送固件更新到多台设备，以便保持设备系统版本统一。

#### 验收标准

1. THE OTA_Update_Wizard SHALL 采用步骤式向导流程（Task Config → Select Firmware → Select Devices）
2. WHILE 处于 Task Config 步骤时，THE OTA_Update_Wizard SHALL 提供任务名称输入、WiFi Only 开关和 Idle Time 开关及时间范围选择
3. WHILE 处于 Select Firmware 步骤时，THE OTA_Update_Wizard SHALL 显示可用固件版本列表，每个版本包含版本号、发布日期和更新说明
4. WHILE 处于 Select Devices 步骤时，THE OTA_Update_Wizard SHALL 显示设备标签列表供用户多选目标设备组
5. WHEN 用户提交 OTA 更新任务时，THE OTA_Update_Wizard SHALL 调用 WiseCloud_API 下发 OTA 更新指令并返回 traceId
6. IF OTA 任务提交成功，THEN THE OTA_Update_Wizard SHALL 显示成功提示并跳转到 Task_Detail_Page

### 需求 8：批量推送指令

**用户故事：** 作为设备管理员，我希望向多台设备批量发送远程指令，以便执行远程重启、调试模式切换等操作。

#### 验收标准

1. THE Push_Instruction_Wizard SHALL 采用步骤式向导流程（Task Config → Select Instruction → Select Devices）
2. WHILE 处于 Select Instruction 步骤时，THE Push_Instruction_Wizard SHALL 显示可用指令类型列表，包含指令名称和功能描述
3. WHEN 用户选择某个指令类型后，THE Push_Instruction_Wizard SHALL 根据指令类型动态显示所需的参数配置表单
4. WHEN 用户提交推送指令任务时，THE Push_Instruction_Wizard SHALL 调用 WiseCloud_API 的 instruction/task/push 接口下发指令并返回 traceId
5. IF 指令推送任务提交成功，THEN THE Push_Instruction_Wizard SHALL 显示成功提示并跳转到 Task_Detail_Page

### 需求 9：批量 WiseOS 系统设置

**用户故事：** 作为设备管理员，我希望批量推送系统配置到多台设备，以便统一管理设备的网络、安全和显示等设置。

#### 验收标准

1. THE WiseOS_Setting_Wizard SHALL 采用步骤式向导流程（Task Config → Configure Settings → Select Devices）
2. WHILE 处于 Configure Settings 步骤时，THE WiseOS_Setting_Wizard SHALL 提供系统设置配置表单，包含网络设置、安全策略和显示偏好等配置项
3. THE WiseOS_Setting_Wizard SHALL 以分组卡片形式展示各类设置项，每个设置项提供开关、下拉选择或文本输入等适当的交互控件
4. WHEN 用户提交系统设置任务时，THE WiseOS_Setting_Wizard SHALL 调用 WiseCloud_API 下发系统设置指令并返回 traceId
5. IF 系统设置任务提交成功，THEN THE WiseOS_Setting_Wizard SHALL 显示成功提示并跳转到 Task_Detail_Page

### 需求 10：批量文件推送

**用户故事：** 作为设备管理员，我希望批量推送配置文件或资源文件到多台设备，以便远程更新设备配置。

#### 验收标准

1. THE File_Push_Wizard SHALL 采用步骤式向导流程（Task Config → Select File → Select Devices）
2. WHILE 处于 Select File 步骤时，THE File_Push_Wizard SHALL 提供文件上传功能，支持选择本地文件并显示文件名、大小和类型
3. THE File_Push_Wizard SHALL 提供目标路径输入框，指定文件在设备上的存放路径
4. WHEN 用户提交文件推送任务时，THE File_Push_Wizard SHALL 上传文件并调用 WiseCloud_API 下发文件推送指令，返回 traceId
5. IF 文件推送任务提交成功，THEN THE File_Push_Wizard SHALL 显示成功提示并跳转到 Task_Detail_Page

### 需求 11：任务管理

**用户故事：** 作为设备管理员，我希望查看和管理所有批量任务的执行状态，以便追踪任务进度。

#### 验收标准

1. THE Task_Management_Page SHALL 在顶部显示返回按钮和"Task Management"标题
2. THE Task_Management_Page SHALL 提供搜索栏，支持按任务名称搜索
3. THE Task_Management_Page SHALL 提供筛选按钮，点击后展开/收起类型筛选标签行
4. THE Task_Management_Page SHALL 提供三个导航标签页：All task、My following、Partner's tasks
5. THE Task_Management_Page SHALL 提供类型筛选标签，包含 All、OTA update、Push app、Push instruction、Add device、File push、App uninstall、Suspend or resume
6. THE Task_Management_Page SHALL 以卡片列表形式展示任务，每张卡片包含任务名称、任务类型标签（颜色区分：In Progress 蓝色、Completed 绿色、Failed 红色、Pending 灰色）和进度条（显示完成数/总数）
7. WHEN 用户点击某张任务卡片时，THE Task_Management_Page SHALL 跳转到该任务的 Task_Detail_Page
8. WHEN 用户切换导航标签页或选择类型筛选标签时，THE Task_Management_Page SHALL 实时过滤任务列表
9. WHILE 任务列表数据正在加载时，THE Task_Management_Page SHALL 显示加载状态（Spinner + "Loading tasks..."文字）
10. IF 筛选结果为空，THEN THE Task_Management_Page SHALL 显示空状态提示（图标 + "No tasks found" + "Try adjusting your search or filter"）

### 需求 12：任务详情

**用户故事：** 作为设备管理员，我希望查看单个任务的详细执行状态和各设备的执行结果，以便排查失败设备。

#### 验收标准

1. THE Task_Detail_Page SHALL 在粘性头部显示返回按钮、任务名称和更多操作按钮
2. THE Task_Detail_Page SHALL 显示任务元信息，包含创建者名称（可编辑）、状态标签、创建时间、管理员和关注按钮
3. THE Task_Detail_Page SHALL 提供四个设备状态标签页：Completed、Failed、Executing、Preparing，每个标签页显示对应状态的设备数量
4. WHEN 用户切换设备状态标签页时，THE Task_Detail_Page SHALL 调用 WiseCloud_API 的 instruction/task/details 接口，按 instructionStatus 值（3=Completed, 4=Failed, 2=Executing, 1=Preparing）筛选并显示对应设备列表
5. THE Task_Detail_Page SHALL 显示应用信息卡片，包含应用图标、名称、版本号、Update at leisure 开关和 WiFi only 开关
6. THE Task_Detail_Page SHALL 显示设备统计摘要，包含总设备数、有效设备数、无效设备数和设备添加记录链接
7. THE Task_Detail_Page SHALL 在设备列表区域提供搜索框，支持按 SN 号搜索设备
8. THE Task_Detail_Page SHALL 以列表形式显示设备执行结果，每条记录包含 SN 号、更新时间和状态标签（Succeed 绿色/Failed 红色）
9. THE Task_Detail_Page SHALL 在设备列表底部显示分页信息（当前显示数/总数）和页码点指示器
10. WHILE 任务处于非终态（存在 instructionStatus 为 1 或 2 的设备）时，THE Task_Detail_Page SHALL 每 10 秒自动轮询一次任务执行状态
11. WHEN 所有设备均到达终态（instructionStatus 为 3 或 4）时，THE Task_Detail_Page SHALL 停止自动轮询

