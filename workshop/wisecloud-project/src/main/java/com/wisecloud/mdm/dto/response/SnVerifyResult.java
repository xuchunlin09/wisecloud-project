package com.wisecloud.mdm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Result of SN batch verification via WiseCloud verify/sn API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnVerifyResult {

    private List<String> sucList;
    private List<SnErrorItem> errList;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SnErrorItem {
        private String sn;
        private String errMsg;
    }
}
