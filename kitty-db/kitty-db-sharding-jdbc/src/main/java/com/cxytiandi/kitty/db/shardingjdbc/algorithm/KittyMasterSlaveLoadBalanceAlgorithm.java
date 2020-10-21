package com.cxytiandi.kitty.db.shardingjdbc.algorithm;

import org.apache.shardingsphere.core.strategy.masterslave.RoundRobinMasterSlaveLoadBalanceAlgorithm;
import org.apache.shardingsphere.spi.masterslave.MasterSlaveLoadBalanceAlgorithm;

import java.util.List;
import java.util.Properties;

/**
 * 自定义读写分离算法
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-10-13 21:51
 */
public class KittyMasterSlaveLoadBalanceAlgorithm implements MasterSlaveLoadBalanceAlgorithm {

    private RoundRobinMasterSlaveLoadBalanceAlgorithm roundRobin = new RoundRobinMasterSlaveLoadBalanceAlgorithm();

    @Override
    public String getDataSource(String name, String masterDataSourceName, List<String> slaveDataSourceNames) {
        String dataSource = roundRobin.getDataSource(name, masterDataSourceName, slaveDataSourceNames);
        // 控制逻辑，比如不同的从节点（配置不同）可以有不同的比例

        return dataSource;
    }

    @Override
    public String getType() {
        return "KITTY_ROUND_ROBIN";
    }

    @Override
    public Properties getProperties() {
        return roundRobin.getProperties();
    }

    @Override
    public void setProperties(Properties properties) {
        roundRobin.setProperties(properties);
    }
}
