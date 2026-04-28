package com.wisecloud.mdm.service;

import com.wiseasy.openapi.response.DeviceDeviceDetailListResponse;
import com.wisecloud.mdm.dto.response.*;
import com.wisecloud.mdm.entity.Device;
import com.wisecloud.mdm.exception.BusinessException;
import com.wisecloud.mdm.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Device management business logic: import, overview, search, detail.
 */
@Service
public class DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);

    private final WiseCloudService wiseCloudService;
    private final DeviceRepository deviceRepository;

    public DeviceService(WiseCloudService wiseCloudService, DeviceRepository deviceRepository) {
        this.wiseCloudService = wiseCloudService;
        this.deviceRepository = deviceRepository;
    }

    /**
     * Import devices by SN list.
     * Flow: verifySn → filter already-existing SNs → queryDeviceDetails for new SNs → persist → return result.
     */
    public DeviceImportResponse importDevices(List<String> snList) {
        List<String> successList = new ArrayList<>();
        List<SnFailInfo> failList = new ArrayList<>();

        // Step 1: Verify SNs via WiseCloud API
        SnVerifyResult verifyResult = wiseCloudService.verifySn(snList);

        // Collect verification failures
        if (verifyResult.getErrList() != null) {
            for (SnVerifyResult.SnErrorItem err : verifyResult.getErrList()) {
                failList.add(new SnFailInfo(err.getSn(), err.getErrMsg()));
            }
        }

        List<String> verifiedSns = verifyResult.getSucList() != null
                ? verifyResult.getSucList()
                : Collections.emptyList();

        // Step 2: Filter out already-existing SNs in local DB (mark as "已导入")
        List<String> newSns = new ArrayList<>();
        for (String sn : verifiedSns) {
            if (deviceRepository.existsBySn(sn)) {
                failList.add(new SnFailInfo(sn, "已导入"));
            } else {
                newSns.add(sn);
            }
        }

        // Step 3: Query device details from WiseCloud for new SNs and persist
        if (!newSns.isEmpty()) {
            List<DeviceDeviceDetailListResponse.DeviceDetail> details =
                    wiseCloudService.queryDeviceDetails(newSns);

            // Build a lookup map by SN for quick access
            Map<String, DeviceDeviceDetailListResponse.DeviceDetail> detailMap = details.stream()
                    .collect(Collectors.toMap(
                            DeviceDeviceDetailListResponse.DeviceDetail::getSn,
                            Function.identity(),
                            (a, b) -> a
                    ));

            for (String sn : newSns) {
                DeviceDeviceDetailListResponse.DeviceDetail detail = detailMap.get(sn);
                Device device = new Device();
                device.setSn(sn);
                if (detail != null) {
                    device.setDeviceName(detail.getDeviceName());
                    device.setDeviceType(detail.getDeviceType());
                    device.setStatus(detail.getStatus());
                    device.setMerchantNo(detail.getMerchantNo());
                    device.setMerchantName(detail.getMerchantName());
                    device.setStoreName(detail.getStoreName());
                }
                deviceRepository.save(device);
                successList.add(sn);
            }
        }

        return new DeviceImportResponse(
                successList.size(),
                failList.size(),
                successList,
                failList
        );
    }

    /**
     * Get device overview: total count, online count, online rate.
     * Queries all local SNs then fetches real-time status from WiseCloud.
     */
    public DeviceOverviewResponse getOverview() {
        List<Device> allDevices = deviceRepository.findAll();
        int totalCount = allDevices.size();

        if (totalCount == 0) {
            return new DeviceOverviewResponse(0, 0, "0.0%");
        }

        List<String> allSns = allDevices.stream()
                .map(Device::getSn)
                .collect(Collectors.toList());

        List<DeviceDeviceDetailListResponse.DeviceDetail> details =
                wiseCloudService.queryDeviceDetails(allSns);

        int onlineCount = 0;
        for (DeviceDeviceDetailListResponse.DeviceDetail detail : details) {
            if (detail.getOnlineStatus() != null && detail.getOnlineStatus() == 1) {
                onlineCount++;
            }
        }

        String onlineRate = BigDecimal.valueOf(onlineCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount), 1, RoundingMode.HALF_UP)
                .toPlainString() + "%";

        return new DeviceOverviewResponse(totalCount, onlineCount, onlineRate);
    }

    /**
     * Fuzzy search devices by SN keyword in local DB.
     */
    public List<DeviceSummary> searchBySn(String keyword) {
        List<Device> devices = deviceRepository.findBySnContaining(keyword);
        return devices.stream()
                .map(d -> new DeviceSummary(d.getSn(), d.getDeviceName(), d.getDeviceType(), d.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Get single device real-time detail from WiseCloud API.
     */
    public DeviceDetailResponse getDeviceDetail(String sn) {
        List<DeviceDeviceDetailListResponse.DeviceDetail> details =
                wiseCloudService.queryDeviceDetails(List.of(sn));

        if (details.isEmpty()) {
            throw new BusinessException(404, "设备不存在或无法查询: " + sn);
        }

        DeviceDeviceDetailListResponse.DeviceDetail detail = details.get(0);
        DeviceDetailResponse response = new DeviceDetailResponse();
        response.setSn(detail.getSn());
        response.setDeviceName(detail.getDeviceName());
        response.setDeviceType(detail.getDeviceType());
        response.setStatus(detail.getStatus());
        response.setOnlineStatus(detail.getOnlineStatus());
        response.setActivationTime(detail.getActivationTime());
        response.setMerchantNo(detail.getMerchantNo());
        response.setMerchantName(detail.getMerchantName());
        response.setStoreName(detail.getStoreName());
        response.setOtaVersionName(detail.getOtaVersionName());
        response.setElectricRate(detail.getElectricRate());
        response.setWifiStatus(detail.getWifiStatus());
        response.setGprsStatus(detail.getGprsStatus());
        response.setLongitude(detail.getLongitude());
        response.setLatitude(detail.getLatitude());

        if (detail.getInstallApps() != null) {
            List<DeviceDetailResponse.InstalledAppInfo> apps = detail.getInstallApps().stream()
                    .map(app -> new DeviceDetailResponse.InstalledAppInfo(
                            app.getAppName(), app.getAppPackage(), app.getVersionName()))
                    .collect(Collectors.toList());
            response.setInstallApps(apps);
        }

        return response;
    }
}
