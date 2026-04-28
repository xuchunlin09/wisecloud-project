package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.request.InstructionTaskQueryRequest;
import com.wiseasy.openapi.response.InstructionTaskQueryResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InstructionTaskQueryTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Command task list query
     */
    @Test
    public void instructionTaskQueryTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        InstructionTaskQueryRequest instructionTaskQueryRequest = new InstructionTaskQueryRequest();
        instructionTaskQueryRequest.setVersion("v1.0");
        buildRequestParams(instructionTaskQueryRequest);
        InstructionTaskQueryResponse instructionTaskQueryResponse = null;
        try {
            instructionTaskQueryResponse = openApiClient.execute(instructionTaskQueryRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(instructionTaskQueryResponse));

    }

    private void buildRequestParams(InstructionTaskQueryRequest instructionTaskQueryRequest) {
        instructionTaskQueryRequest.setTraceId("61f2350864f16a0001a426e4");
    }

}
