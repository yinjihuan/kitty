package com.cxytiandi.kitty.db.redis.spring;

import org.springframework.data.redis.connection.ClusterInfo;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.RedisClientInfo;

import java.util.*;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-27 21:10
 */
public class CatMonitorRedisClusterConnection extends CatMonitorRedisConnection implements RedisClusterConnection {

    private final RedisClusterConnection connection;

    private CatMonitorHelper catMonitorHelper;

    public CatMonitorRedisClusterConnection(RedisClusterConnection connection) {
        super(connection);
        this.connection = connection;
        this.catMonitorHelper = new CatMonitorHelper();
    }


    @Override
    public String ping(RedisClusterNode node) {
        return catMonitorHelper.execute(RedisCommand.PING, () -> connection.ping(node));
    }

    @Override
    public Set<byte[]> keys(RedisClusterNode node, byte[] pattern) {
        return catMonitorHelper.execute(RedisCommand.KEYS, () -> connection.keys(node, pattern));
    }

    @Override
    public Cursor<byte[]> scan(RedisClusterNode node, ScanOptions options) {
        return catMonitorHelper.execute(RedisCommand.SCAN, () -> connection.scan(node, options));
    }

    @Override
    public byte[] randomKey(RedisClusterNode node) {
        return catMonitorHelper.execute(RedisCommand.RANDOMKEY, () -> connection.randomKey(node));
    }

    @Override
    public <T> T execute(String command, byte[] key, Collection<byte[]> args) {
        return catMonitorHelper.execute(RedisCommand.EXECUTE, key, () -> connection.execute(command, key, args));
    }

    @Override
    public Iterable<RedisClusterNode> clusterGetNodes() {
        return catMonitorHelper.execute(RedisCommand.CLUSTER_NODES, () -> connection.clusterGetNodes());
    }

    @Override
    public Collection<RedisClusterNode> clusterGetSlaves(RedisClusterNode node) {
        return catMonitorHelper.execute(RedisCommand.CLUSTER_SLAVES, () -> connection.clusterGetSlaves(node));
    }

    @Override
    public Map<RedisClusterNode, Collection<RedisClusterNode>> clusterGetMasterSlaveMap() {
        return catMonitorHelper.execute(RedisCommand.CLUSTER_MASTER_SLAVE_MAP, () -> connection.clusterGetMasterSlaveMap());
    }

    @Override
    public Integer clusterGetSlotForKey(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.CLUSTER_KEYSLOT, key, () -> connection.clusterGetSlotForKey(key));
    }

    @Override
    public RedisClusterNode clusterGetNodeForSlot(int slot) {
        return catMonitorHelper.execute(RedisCommand.CLUSTER_NODE_FOR_SLOT, () -> connection.clusterGetNodeForSlot(slot));
    }

    @Override
    public RedisClusterNode clusterGetNodeForKey(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.CLUSTER_NODE_FOR_KEY, key, () -> connection.clusterGetNodeForKey(key));
    }

    @Override
    public ClusterInfo clusterGetClusterInfo() {
        return catMonitorHelper.execute(RedisCommand.CLUSTER_INFO, () -> connection.clusterGetClusterInfo());
    }

    @Override
    public void clusterAddSlots(RedisClusterNode node, int... slots) {
        catMonitorHelper.execute(RedisCommand.CLUSTER_ADDSLOTS, () -> connection.clusterAddSlots(node, slots));
    }

    @Override
    public void clusterAddSlots(RedisClusterNode node, RedisClusterNode.SlotRange range) {
        catMonitorHelper.execute(RedisCommand.CLUSTER_ADDSLOTS, () -> connection.clusterAddSlots(node, range));
    }

    @Override
    public Long clusterCountKeysInSlot(int slot) {
        return catMonitorHelper.execute(RedisCommand.CLUSTER_COUNTKEYSINSLOT, () -> connection.clusterCountKeysInSlot(slot));
    }

    @Override
    public void clusterDeleteSlots(RedisClusterNode node, int... slots) {
        catMonitorHelper.execute(RedisCommand.CLUSTER_DELSLOTS, () -> connection.clusterDeleteSlots(node, slots));
    }

    @Override
    public void clusterDeleteSlotsInRange(RedisClusterNode node, RedisClusterNode.SlotRange range) {
        catMonitorHelper.execute(RedisCommand.CLUSTER_DELSLOTS, () -> connection.clusterDeleteSlotsInRange(node, range));
    }

    @Override
    public void clusterForget(RedisClusterNode node) {
        catMonitorHelper.execute(RedisCommand.CLUSTER_FORGET, () -> connection.clusterForget(node));
    }

    @Override
    public void clusterMeet(RedisClusterNode node) {
        catMonitorHelper.execute(RedisCommand.CLUSTER_MEET, () -> connection.clusterMeet(node));
    }

    @Override
    public void clusterSetSlot(RedisClusterNode node, int slot, AddSlots mode) {
        catMonitorHelper.execute(RedisCommand.CLUSTER_SETSLOT, () -> connection.clusterSetSlot(node, slot, mode));
    }

    @Override
    public List<byte[]> clusterGetKeysInSlot(int slot, Integer count) {
        return catMonitorHelper.execute(RedisCommand.CLUSTER_GETKEYSINSLOT, () -> connection.clusterGetKeysInSlot(slot, count));
    }

    @Override
    public void clusterReplicate(RedisClusterNode master, RedisClusterNode slave) {
        catMonitorHelper.execute(RedisCommand.CLUSTER_REPLICATE, () -> connection.clusterReplicate(master, slave));
    }

    @Override
    public void bgReWriteAof(RedisClusterNode node) {
        catMonitorHelper.execute(RedisCommand.BGREWRITEAOF, () -> connection.bgReWriteAof(node));
    }

    @Override
    public void bgSave(RedisClusterNode node) {
        catMonitorHelper.execute(RedisCommand.BGSAVE, () -> connection.bgSave(node));
    }

    @Override
    public Long lastSave(RedisClusterNode node) {
        return catMonitorHelper.execute(RedisCommand.LASTSAVE, () -> connection.lastSave(node));
    }

    @Override
    public void save(RedisClusterNode node) {
        catMonitorHelper.execute(RedisCommand.SAVE, () -> connection.save(node));
    }

    @Override
    public Long dbSize(RedisClusterNode node) {
        return catMonitorHelper.execute(RedisCommand.DBSIZE, () -> connection.dbSize(node));
    }

    @Override
    public void flushDb(RedisClusterNode node) {
        catMonitorHelper.execute(RedisCommand.FLUSHDB, () -> connection.flushDb(node));
    }

    @Override
    public void flushAll(RedisClusterNode node) {
        catMonitorHelper.execute(RedisCommand.FLUSHALL, () -> connection.flushAll(node));
    }

    @Override
    public Properties info(RedisClusterNode node) {
        return catMonitorHelper.execute(RedisCommand.INFO, () -> connection.info(node));
    }

    @Override
    public Properties info(RedisClusterNode node, String section) {
        return catMonitorHelper.execute(RedisCommand.INFO, () -> connection.info(node, section));
    }

    @Override
    public void shutdown(RedisClusterNode node) {
        catMonitorHelper.execute(RedisCommand.SHUTDOWN, () -> connection.shutdown(node));
    }

    @Override
    public Properties getConfig(RedisClusterNode node, String pattern) {
        return catMonitorHelper.execute(RedisCommand.CONFIG_GET, () -> connection.getConfig(node, pattern));
    }

    @Override
    public void setConfig(RedisClusterNode node, String param, String value) {
        catMonitorHelper.execute(RedisCommand.CONFIG_GET, () -> connection.setConfig(node, param, value));
    }

    @Override
    public void resetConfigStats(RedisClusterNode node) {
        catMonitorHelper.execute(RedisCommand.CONFIG_RESETSTAT, () -> connection.resetConfigStats(node));
    }

    @Override
    public Long time(RedisClusterNode node) {
        return catMonitorHelper.execute(RedisCommand.TIME, () -> connection.time(node));
    }

    @Override
    public List<RedisClientInfo> getClientList(RedisClusterNode node) {
        return catMonitorHelper.execute(RedisCommand.CLIENT_LIST, () -> connection.getClientList(node));
    }
}
