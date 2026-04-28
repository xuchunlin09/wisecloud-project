package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.constant.UploadTypeEnum;
import com.wiseasy.openapi.request.TerminalSettingAddRequest;
import com.wiseasy.openapi.response.TerminalSettingAddResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


public class TerminalSettingAddTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Save terminal system Settings
     *
     */
    @Test
    public void terminalSettingAddTest(){
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        TerminalSettingAddRequest params = new TerminalSettingAddRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        TerminalSettingAddResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result："+ JSONUtil.toJSONString(result));
    }

    private void buildRequestParams(TerminalSettingAddRequest params) {
        params.setUploadType(UploadTypeEnum.WALLPAPER);
        params.setDownloadPath("https://wisecloud3-oss.wiseasy.com/service/dms/wiseos/preview/78044e7384334831b6e946fa2bf49460.png");
        params.setSnList(Arrays.asList("PP35542108000157","P327701702000683"));
        params.setMd5("e96dbe96d81629ae2c1cb07262f375ea");
        params.setDisplayResolution("720*1280");
        params.setFilesize(1340574);
    }
}
