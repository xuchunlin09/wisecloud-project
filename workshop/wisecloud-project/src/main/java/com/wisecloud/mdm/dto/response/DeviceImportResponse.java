package com.wisecloud.mdm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportResponse {
    private int successCount;
    private int failCount;
    private List<String> successList;
    private List<SnFailInfo> failList;
}
