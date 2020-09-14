package com.cxytiandi.kitty.rocketmq.task;

import java.util.Date;
import java.util.List;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.org.apache.commons.lang3.StringUtils;
import com.cxytiandi.kitty.common.cat.CatTransactionManager;
import com.cxytiandi.kitty.lock.DistributedLock;
import com.cxytiandi.kitty.rocketmq.*;
import com.cxytiandi.kitty.rocketmq.constant.RocketMqConstant;
import com.cxytiandi.kitty.rocketmq.enums.RocketMqMessageTypeEnum;
import com.cxytiandi.kitty.rocketmq.service.TransactionMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消息发送简单处理，加锁
 * 可以单独出去，用专门的任务调度框架调度
 */
@Slf4j
public class ProcessMessageTask {

    @Autowired
	private TransactionMqService transactionMqService;

    @Autowired
	private RocketMqProducer rocketMqProducer;

	private DistributedLock distributedLock;

	public ProcessMessageTask(DistributedLock distributedLock) {
		this.distributedLock = distributedLock;
	}

	public void start() {
		Thread th = new Thread(new Runnable() {
			
			public void run() {
				while(true) {
					processByLock();
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
		List<TransactionMessage> messages = transactionMqService.listWatingSendMessage(100);
		CatTransactionManager.newTransaction(() -> {
			doProcess(messages);
		}, RocketMqConstant.MQ_CAT_TYPE, RocketMqConstant.DISPATCH);
	}

	private void doProcess(List<TransactionMessage> messages) {
		messages.parallelStream().forEach(message -> {
			SendResult sendResult = null;
			try {
				sendResult = sendMessage(message);
			} catch (Exception e) {
				log.error("消息发送失败: {}", message.getId(), e);
			} finally {
				if (sendResult != null && StringUtils.isNotBlank(sendResult.getMessageId())) {
					message.setMessageId(sendResult.getMessageId());
					message.setStatus(1);
				}
				message.setSendTime(new Date());
				message.setSendCount(message.getSendCount() + 1);
				transactionMqService.updateMessage(message);
			}
		});
	}
	
	private SendResult sendMessage(TransactionMessage message) {
		SendResult sendResult = null;
		Message msg = JSONObject.parseObject(message.getMessage(), Message.class);
		if (RocketMqMessageTypeEnum.NORMAL.getType().equals(message.getMessageType())) {
			sendResult = rocketMqProducer.sendMessage(msg, false);
		}

		if (RocketMqMessageTypeEnum.ORDER.getType().equals(message.getMessageType())) {
			sendResult = rocketMqProducer.sendOrderMessage(msg, msg.getShardingKey(),false);
		}

		if (RocketMqMessageTypeEnum.DELAY.getType().equals(message.getMessageType())) {
			sendResult = rocketMqProducer.sendMessage(msg, false);
		}

		return sendResult;
	}
}
