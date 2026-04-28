package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.InstructionTaskDetailsRequest;
import com.wiseasy.openapi.response.InstructionTaskDetailsResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InstructionTaskDetailsTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Command task result query
     */
    @Test
    public void instructionTaskDetailsTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        InstructionTaskDetailsRequest instructionTaskDetailsRequest = new InstructionTaskDetailsRequest();
        instructionTaskDetailsRequest.setVersion("v1.0");
        buildRequestParams(instructionTaskDetailsRequest);
        InstructionTaskDetailsResponse instructionTaskDetailsResponse = null;
        try {
            instructionTaskDetailsResponse = openApiClient.execute(instructionTaskDetailsRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(instructionTaskDetailsResponse));

    }

    private void buildRequestParams(InstructionTaskDetailsRequest instructionTaskDetailsRequest) {
        instructionTaskDetailsRequest.setTraceId("61f2350864f16a0001a426e4");
    }

}
