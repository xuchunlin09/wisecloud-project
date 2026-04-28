package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.constant.UploadTypeEnum;
import com.wiseasy.openapi.request.TerminalResourceUploadRequest;
import com.wiseasy.openapi.response.TerminalResourceUploadResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


public class TerminalResourceUploadTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Resource upload (boot animation, boot screen, wallpaper, screensaver)
     *
     */
    @Test
    public void terminalResourceUploadTest(){
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        TerminalResourceUploadRequest params = new TerminalResourceUploadRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        TerminalResourceUploadResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result："+ JSONUtil.toJSONString(result));
    }

    private void buildRequestParams(TerminalResourceUploadRequest params) {
        params.setUploadType(UploadTypeEnum.PREVIEW);
        byte[] fileData = new byte[0];
        try {
            FileInputStream in = new FileInputStream("E:\\TEST-REPO\\picture\\2.png");
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
        params.putFile("test.png",new File("E:\\TEST-REPO\\picture\\2.png"));
//        params.putByteArrayFile("test.png",fileData);
    }

}
