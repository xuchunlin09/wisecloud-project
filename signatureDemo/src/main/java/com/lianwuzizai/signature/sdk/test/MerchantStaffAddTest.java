package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EmployeePositionEnum;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.MerchantStaffAddRequest;
import com.wiseasy.openapi.response.MerchantStaffAddResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerchantStaffAddTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Add merchant staff
     */
    @Test
    public void merchantStaffAddTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        MerchantStaffAddRequest addRequest = new MerchantStaffAddRequest();
        addRequest.setVersion("v1.0");
        addRequest.setVersion("HmacSHA256");
        buildRequestParams(addRequest);
        MerchantStaffAddResponse addResponse = null;
        try {
            addResponse = openApiClient.execute(addRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(addResponse));
    }

    private void buildRequestParams(MerchantStaffAddRequest addRequest) {
        addRequest.setSn("WISEBOT000010");
        addRequest.setMerchantNo("80000000000774");
        addRequest.setPosition(EmployeePositionEnum.manager);
        addRequest.setStaffName("test sha256 sign");
        addRequest.setLoginAccount("liqing@wiseasy.com");
        addRequest.setStoreNo("40000546");
        addRequest.setStoreName("test sha256 signature");
    }
}
