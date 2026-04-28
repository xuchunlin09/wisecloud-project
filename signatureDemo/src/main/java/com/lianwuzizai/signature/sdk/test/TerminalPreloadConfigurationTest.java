package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.constant.PreloadConfigurationFlagEnum;
import com.wiseasy.openapi.request.TerminalPreloadConfigurationRequest;
import com.wiseasy.openapi.response.TerminalPreloadConfigurationResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TerminalPreloadConfigurationTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Pre-installed application configuration (added batch removal)
     *
     */
    @Test
    public void terminalPreloadConfigurationTest(){
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        TerminalPreloadConfigurationRequest params = new TerminalPreloadConfigurationRequest();
        params.setVersion("v1.0");
        buildRequestParams(params);
        TerminalPreloadConfigurationResponse result = null;
        try {
            result = openApiClient.execute(params);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result："+ JSONUtil.toJSONString(result));
    }

    private void buildRequestParams(TerminalPreloadConfigurationRequest params) {
        params.setSnList(Arrays.asList("PP35542108000157","P327701702000683"));
        params.setFlag(PreloadConfigurationFlagEnum.ADD_FLAG);
        List<TerminalPreloadConfigurationRequest.AppVersion> list = new ArrayList<>();
        TerminalPreloadConfigurationRequest.AppVersion appVersion = new TerminalPreloadConfigurationRequest.AppVersion();
        appVersion.setVersionMD5("65f52b3d200c41458858f6c721fed7d1");
        appVersion.setVersionNumber(1467L);
        list.add(appVersion);
        params.setAppList(list);
    }
}
