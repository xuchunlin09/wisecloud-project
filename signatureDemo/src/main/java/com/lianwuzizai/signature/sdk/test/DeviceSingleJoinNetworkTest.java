package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.DeviceSingleJoinNetworkRequest;
import com.wiseasy.openapi.response.DeviceSingleJoinNetworkResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeviceSingleJoinNetworkTest {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * A single device is connected to the network
     */
    @Test
    public void deviceSingleJoinNetworkTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        DeviceSingleJoinNetworkRequest params = new DeviceSingleJoinNetworkRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        DeviceSingleJoinNetworkResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(result));
    }

    private void buildRequestParams(DeviceSingleJoinNetworkRequest params) {
        params.setSn("WNET5W20022111112");
    }

}
