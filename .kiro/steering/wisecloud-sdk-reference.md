---
inclusion: auto
---

# WiseCloud SDK 签名调用代码参考

在进行 WiseCloud 相关的代码开发时，必须参考 `signatureDemo/` 目录下的代码示例。该项目包含完整的 SDK 签名调用方式和接口调用样例。

## 参考代码位置

所有参考代码位于 `othersource/signatureDemo/` 目录下：

### 签名示例（demo 包）

- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/demo/NonEncryptedMessageSignature.java]]` — 非加密消息签名示例
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/demo/EncryptedMessageSignature.java]]` — 加密消息签名示例
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/demo/Main.java]]` — 签名调用入口示例

### 工具类（utils 包）

- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/utils/AesUtils.java]]` — AES 加解密工具
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/utils/Base64Util.java]]` — Base64 编解码工具

### SDK 接口调用样例（sdk/test 包）

以下是各类 API 接口的调用示例，开发新接口时应参考对应的 Test 文件了解请求参数构造、签名流程和调用方式：

- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/CommonTest.java]]` — 通用调用基类/公共逻辑
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/DeviceDetailsTest.java]]` — 设备详情查询
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/DeviceSingleJoinNetworkTest.java]]` — 单设备入网
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/DeviceBatchJoinNetworkTest.java]]` — 批量设备入网
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/InstructionTaskPushTest.java]]` — 指令任务下发
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/InstructionTaskQueryTest.java]]` — 指令任务查询
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/ApplicationAddTest.java]]` — 应用添加
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/ApplicationUploadTest.java]]` — 应用上传
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/MerchantStaffAddTest.java]]` — 商户员工添加
- `#[[file:othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/MerchantStaffListTest.java]]` — 商户员工列表

> 更多接口样例请查看 `othersource/signatureDemo/src/main/java/com/lianwuzizai/signature/sdk/test/` 目录下的其他 Test 文件。

### 签名说明文档

- `#[[file:othersource/signatureDemo/README.md]]` — 签名规则说明（非加密/加密消息签名流程）

### 项目依赖

- `#[[file:othersource/signatureDemo/pom.xml]]` — Maven 依赖配置

## 开发规范

1. 所有与 WiseCloud 平台的 API 交互必须按照 signatureDemo 中的签名流程进行签名
2. 非加密消息：参数按 ASCII 排序后拼接，再使用 accessKeySecret 进行 MD5 或 HmacSHA256 签名
3. 加密消息：使用 AES（密钥为 accessKeySecret）加密后，对密文直接签名
4. 新增接口调用时，优先参考 `sdk/test/` 下已有的同类接口样例，保持调用风格一致
5. 工具类（AesUtils、Base64Util）应直接复用，避免重复实现
