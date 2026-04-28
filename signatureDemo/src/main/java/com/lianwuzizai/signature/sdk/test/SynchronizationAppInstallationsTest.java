package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.SynchronizationAppInstallationsRequest;
import com.wiseasy.openapi.response.SynchronizationAppInstallationsResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SynchronizationAppInstallationsTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * query partners' applications Install devices
     */
    @Test
    public void synchronizationAppInstallationsTest(){
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        SynchronizationAppInstallationsRequest appInstallSyncRequest = new SynchronizationAppInstallationsRequest();
        appInstallSyncRequest.setVersion("v1.0");
        buildRequestParams(appInstallSyncRequest);
        SynchronizationAppInstallationsResponse appInstallSyncResponse = null;
        try {
            appInstallSyncResponse = openApiClient.execute(appInstallSyncRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(appInstallSyncResponse));

    }

    private void buildRequestParams(SynchronizationAppInstallationsRequest appInstallSyncRequest) {

    }

}
