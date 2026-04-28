package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.ApplicationUploadAddRequest;
import com.wiseasy.openapi.response.ApplicationUploadAddResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


public class ApplicationUploadAddTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * upload and add application
     */
    @Test
    public void applicationUploadAddTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        ApplicationUploadAddRequest appUploadAddRequest = new ApplicationUploadAddRequest();
        appUploadAddRequest.setVersion("v1.0");
        buildRequestParams(appUploadAddRequest);
        ApplicationUploadAddResponse appUploadAddResponse = null;
        try {
            appUploadAddResponse = openApiClient.execute(appUploadAddRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(appUploadAddResponse));

    }

    private void buildRequestParams(ApplicationUploadAddRequest applicationUploadAddRequest) {
        byte[] fileData = new byte[0];
        try {
            FileInputStream in = new FileInputStream("C:\\Users\\wangpos\\Desktop\\test\\apkTest\\apkTest1.3.apk");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            fileData = out.toByteArray();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        appUploadRequest.putByteArrayFile("Settings.apk", fileData);
        applicationUploadAddRequest.putFile("Settings.apk", new File("C:\\Users\\wangpos\\Desktop\\test\\apkTest\\apkTest1.3.apk"));
    }

}
