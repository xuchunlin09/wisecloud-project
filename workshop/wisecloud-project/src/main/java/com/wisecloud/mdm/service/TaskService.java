package com.wisecloud.mdm.service;

import com.wisecloud.mdm.dto.request.InstallTaskRequest;
import com.wisecloud.mdm.dto.request.UninstallTaskRequest;
import com.wisecloud.mdm.dto.response.*;
import com.wisecloud.mdm.entity.Task;
import com.wisecloud.mdm.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Task management business logic: create install/uninstall tasks, list tasks with progress, query task details.
 */
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final WiseCloudService wiseCloudService;
    private final TaskRepository taskRepository;

    public TaskService(WiseCloudService wiseCloudService, TaskRepository taskRepository) {
        this.wiseCloudService = wiseCloudService;
        this.taskRepository = taskRepository;
    }

    /**
     * Create an install task: push install instruction via WiseCloud, then persist task metadata.
     */
    public TaskCreateResponse createInstallTask(InstallTaskRequest request) {
        String traceId = wiseCloudService.pushInstallInstruction(
                request.snList(),
                request.versionMD5(),
                request.versionNumber(),
                request.versionName(),
                request.appName()
        );

        Task task = new Task();
        task.setTraceId(traceId);
        task.setTaskName(request.taskName());
        task.setTaskType("install");
        task.setTargetApp(request.appName());
        task.setDeviceCount(request.snList().size());
        taskRepository.save(task);

        log.info("Install task created: traceId={}, taskName={}, deviceCount={}",
                traceId, request.taskName(), request.snList().size());

        return new TaskCreateResponse(traceId, request.taskName());
    }

    /**
     * Create an uninstall task: push uninstall instruction via WiseCloud, then persist task metadata.
     */
    public TaskCreateResponse createUninstallTask(UninstallTaskRequest request) {
        String traceId = wiseCloudService.pushUninstallInstruction(
                request.snList(),
                request.pkgName()
        );

        Task task = new Task();
        task.setTraceId(traceId);
        task.setTaskType("uninstall");
        task.setTargetApp(request.pkgName());
        task.setDeviceCount(request.snList().size());
        taskRepository.save(task);

        log.info("Uninstall task created: traceId={}, pkgName={}, deviceCount={}",
                traceId, request.pkgName(), request.snList().size());

        return new TaskCreateResponse(traceId, null);
    }

    /**
     * List all tasks with real-time progress from WiseCloud.
     * For each task, queries WiseCloud for execution details and aggregates device status counts.
     */
    public List<TaskSummary> listTasks() {
        List<Task> tasks = taskRepository.findAll();
        List<TaskSummary> summaries = new ArrayList<>();

        for (Task task : tasks) {
            TaskExecutionResult result = wiseCloudService.queryTaskDetails(task.getTraceId());
            List<TaskExecutionResult.DeviceExecutionRecord> records = result.getRecords();

            int completedCount = 0;
            int failedCount = 0;
            int total = records != null ? records.size() : 0;

            if (records != null) {
                for (TaskExecutionResult.DeviceExecutionRecord record : records) {
                    if (record.getInstructionStatus() != null) {
                        if (record.getInstructionStatus() == 3) {
                            completedCount++;
                        } else if (record.getInstructionStatus() == 4) {
                            failedCount++;
                        }
                    }
                }
            }

            String progress = completedCount + "/" + total + " Completed";

            summaries.add(new TaskSummary(
                    task.getTraceId(),
                    task.getTaskName(),
                    task.getTaskType(),
                    task.getTargetApp(),
                    task.getDeviceCount(),
                    completedCount,
                    failedCount,
                    progress,
                    task.getCreatedAt()
            ));
        }

        return summaries;
    }

    /**
     * Get task details: query WiseCloud for execution records and group by instructionStatus.
     * Groups: completed (3), failed (4), executing (2), preparing (1).
     */
    public TaskDetailResponse getTaskDetails(String traceId) {
        TaskExecutionResult result = wiseCloudService.queryTaskDetails(traceId);
        List<TaskExecutionResult.DeviceExecutionRecord> records = result.getRecords();

        List<TaskDeviceStatus> completed = new ArrayList<>();
        List<TaskDeviceStatus> failed = new ArrayList<>();
        List<TaskDeviceStatus> executing = new ArrayList<>();
        List<TaskDeviceStatus> preparing = new ArrayList<>();

        if (records != null) {
            for (TaskExecutionResult.DeviceExecutionRecord record : records) {
                TaskDeviceStatus status = new TaskDeviceStatus(
                        record.getSn(),
                        record.getInstructionStatus() != null ? record.getInstructionStatus() : 0,
                        record.getExecuteCode(),
                        record.getExecuteMessage()
                );

                if (record.getInstructionStatus() != null) {
                    switch (record.getInstructionStatus()) {
                        case 3 -> completed.add(status);
                        case 4 -> failed.add(status);
                        case 2 -> executing.add(status);
                        case 1 -> preparing.add(status);
                    }
                }
            }
        }

        return new TaskDetailResponse(completed, failed, executing, preparing);
    }
}
