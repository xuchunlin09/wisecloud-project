package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EmployeePositionEnum;
import com.wiseasy.openapi.constant.EmployeeStatusEnum;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.MerchantStaffUpdateRequest;
import com.wiseasy.openapi.response.MerchantStaffUpdateResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerchantStaffUpdateTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Modify Employee Information
     */
    @Test
    public void merchantStaffUpdateTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        MerchantStaffUpdateRequest updateRequest = new MerchantStaffUpdateRequest();
        updateRequest.setVersion("v1.0");
        updateRequest.setSignType("hmacsha256");
        buildRequestParams(updateRequest);
        MerchantStaffUpdateResponse addResponse = null;
        try {
            addResponse = openApiClient.execute(updateRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(addResponse));
    }

    private void buildRequestParams(MerchantStaffUpdateRequest updateRequest) {
        updateRequest.setId(598);
        updateRequest.setSn("WISEBOT000010");
        updateRequest.setPosition(EmployeePositionEnum.manager);
        updateRequest.setStaffName("lq");
        updateRequest.setLoginAccount("xxx@wiseasy.com");
        updateRequest.setStoreNo("40000546");
        updateRequest.setStoreName("help foods");
        updateRequest.setStatus(EmployeeStatusEnum.manager);
    }
}
