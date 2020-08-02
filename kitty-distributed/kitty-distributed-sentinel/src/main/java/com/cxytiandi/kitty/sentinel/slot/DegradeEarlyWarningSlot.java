package com.cxytiandi.kitty.sentinel.slot;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.cxytiandi.kitty.sentinel.alarm.SentinelBlockQueue;
import com.cxytiandi.kitty.sentinel.properties.EarlyWarningProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-29 21:26
 */
@Slf4j
public class DegradeEarlyWarningSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    /**
     * 默认提前告警比例
     */
    private double defaultProportion = 0.8;

    private EarlyWarningProperties earlyWarningProperties;

    public DegradeEarlyWarningSlot() {

    }

    public DegradeEarlyWarningSlot(EarlyWarningProperties earlyWarningProperties) {
        this.earlyWarningProperties = earlyWarningProperties;
    }

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, boolean prioritized, Object... args)
            throws Throwable {
        String resource = context.getCurEntry().getResourceWrapper().getName();
        List<DegradeRule> rules = getRuleProvider(resource);
        if (rules != null) {
            for (DegradeRule rule : rules) {
                if (!rule.passCheck(context, node, count)) {
                    DegradeRule originRule = getOriginRule(resource);
                    String originRuleCount = originRule == null ? "未知" : String.valueOf(originRule.getCount());
                    String warnMsg = String.format("DegradeEarlyWarning:资源【%s】目前的熔断指标已经超过【%s】，接近配置的熔断阈值:【%s】, 请对应负责人及时关注。", resource, rule.getCount(), originRuleCount);
                    log.warn(warnMsg);
                    SentinelBlockQueue.add(warnMsg);
                    break;
                }
            }
        }

        fireEntry(context, resourceWrapper, node, count, prioritized, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }



    private List<DegradeRule> getRuleProvider(String resource) {
        // Flow rule map should not be null.
        List<DegradeRule> rules = DegradeRuleManager.getRules();
        List<DegradeRule> earlyWarningRuleList = new ArrayList<>();
        for (DegradeRule rule : rules) {
            DegradeRule earlyWarningRule = new DegradeRule();
            BeanUtils.copyProperties(rule, earlyWarningRule);
            earlyWarningRule.setCount(rule.getCount() * defaultProportion);
            if (earlyWarningProperties.getProportionMap() != null && earlyWarningProperties.getProportionMap().containsKey(resource)) {
                earlyWarningRule.setCount(earlyWarningProperties.getProportionMap().get(resource));
            }
            earlyWarningRule.getPassCount().set(rule.getPassCount().longValue());
            earlyWarningRuleList.add(earlyWarningRule);
        }
        return earlyWarningRuleList.stream().filter(rule -> resource.equals(rule.getResource())).collect(Collectors.toList());
    }

    /**
     * get origin rule
     *
     * @param resource
     * @return
     */
    private DegradeRule getOriginRule(String resource) {
        List<DegradeRule> originRule = DegradeRuleManager.getRules().stream().filter(rule -> rule.getResource().equals(resource)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(originRule)) {
            return null;
        }
        return originRule.get(0);
    }
}
