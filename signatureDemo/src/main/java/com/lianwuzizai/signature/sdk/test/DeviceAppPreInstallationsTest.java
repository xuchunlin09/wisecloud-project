package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.AppPreStatusEnum;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.DeviceAppPreInstallationsRequest;
import com.wiseasy.openapi.response.DeviceAppPreInstallationsResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeviceAppPreInstallationsTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * device pre-applications
     */
    @Test
    public void deviceInstallApplicationsTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        DeviceAppPreInstallationsRequest params = new DeviceAppPreInstallationsRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        DeviceAppPreInstallationsResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(result));
    }

    private void buildRequestParams(DeviceAppPreInstallationsRequest params) {
        params.setSn("WNET5W2004000029");
        params.setPreStatus(AppPreStatusEnum.INSTALLED_STATUS);
    }

}
