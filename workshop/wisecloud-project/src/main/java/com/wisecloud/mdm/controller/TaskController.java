package com.wisecloud.mdm.controller;

import com.wisecloud.mdm.dto.request.InstallTaskRequest;
import com.wisecloud.mdm.dto.request.UninstallTaskRequest;
import com.wisecloud.mdm.dto.response.ApiResponse;
import com.wisecloud.mdm.dto.response.TaskCreateResponse;
import com.wisecloud.mdm.dto.response.TaskDetailResponse;
import com.wisecloud.mdm.dto.response.TaskSummary;
import com.wisecloud.mdm.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/install")
    public ResponseEntity<ApiResponse<TaskCreateResponse>> createInstallTask(
            @RequestBody InstallTaskRequest request) {
        TaskCreateResponse result = taskService.createInstallTask(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/uninstall")
    public ResponseEntity<ApiResponse<TaskCreateResponse>> createUninstallTask(
            @RequestBody UninstallTaskRequest request) {
        TaskCreateResponse result = taskService.createUninstallTask(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskSummary>>> listTasks() {
        List<TaskSummary> result = taskService.listTasks();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{traceId}/details")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> getTaskDetails(
            @PathVariable String traceId) {
        TaskDetailResponse result = taskService.getTaskDetails(traceId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
