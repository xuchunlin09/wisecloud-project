package com.wisecloud.mdm.controller;

import com.wisecloud.mdm.dto.response.ApiResponse;
import com.wisecloud.mdm.dto.response.AppUploadResponse;
import com.wisecloud.mdm.dto.response.ApplicationInfo;
import com.wisecloud.mdm.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<AppUploadResponse>> uploadApk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("appAlias") String appAlias,
            @RequestParam(value = "appDescription", required = false) String appDescription)
            throws IOException {
        AppUploadResponse result = applicationService.uploadApk(file, appAlias, appDescription);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplicationInfo>>> listApplications() {
        List<ApplicationInfo> result = applicationService.listApplications();
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
