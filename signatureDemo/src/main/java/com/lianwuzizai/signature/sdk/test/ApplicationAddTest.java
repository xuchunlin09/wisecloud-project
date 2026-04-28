package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.ApplicationAddRequest;
import com.wiseasy.openapi.response.ApplicationAddResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApplicationAddTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * add application
     */
    @Test
    public void applicationAddTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        ApplicationAddRequest addAppRequest = new ApplicationAddRequest();
        addAppRequest.setVersion("v1.0");
        buildRequestParams(addAppRequest);
        ApplicationAddResponse addAppResponse = null;
        try {
            addAppResponse = openApiClient.execute(addAppRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(addAppResponse));

    }

    private void buildRequestParams(ApplicationAddRequest addAppRequest) {
        addAppRequest.setPackageName("com.seven.wiseasycloud");
        addAppRequest.setAppSign("30819f300d06092a864886f70d010101050003818d003081890281810092b2b582024f8b1e4873459551cafc0b68a9e6e30c3869595165da7515120605accfcd9a97c15aa34c63acf8d6178936b0f09a89522cf24b26b79c6ce9eca4a58eee58f18fe3b361860c73f4baacc674c4062366b0a322c9c374865d87486ce68f474a55c8848277a94127c309b45c32064313fcf73da6eade430aa23cde16a70203010001");
        addAppRequest.setAppName("apk test");
        addAppRequest.setVersionName("1.7");
        addAppRequest.setVersionNumber(17L);
        addAppRequest.setAppSize(1807029);
        addAppRequest.setAppMD5("e51cbe12a4ea70b60d389a234ac17c86");
        addAppRequest.setAppType(2);
        addAppRequest.setAppIconUrl("");
        addAppRequest.setAppUrl("");
        addAppRequest.setAppAlias("apkTest2");
    }


}
