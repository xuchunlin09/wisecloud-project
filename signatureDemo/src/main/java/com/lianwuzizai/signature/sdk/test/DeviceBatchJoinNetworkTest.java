package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.DeviceBatchJoinNetworkRequest;
import com.wiseasy.openapi.response.DeviceBatchJoinNetworkResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


public class DeviceBatchJoinNetworkTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Devices are connected to the network in batches
     */
    @Test
    public void deviceBatchJoinNetworkTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        DeviceBatchJoinNetworkRequest params = new DeviceBatchJoinNetworkRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        DeviceBatchJoinNetworkResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(result));
    }

    private void buildRequestParams(DeviceBatchJoinNetworkRequest params) {
        params.setSnList(Arrays.asList("WNET5W20040111112", "WNET5W200402222222"));
    }

}
