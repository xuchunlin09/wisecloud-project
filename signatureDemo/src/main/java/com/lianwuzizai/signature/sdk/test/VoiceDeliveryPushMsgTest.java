package com.lianwuzizai.signature.sdk.test;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import com.wiseasy.openapi.client.OpenApiClientException;
import com.wiseasy.openapi.constant.*;
import com.wiseasy.openapi.request.VoiceDeliveryPushMsgRequest;
import com.wiseasy.openapi.response.VoiceDeliveryPushMsgResponse;
import com.wiseasy.openapi.utils.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class VoiceDeliveryPushMsgTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * The message cloud sends messages
     */
    @Test
    public void deliveryPushMsgTest() {
        Config config = new Config("your accessKeyId", "your accessKeySecret")
                .setEndpointUrl(EndpointUrlEnum.LONDON); //the default endpoint is London if not set
        OpenApiClient openApiClient = new DefaultOpenApiClient(config);
        VoiceDeliveryPushMsgRequest pushMsgRequest = new VoiceDeliveryPushMsgRequest();
        pushMsgRequest.setVersion("v1.0");
        buildRequestParams(pushMsgRequest);
        VoiceDeliveryPushMsgResponse pushMsgResponse = null;
        try {
            pushMsgResponse = openApiClient.execute(pushMsgRequest);
        } catch (OpenApiClientException e) {
            e.printStackTrace();
            logger.error("error:" + e.getCode() + "->>" + e.getMsg());
        }
        logger.info(JSONUtil.toJSONString(pushMsgResponse));
    }

    private void buildRequestParams(VoiceDeliveryPushMsgRequest pushMsgRequest) {
        pushMsgRequest.setBroadcastNumber("3.15");
        pushMsgRequest.setContent("3.15");
        pushMsgRequest.setSn("WNET5W1805000066");
        pushMsgRequest.setCommandKey(VoiceCommandKeyEnum.voice);
        pushMsgRequest.setVoiceType(VoiceTypeEnum.localZipper);
        pushMsgRequest.setSubmitType(1);
        VoiceDeliveryPushMsgRequest.VoiceZipperInfo voiceZipperInfo = new VoiceDeliveryPushMsgRequest.VoiceZipperInfo();
//        voiceZipperInfo.setCode();
        voiceZipperInfo.setVoiceText("3.15");
        voiceZipperInfo.setLang(VoiceLangEnum.English);
        voiceZipperInfo.setType(VoiceCorpusTypeEnum.Raw);
        voiceZipperInfo.setPackageCode("V00001");
        List<VoiceDeliveryPushMsgRequest.VoiceZipperInfo> list = new ArrayList<>();
        list.add(voiceZipperInfo);
        pushMsgRequest.setVoiceZipperInfo(list);
    }
}
