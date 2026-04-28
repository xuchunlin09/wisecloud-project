package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.constant.TimeZoneEnum;
import com.wiseasy.openapi.request.WiseOsSettingPushRequest;
import com.wiseasy.openapi.response.WiseOsSettingPushResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


public class WiseOsSettingPushTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Change device time zones in batches
     */
    @Test
    public void wiseOsSettingPushTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        WiseOsSettingPushRequest params = new WiseOsSettingPushRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        logger.info("=========>" + JSONUtil.toJSONString(params));
        WiseOsSettingPushResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(result));

    }

    private void buildRequestParams(WiseOsSettingPushRequest wiseOsSettingPushRequest) {
        Set<String> set = new HashSet<>();
        set.add("12qwreqw231412");
        set.add("265432123");
        wiseOsSettingPushRequest.setSnList(set);
        WiseOsSettingPushRequest.Params params = new WiseOsSettingPushRequest.Params();
        WiseOsSettingPushRequest.Params.SystemSettings systemSettings = new WiseOsSettingPushRequest.Params.SystemSettings();
        systemSettings.setTimeZone(TimeZoneEnum.Athens);
        params.setSystemSettings(systemSettings);
        wiseOsSettingPushRequest.setParams(params);
    }
}
