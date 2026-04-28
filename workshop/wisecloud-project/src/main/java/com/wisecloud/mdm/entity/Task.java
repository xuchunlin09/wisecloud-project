package com.wisecloud.mdm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", length = 100)
    private String taskName;

    @Column(name = "trace_id", unique = true, nullable = false, length = 50)
    private String traceId;

    @Column(name = "task_type", nullable = false, length = 20)
    private String taskType;

    @Column(name = "target_app", length = 100)
    private String targetApp;

    @Column(name = "device_count")
    private Integer deviceCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
