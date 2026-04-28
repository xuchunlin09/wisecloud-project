package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.DeviceOnlineRecordRequest;
import com.wiseasy.openapi.response.DeviceOnlineRecordResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeviceOnlineRecordTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Record the device online and offline
     */
    @Test
    public void deviceOnlineRecordTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        DeviceOnlineRecordRequest params = new DeviceOnlineRecordRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        DeviceOnlineRecordResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(result));
    }

    private void buildRequestParams(DeviceOnlineRecordRequest params) {
        params.setSn("PP352720C1003990");
        params.setOperationTime(1);
    }

}
