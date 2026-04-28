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
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String sn;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    private Integer status;

    @Column(name = "activation_time")
    private LocalDateTime activationTime;

    @Column(name = "merchant_no", length = 50)
    private String merchantNo;

    @Column(name = "merchant_name", length = 100)
    private String merchantName;

    @Column(name = "store_name", length = 100)
    private String storeName;

    @Column(name = "imported_at")
    private LocalDateTime importedAt;

    @Column(name = "imported_by")
    private Long importedBy;

    @PrePersist
    protected void onCreate() {
        importedAt = LocalDateTime.now();
    }
}
