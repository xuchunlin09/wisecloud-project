package com.lianwuzizai.signature.sdk.test;


import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.Constants;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.MerchantStaffListRequest;
import com.wiseasy.openapi.response.MerchantStaffListResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;

public class MerchantStaffListTest {

    /**
     * Query Employee List
     */
    @Test
    public void merchantStaffListTest() {
        //init client
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        MerchantStaffListRequest request = new MerchantStaffListRequest();
        request.setSignType(Constants.SIGN_TYPE_MD5);
        request.setCurrentPosition(0);
        request.setSn("WPOSQT2070000091");

        MerchantStaffListResponse response = null;
        try {
            response = openApiClient.execute(request);
        } catch (OpenApiClientException e) {
            System.out.println("error:" + e.getCode() + "->>" + e.getMsg());
            return;
        }

        if (response.isSuccess()) {
            if (response.getData() != null) {
                System.out.println(JSONUtil.toJSONString(response));
                System.out.println(response.getData().get(0).getStaffName());
            }
        } else {
            System.out.println("error:" + response.getCode() + "->>" + response.getMsg());
        }

    }

}
