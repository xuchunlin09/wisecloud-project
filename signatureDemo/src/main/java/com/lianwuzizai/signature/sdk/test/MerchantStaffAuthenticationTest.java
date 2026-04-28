package com.lianwuzizai.signature.sdk.test;


import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.Constants;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.MerchantStaffAuthenticationRequest;
import com.wiseasy.openapi.response.MerchantStaffAuthenticationResponse;
import com.wiseasy.openapi.response.MerchantStaffListResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;

public class MerchantStaffAuthenticationTest {

    /**
     * Employee identity authentication
     */
    @Test
    public void test() {
        //init client
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        MerchantStaffAuthenticationRequest request = new MerchantStaffAuthenticationRequest();
        request.setSignType(Constants.SIGN_TYPE_MD5);
        request.setPosFirstLogin(1);

        //pre
        request.setLoginAccount("0988303036");
        request.setSn("WPOSQT2070000091");

        MerchantStaffAuthenticationResponse response = null;
        try {
            response = openApiClient.execute(request);
        } catch (OpenApiClientException e) {
            System.out.println("error:" + e.getCode() + "->>" + e.getMsg());
            return;
        }

        if (response.isSuccess()) {
            if (response.getData() != null) {
                System.out.println(JSONUtil.toJSONString(response));
                System.out.println(response.getData().getStaffName());
            }
        } else {
            System.out.println("error:" + response.getCode() + "->>" + response.getMsg());
        }

    }


    public static void main(String[] args) {
        String jsonstr = "{\"msg\":\"success\",\"code\":0,\"data\":[{\"lastLoginTime\":\"2022-01-26T11:00:47.000+00:00\",\"loginAccount\":\"1232145677\",\"partnerCode\":\"0\",\"firstLogin\":1,\"createTime\":\"2021-12-30T02:07:41.000+00:00\",\"staffName\":\"test1\",\"updateTime\":\"2022-01-26T11:00:47.000+00:00\",\"id\":30,\"position\":0,\"merchantNo\":\"800000000549\",\"status\":1}],\"count\":1,\"timestamp\":1643264584848,\"signatureValue\":\"5e8661a4f252abede833cd9f20633685\"}";

//        jsonstr = "{\"msg\":\"success\",\"code\":0,\"data\":{\"firstLogin\":1,\"staffName\":\"test1\",\"position\":0},\"timestamp\":1643264986206,\"signatureValue\":\"9df2d52b39d43e7d36fd63ef3fa8f359\"}";
        Object o = JSONUtil.toJavaObject(jsonstr, MerchantStaffListResponse.class);
        System.out.println(o);
        System.out.println(JSONUtil.toJSONString(o));
    }
}
