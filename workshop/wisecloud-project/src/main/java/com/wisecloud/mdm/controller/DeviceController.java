package com.wisecloud.mdm.controller;

import com.wisecloud.mdm.dto.request.DeviceImportRequest;
import com.wisecloud.mdm.dto.response.*;
import com.wisecloud.mdm.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<DeviceImportResponse>> importDevices(
            @Valid @RequestBody DeviceImportRequest request) {
        DeviceImportResponse result = deviceService.importDevices(request.snList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DeviceOverviewResponse>> getOverview() {
        DeviceOverviewResponse result = deviceService.getOverview();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DeviceSummary>>> searchDevices(
            @RequestParam String keyword) {
        List<DeviceSummary> result = deviceService.searchBySn(keyword);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{sn}/detail")
    public ResponseEntity<ApiResponse<DeviceDetailResponse>> getDeviceDetail(
            @PathVariable String sn) {
        DeviceDetailResponse result = deviceService.getDeviceDetail(sn);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
