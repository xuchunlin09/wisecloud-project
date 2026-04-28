package com.wisecloud.mdm.service;

import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.InstructionTypeEnum;
import com.wiseasy.openapi.request.ApplicationUploadAddRequest;
import com.wiseasy.openapi.request.DeviceDeviceDetailListRequest;
import com.wiseasy.openapi.request.DeviceVerifySnRequest;
import com.wiseasy.openapi.request.InstructionTaskDetailsRequest;
import com.wiseasy.openapi.request.InstructionTaskPushRequest;
import com.wiseasy.openapi.response.ApplicationUploadAddResponse;
import com.wiseasy.openapi.response.DeviceDeviceDetailListResponse;
import com.wiseasy.openapi.response.DeviceVerifySnResponse;
import com.wiseasy.openapi.response.InstructionTaskDetailsResponse;
import com.wiseasy.openapi.response.InstructionTaskPushResponse;
import com.wisecloud.mdm.dto.response.AppUploadResult;
import com.wisecloud.mdm.dto.response.SnVerifyResult;
import com.wisecloud.mdm.dto.response.TaskExecutionResult;
import com.wisecloud.mdm.exception.WiseCloudApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Unified wrapper for all WiseCloud SDK API calls.
 * Handles auto-batching (≤300 per batch), exception translation, and business error checking.
 */
@Service
public class WiseCloudService {

    private static final Logger log = LoggerFactory.getLogger(WiseCloudService.class);
    static final int BATCH_SIZE = 300;

    private final OpenApiClient openApiClient;

    public WiseCloudService(OpenApiClient openApiClient) {
        this.openApiClient = openApiClient;
    }

    // ─── Batch Utility ───────────────────────────────────────────────────

    /**
     * Split a list into batches of at most BATCH_SIZE elements.
     * Preserves element order; never produces empty batches.
     */
    public static <T> List<List<T>> splitIntoBatches(List<T> list, int batchSize) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be positive");
        }
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(new ArrayList<>(list.subList(i, Math.min(i + batchSize, list.size()))));
        }
        return batches;
    }

    // ─── verify/sn ──────────────────────────────────────────────────────

    /**
     * Batch-verify SN list. Auto-splits into batches of ≤300 and merges results.
     *
     * @param snList the SN list to verify
     * @return merged SnVerifyResult containing sucList and errList
     */
    public SnVerifyResult verifySn(List<String> snList) {
        List<String> allSuc = new ArrayList<>();
        List<SnVerifyResult.SnErrorItem> allErr = new ArrayList<>();

        List<List<String>> batches = splitIntoBatches(snList, BATCH_SIZE);
        for (List<String> batch : batches) {
            DeviceVerifySnRequest request = new DeviceVerifySnRequest();
            request.setSnList(new HashSet<>(batch));

            DeviceVerifySnResponse response = executeWithErrorHandling(request, DeviceVerifySnResponse.class);

            if (response.getSucList() != null) {
                allSuc.addAll(response.getSucList());
            }
            if (response.getErrList() != null) {
                for (DeviceVerifySnResponse.SnErrorInfo err : response.getErrList()) {
                    allErr.add(new SnVerifyResult.SnErrorItem(err.getSn(), err.getErrMsg()));
                }
            }
        }

        return new SnVerifyResult(allSuc, allErr);
    }

    // ─── device/detailList ──────────────────────────────────────────────

    /**
     * Batch-query device details. Auto-splits into batches of ≤300 and merges results.
     *
     * @param snList the SN list to query
     * @return merged list of device details
     */
    public List<DeviceDeviceDetailListResponse.DeviceDetail> queryDeviceDetails(List<String> snList) {
        List<DeviceDeviceDetailListResponse.DeviceDetail> allResults = new ArrayList<>();

        List<List<String>> batches = splitIntoBatches(snList, BATCH_SIZE);
        for (List<String> batch : batches) {
            DeviceDeviceDetailListRequest request = new DeviceDeviceDetailListRequest();
            request.setSnList(batch);

            DeviceDeviceDetailListResponse response = executeWithErrorHandling(request, DeviceDeviceDetailListResponse.class);

            if (response.getData() != null) {
                allResults.addAll(response.getData());
            }
        }

        return allResults;
    }

    // ─── application/upload/add ─────────────────────────────────────────

    /**
     * Upload an APK to the WiseCloud application library.
     *
     * @param fileData       APK file bytes
     * @param fileName       original file name
     * @param appAlias       application alias
     * @param appDescription optional description
     * @return upload result containing versionMD5, versionNumber, appName, appPackage
     */
    public AppUploadResult uploadApplication(byte[] fileData, String fileName,
                                             String appAlias, String appDescription) {
        ApplicationUploadAddRequest request = new ApplicationUploadAddRequest();
        request.putByteArrayFile(fileName, fileData);
        request.setAppAlias(appAlias);
        request.setAppDescription(appDescription);

        ApplicationUploadAddResponse response = executeWithErrorHandling(request, ApplicationUploadAddResponse.class);

        return new AppUploadResult(
                response.getVersionMD5(),
                response.getVersionNumber(),
                response.getAppName(),
                response.getAppPackage()
        );
    }

    // ─── instruction/task/push (install) ────────────────────────────────

    /**
     * Push an APK install instruction to a batch of devices.
     *
     * @param snList        target device SN list
     * @param versionMD5    application version MD5
     * @param versionNumber application version number
     * @param versionName   application version name
     * @param appName       application name
     * @return traceId for tracking the task
     */
    public String pushInstallInstruction(List<String> snList, String versionMD5,
                                         String versionNumber, String versionName,
                                         String appName) {
        InstructionTaskPushRequest request = new InstructionTaskPushRequest();
        request.setInstructionKey("apkInstall");
        request.setType(InstructionTypeEnum.BATCH_PUSH);
        request.setTarget(snList);

        Map<String, Object> param = new HashMap<>();
        param.put("versionMD5", versionMD5);
        param.put("versionNumber", versionNumber);
        param.put("versionName", versionName);
        param.put("appName", appName);
        request.setParam(param);

        InstructionTaskPushResponse response = executeWithErrorHandling(request, InstructionTaskPushResponse.class);
        return response.getTraceId();
    }

    // ─── instruction/task/push (uninstall) ──────────────────────────────

    /**
     * Push an app uninstall instruction to a batch of devices.
     *
     * @param snList  target device SN list
     * @param pkgName package name of the app to uninstall
     * @return traceId for tracking the task
     */
    public String pushUninstallInstruction(List<String> snList, String pkgName) {
        InstructionTaskPushRequest request = new InstructionTaskPushRequest();
        request.setInstructionKey("uninstallApp");
        request.setType(InstructionTypeEnum.BATCH_PUSH);
        request.setTarget(snList);

        Map<String, Object> param = new HashMap<>();
        param.put("pkgName", pkgName);
        request.setParam(param);

        InstructionTaskPushResponse response = executeWithErrorHandling(request, InstructionTaskPushResponse.class);
        return response.getTraceId();
    }

    // ─── instruction/task/details ───────────────────────────────────────

    /**
     * Query task execution details by traceId.
     *
     * @param traceId the task trace ID
     * @return task execution result with per-device records
     */
    public TaskExecutionResult queryTaskDetails(String traceId) {
        InstructionTaskDetailsRequest request = new InstructionTaskDetailsRequest();
        request.setTraceId(traceId);

        InstructionTaskDetailsResponse response = executeWithErrorHandling(request, InstructionTaskDetailsResponse.class);

        List<TaskExecutionResult.DeviceExecutionRecord> records = new ArrayList<>();
        if (response.getList() != null) {
            for (InstructionTaskDetailsResponse.DeviceExecutionRecord r : response.getList()) {
                records.add(new TaskExecutionResult.DeviceExecutionRecord(
                        r.getSn(),
                        r.getInstructionStatus(),
                        r.getExecuteCode(),
                        r.getExecuteMessage()
                ));
            }
        }

        return new TaskExecutionResult(records);
    }

    // ─── Unified Error Handling ─────────────────────────────────────────

    /**
     * Execute an SDK request with unified exception handling.
     * Catches OpenApiClientException (SDK errors) and checks response.isSuccess() (business errors).
     *
     * @param request       the SDK request object
     * @param responseClass the expected response class (used for logging)
     * @param <T>           response type extending BaseResponse
     * @return the successful response
     * @throws WiseCloudApiException if SDK throws or business error occurs
     */
    @SuppressWarnings("unchecked")
    private <T extends com.wiseasy.openapi.response.BaseResponse> T executeWithErrorHandling(
            Object request, Class<T> responseClass) {
        T response;
        try {
            response = (T) openApiClient.execute(request);
        } catch (OpenApiClientException e) {
            log.error("WiseCloud SDK error: code={}, msg={}", e.getCode(), e.getMsg(), e);
            throw new WiseCloudApiException(e.getCode(), e.getMsg(), e);
        }

        if (response == null) {
            log.error("WiseCloud API returned null response for {}", responseClass.getSimpleName());
            throw new WiseCloudApiException(-1, "WiseCloud API returned null response");
        }

        if (!response.isSuccess()) {
            log.error("WiseCloud business error: code={}, msg={}", response.getCode(), response.getMsg());
            throw new WiseCloudApiException(
                    parseCode(response.getCode()),
                    response.getMsg()
            );
        }

        return response;
    }

    private static int parseCode(String code) {
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
