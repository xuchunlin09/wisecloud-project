package com.wisecloud.mdm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSummary {
    private String sn;
    private String deviceName;
    private String deviceType;
    private Integer status;
}
