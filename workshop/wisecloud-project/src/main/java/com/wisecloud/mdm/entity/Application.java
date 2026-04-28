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
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name", length = 100)
    private String appName;

    @Column(name = "app_package", length = 200)
    private String appPackage;

    @Column(name = "version_number", length = 50)
    private String versionNumber;

    @Column(name = "version_name", length = 50)
    private String versionName;

    @Column(name = "version_md5", unique = true, length = 100)
    private String versionMd5;

    @Column(name = "app_alias", length = 200)
    private String appAlias;

    @Column(name = "app_description", columnDefinition = "TEXT")
    private String appDescription;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "uploaded_by")
    private Long uploadedBy;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
