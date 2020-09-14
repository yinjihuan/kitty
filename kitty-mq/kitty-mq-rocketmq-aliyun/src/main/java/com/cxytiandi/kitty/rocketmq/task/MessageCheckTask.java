package com.cxytiandi.kitty.rocketmq.task;

import com.aliyun.openservices.shade.org.apache.commons.lang3.StringUtils;
import com.aliyuncs.ons.model.v20190214.OnsTraceGetResultResponse;
import com.cxytiandi.kitty.common.alarm.AlarmManager;
import com.cxytiandi.kitty.common.alarm.AlarmMessage;
import com.cxytiandi.kitty.common.alarm.AlarmTypeEnum;
import com.cxytiandi.kitty.common.json.JsonUtils;
import com.cxytiandi.kitty.lock.DistributedLock;
import com.cxytiandi.kitty.rocketmq.service.OnsTraceService;
import com.cxytiandi.kitty.rocketmq.service.TransactionMqService;
import com.cxytiandi.kitty.rocketmq.TransactionMessage;
import com.cxytiandi.kitty.rocketmq.properties.MessageWarningProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class MessageCheckTask {

    private final String WORKING = "working";
    private final String FINISH = "finish";

    /**
     * 消费失败告警次数阀值
     */
    private int consumeFailAlarmCount = 5;

    /**
     * 应用名称，告警用到
     */
    @Value("${spring.application.name:unknown}")
    private String applicationName;

    private DistributedLock distributedLock;

    @Autowired
    private TransactionMqService transactionMQService;

    @Autowired
    private OnsTraceService onsTraceService;

    @Autowired
    private MessageWarningProperties messageWarningProperties;

    public MessageCheckTask(DistributedLock distributedLock) {
        this.distributedLock = distributedLock;
    }

    public void start() {
        Thread th = new Thread(new Runnable() {

            public void run() {
                while(true) {
                    processByLock();
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();
    }

    private void processByLock() {
        distributedLock.lock("transaction_message", 1000 * 60, () -> {
            process();
            return null;
        }, () -> {
            log.warn("获取锁失败");
            return null;
        });
    }

    private void process() {
        List<TransactionMessage> transactionMessages = transactionMQService.listWatingConsumeMessage(100);
        transactionMessages.forEach(message -> {
            doProcess(message, "");
        });
    }

    private void doProcess(TransactionMessage message, String queryId) {
        if (StringUtils.isBlank(queryId)) {
            queryId = onsTraceService.queryOnsTraceByMsgId(message.getMessageId(), message.getTopic(), message.getAddTime());
            if (StringUtils.isBlank(queryId)) {
                return;
            }
        }

        try {
            // 等待轨迹信息查询完成
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }

        OnsTraceGetResultResponse onsTraceResult = onsTraceService.getOnsTraceResult(queryId);
        if (onsTraceResult == null) {
            return;
        }

        String status = onsTraceResult.getTraceData().getStatus();
        if (WORKING.equals(status)) {
            doProcess(message, queryId);
        }

        if (FINISH.equals(status)) {
            confirmMessageConsumed(onsTraceResult, message);
        }
    }

    private void confirmMessageConsumed(OnsTraceGetResultResponse onsTraceResult, TransactionMessage message) {
        List<OnsTraceGetResultResponse.TraceData.TraceMapDo> traceList = onsTraceResult.getTraceData().getTraceList();
        if (CollectionUtils.isEmpty(traceList)) {
            return;
        }

        List<OnsTraceGetResultResponse.TraceData.TraceMapDo.SubMapDo> subList = traceList.get(0).getSubList();
        if (CollectionUtils.isEmpty(subList)) {
            return;
        }

        // 有成功次数则证明消费成功
        Integer successCount = subList.get(0).getSuccessCount();

        if (successCount > 0) {
            message.setStatus(TransactionMessage.CONSOMED);
            message.setTraces(JsonUtils.toJson(onsTraceResult));
            transactionMQService.updateMessage(message);
        }

        Integer failCount = subList.get(0).getFailCount();
        if (failCount > 0) {
            message.setConsumeFailCount(failCount);
            message.setTraces(JsonUtils.toJson(onsTraceResult));
            transactionMQService.updateMessage(message);
        }

        if (failCount > consumeFailAlarmCount) {
            sendAlarmMessage(String.format("[事务消息消费检查告警] : 消费失败次数 %s, 消息ID %s", failCount, message.getId()));
        }
    }

    private void sendAlarmMessage(String msg) {
        // 没有配置告警信息
        if (StringUtils.isBlank(messageWarningProperties.getAlarmApiUrl()) && StringUtils.isBlank(messageWarningProperties.getAccessToken())) {
            return;
        }

        AlarmMessage alarmMessage = AlarmMessage.builder()
                .alarmName("AliCloudRocketMqMessageAlarm")
                .alarmType(getAlarmType())
                .apiUrl(messageWarningProperties.getAlarmApiUrl())
                .message(getAlarmMessage(msg, messageWarningProperties))
                .accessToken(messageWarningProperties.getAccessToken())
                .secret(messageWarningProperties.getSecret())
                .alarmTimeInterval(messageWarningProperties.getAlarmTimeInterval())
                .build();

        AlarmManager.sendAlarmMessage(alarmMessage);

    }


    private String getAlarmMessage(String reason, MessageWarningProperties prop) {
        StringBuilder content = new StringBuilder();
        content.append("告警应用:").append(applicationName).append("\n");
        content.append("告警原因:").append(reason).append("\n");
        content.append("业务负责人:").append(prop.getOwner()).append("\n");
        content.append("告警间隔:").append(prop.getAlarmTimeInterval()).append("分钟\n");
        return content.toString();
    }


    private AlarmTypeEnum getAlarmType() {
        return org.springframework.util.StringUtils.hasText(messageWarningProperties.getAlarmApiUrl()) ? AlarmTypeEnum.EXTERNAL_SYSTEM : AlarmTypeEnum.DING_TALK;
    }

}
