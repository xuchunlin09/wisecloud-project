package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.EndpointUrlEnum;
import com.wiseasy.openapi.constant.ExportExcelTypeEnum;
import com.wiseasy.openapi.request.ExportTaskAddRequest;
import com.wiseasy.openapi.response.ExportTaskAddResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExportTaskAddTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Adding an Export Task
     */
    @Test
    public void exportTaskAddTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        ExportTaskAddRequest exportTaskAddRequest = new ExportTaskAddRequest();
        exportTaskAddRequest.setVersion("v1.0");
        buildRequestParams(exportTaskAddRequest);
        ExportTaskAddResponse exportTaskAddResponse = null;
        try {
            exportTaskAddResponse = openApiClient.execute(exportTaskAddRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info("result：" + JSONUtil.toJSONString(exportTaskAddResponse));

    }

    private void buildRequestParams(ExportTaskAddRequest exportTaskAddRequest) {
        exportTaskAddRequest.setTaskType(ExportExcelTypeEnum.APP_INSTALLED_DEVICE);
        ExportTaskAddRequest.Parameter parameter = new ExportTaskAddRequest.Parameter();
        parameter.setVersionMD5("2657633996c71ebc508d2ee177e78009");
        exportTaskAddRequest.setParam(parameter);
//        exportTaskAddRequest.setTaskName("导出任务"+System.currentTimeMillis());
    }

}
