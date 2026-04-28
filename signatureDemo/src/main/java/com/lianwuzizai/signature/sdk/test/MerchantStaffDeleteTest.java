package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.MerchantStaffDeleteRequest;
import com.wiseasy.openapi.response.MerchantStaffDeleteResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerchantStaffDeleteTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * delete employee of merchant
     *
     */
    @Test
    public void merchantStaffDeleteTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        MerchantStaffDeleteRequest deleteRequest = new MerchantStaffDeleteRequest();
        deleteRequest.setVersion("v1.0");
        deleteRequest.setSignType("hmacsha256");
        buildRequestParams(deleteRequest);
        MerchantStaffDeleteResponse addResponse = null;

        try {
            addResponse = openApiClient.execute(deleteRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(addResponse));
    }

    private void buildRequestParams(MerchantStaffDeleteRequest deleteRequest) {
        deleteRequest.setId(599);
        deleteRequest.setSn("WISEBOT000010");
    }
}
