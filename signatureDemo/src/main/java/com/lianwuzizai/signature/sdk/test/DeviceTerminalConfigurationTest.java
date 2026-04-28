package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.DeviceTerminalConfigurationRequest;
import com.wiseasy.openapi.response.DeviceTerminalConfigurationResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeviceTerminalConfigurationTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The latest configuration of the device in the system configuration
     */
    @Test
    public void deviceOnlineRecordTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        DeviceTerminalConfigurationRequest params = new DeviceTerminalConfigurationRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        DeviceTerminalConfigurationResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(result));
    }

    private void buildRequestParams(DeviceTerminalConfigurationRequest params) {
        params.setSn("PP352720C1003990");
    }

}
