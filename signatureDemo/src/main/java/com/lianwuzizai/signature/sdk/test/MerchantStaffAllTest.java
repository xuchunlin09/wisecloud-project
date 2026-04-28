package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.MerchantStaffAllRequest;
import com.wiseasy.openapi.response.MerchantStaffAllResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerchantStaffAllTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Query All Employee Information
     */
    @Test
    public void merchantStaffAllTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        MerchantStaffAllRequest allRequest = new MerchantStaffAllRequest();
        allRequest.setVersion("v1.0");
        allRequest.setSignType("HmacSHA256");
        MerchantStaffAllResponse addResponse = null;

        try {
            addResponse = openApiClient.execute(allRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(addResponse));
    }

}
