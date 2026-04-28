package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.BatchDevicesDetailRequest;
import com.wiseasy.openapi.response.BatchDevicesDetailResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


public class BatchDevicesDetailTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Batch Query Device Detail
     */
    @Test
    public void batchDevicesDetailTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        BatchDevicesDetailRequest params = new BatchDevicesDetailRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        BatchDevicesDetailResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(result));

    }

    private void buildRequestParams(BatchDevicesDetailRequest params) {
        params.setSnList(Arrays.asList("PP12345612345612", "PP35272108000480"));
    }

}
