package com.cxytiandi.kitty.rocketmq.service;

import com.aliyun.openservices.shade.org.apache.commons.lang3.time.DateUtils;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.ons.model.v20190214.OnsTraceGetResultRequest;
import com.aliyuncs.ons.model.v20190214.OnsTraceGetResultResponse;
import com.aliyuncs.ons.model.v20190214.OnsTraceQueryByMsgIdRequest;
import com.aliyuncs.ons.model.v20190214.OnsTraceQueryByMsgIdResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.cxytiandi.kitty.rocketmq.properties.RocketMqProperties;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import java.util.Date;

@Slf4j
public class OnsTraceService {

    private RocketMqProperties rocketMqProperties;

    private IAcsClient client;

    public OnsTraceService(RocketMqProperties rocketMqProperties) {
        this.rocketMqProperties = rocketMqProperties;
    }

    @PostConstruct
    public void init() {
        DefaultProfile profile = DefaultProfile.getProfile(rocketMqProperties.getRegionId(), rocketMqProperties.getAccessKey(), rocketMqProperties.getSecretKey());
        client = new DefaultAcsClient(profile);
    }

    /**
     * 查询消息轨迹
     * @param messageId 消息ID
     * @param topic 主题
     * @param sendTime 消息发送时间
     * @return 轨迹ID
     */
    public String queryOnsTraceByMsgId(String messageId, String topic, Date sendTime) {
        OnsTraceQueryByMsgIdRequest request = new OnsTraceQueryByMsgIdRequest();
        try {
            request.setMsgId(messageId);
            request.setInstanceId(rocketMqProperties.getInstanceId());
            request.setTopic(topic);
            Date endTime = DateUtils.addDays(sendTime, 1);
            request.setBeginTime(sendTime.getTime());
            request.setEndTime(endTime.getTime());
            OnsTraceQueryByMsgIdResponse response = client.getAcsResponse(request);
            return response.getQueryId();
        } catch (ServerException e) {
            log.error("RocketMq Open Api服务端异常", e);
        } catch (ClientException e) {
            log.error("RocketMq Open Api客户端异常", e);
        }
        return null;
    }

    public OnsTraceGetResultResponse getOnsTraceResult(String queryId) {
        OnsTraceGetResultRequest request = new OnsTraceGetResultRequest();
        request.setRegionId(rocketMqProperties.getRegionId());
        request.setQueryId(queryId);

        try {
            OnsTraceGetResultResponse response = client.getAcsResponse(request);
            return response;
        } catch (ServerException e) {
            log.error("RocketMq Open Api服务端异常", e);
        } catch (ClientException e) {
            log.error("RocketMq Open Api客户端异常", e);
        }
        return null;
    }

}
