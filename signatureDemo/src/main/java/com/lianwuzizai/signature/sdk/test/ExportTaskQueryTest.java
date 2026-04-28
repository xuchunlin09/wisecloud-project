package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.constant.ExportExcelTypeEnum;
import com.wiseasy.openapi.constant.ExportTaskEnum;
import com.wiseasy.openapi.request.ExportTaskQueryRequest;
import com.wiseasy.openapi.response.ExportTaskQueryResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExportTaskQueryTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Query the list of export tasks
     */
    @Test
    public void exportTaskQueryTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        ExportTaskQueryRequest exportTaskQueryRequest = new ExportTaskQueryRequest();
        exportTaskQueryRequest.setVersion("v1.0");
        buildRequestParams(exportTaskQueryRequest);
        ExportTaskQueryResponse exportTaskQueryResponse = null;
        try {
            exportTaskQueryResponse = openApiClient.execute(exportTaskQueryRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(exportTaskQueryResponse));

    }

    private void buildRequestParams(ExportTaskQueryRequest exportTaskQueryRequest) {
        exportTaskQueryRequest.setTaskType(ExportExcelTypeEnum.APP_INSTALLED_DEVICE);
        exportTaskQueryRequest.setTaskStatus(ExportTaskEnum.end);
        exportTaskQueryRequest.setTaskSepNo("621dcacac8ca2f0001267146");
    }

}
