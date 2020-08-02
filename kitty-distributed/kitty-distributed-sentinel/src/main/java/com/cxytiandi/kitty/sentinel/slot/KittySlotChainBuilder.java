package com.cxytiandi.kitty.sentinel.slot;

import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.SlotChainBuilder;
import com.alibaba.csp.sentinel.slots.DefaultSlotChainBuilder;
import com.cxytiandi.kitty.sentinel.ApplicationContextHelper;
import com.cxytiandi.kitty.sentinel.properties.EarlyWarningProperties;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-07-29 21:05
 */
public class KittySlotChainBuilder implements SlotChainBuilder {

    @Override
    public ProcessorSlotChain build() {
        EarlyWarningProperties warningProperties = ApplicationContextHelper.getBean(EarlyWarningProperties.class);
        ProcessorSlotChain chain = new DefaultSlotChainBuilder().build();
        chain.addLast(new FlowEarlyWarningSlot(warningProperties));
        chain.addLast(new DegradeEarlyWarningSlot(warningProperties));
        return chain;
    }

}
