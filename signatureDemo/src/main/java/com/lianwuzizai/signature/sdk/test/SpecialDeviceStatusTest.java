package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.SpecialDeviceStatusRequest;
import com.wiseasy.openapi.response.SpecialDeviceStatusResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpecialDeviceStatusTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *
     */
    @Test
    public void specialDeviceStatusTest(){
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        SpecialDeviceStatusRequest params = new SpecialDeviceStatusRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        logger.info("=========>"+JSONUtil.toJSONString(params));
        SpecialDeviceStatusResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result："+ JSONUtil.toJSONString(result));

    }

    private void buildRequestParams(SpecialDeviceStatusRequest specialDeviceStatusRequest) {
        specialDeviceStatusRequest.setQueryTime("2023-06-13");
    }
}
