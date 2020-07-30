package com.cxytiandi.kitty.sentinel.slot;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleChecker;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleUtil;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.cxytiandi.kitty.sentinel.properties.EarlyWarningProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-29 21:11
 */
@Slf4j
public class FlowEarlyWarningSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    /**
     * 默认提前告警比例
     */
    private double defaultProportion = 0.8;

    private final FlowRuleChecker checker;

    private EarlyWarningProperties earlyWarningProperties;

    public FlowEarlyWarningSlot() {
        this(new FlowRuleChecker());
    }

    public FlowEarlyWarningSlot(EarlyWarningProperties earlyWarningProperties) {
        this(new FlowRuleChecker());
        this.earlyWarningProperties = earlyWarningProperties;
    }

    /**
     * Package-private for test.
     *
     * @param checker flow rule checker
     * @since 1.6.1
     */
    FlowEarlyWarningSlot(FlowRuleChecker checker) {
        AssertUtil.notNull(checker, "flow checker should not be null");
        this.checker = checker;
    }

    private List<FlowRule> getRuleProvider(String resource) {
        List<FlowRule> rules = FlowRuleManager.getRules();
        List<FlowRule> earlyWarningRuleList = new ArrayList<>();
        for (FlowRule rule : rules) {
            FlowRule earlyWarningRule = new FlowRule();
            BeanUtils.copyProperties(rule, earlyWarningRule);
            earlyWarningRule.setCount(rule.getCount() * defaultProportion);
            if (earlyWarningProperties.getProportionMap()!= null && earlyWarningProperties.getProportionMap().containsKey(resource)) {
                earlyWarningRule.setCount(earlyWarningProperties.getProportionMap().get(resource));
            }
            earlyWarningRuleList.add(earlyWarningRule);
        }
        Map<String, List<FlowRule>> flowRules = FlowRuleUtil.buildFlowRuleMap(earlyWarningRuleList);
        return flowRules.get(resource);
    }

    /**
     * get origin rule
     *
     * @param resource
     * @return
     */
    private FlowRule getOriginRule(String resource) {
        List<FlowRule> originRule = FlowRuleManager.getRules().stream().filter(flowRule -> flowRule.getResource().equals(resource)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(originRule)) {
            return null;
        }
        return originRule.get(0);
    }

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, boolean prioritized, Object... args)
            throws Throwable {
        String resource = context.getCurEntry().getResourceWrapper().getName();
        List<FlowRule> rules = getRuleProvider(resource);
        if (rules != null) {
            for (FlowRule rule : rules) {
                //这里取到的规则都是配置阈值的80%,这里如果检查到阈值了，说明就是到了真实阈值的80%，既可以发报警给对应负责人了
                if (!checker.canPassCheck(rule, context, node, count, prioritized)) {
                    FlowRule originRule = getOriginRule(resource);
                    String originRuleCount = originRule == null ? "未知" : String.valueOf(originRule.getCount());
                    log.info("FlowEarlyWarning:服务{}目前的流量指标已经超过{}，接近配置的流控阈值:{}, 请对应负责人及时关注。", resource, rule.getCount(), originRuleCount);
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
}
