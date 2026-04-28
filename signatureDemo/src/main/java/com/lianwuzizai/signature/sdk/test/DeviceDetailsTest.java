package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.DeviceDetailsRequest;
import com.wiseasy.openapi.response.DeviceDetailsResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeviceDetailsTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * device detail
     */
    @Test
    public void deviceDetailsTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        DeviceDetailsRequest params = new DeviceDetailsRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        DeviceDetailsResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(result));
    }

    private void buildRequestParams(DeviceDetailsRequest params) {
        params.setSn("PP35272121000050");
    }

}
