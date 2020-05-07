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

    public CatMonitorRedisClusterConnection(RedisClusterConnection connection) {
        super(connection);
        this.connection = connection;
    }


    @Override
    public String ping(RedisClusterNode redisClusterNode) {
        return null;
    }

    @Override
    public Set<byte[]> keys(RedisClusterNode redisClusterNode, byte[] bytes) {
        return null;
    }

    @Override
    public Cursor<byte[]> scan(RedisClusterNode redisClusterNode, ScanOptions scanOptions) {
        return null;
    }

    @Override
    public byte[] randomKey(RedisClusterNode redisClusterNode) {
        return new byte[0];
    }

    @Override
    public <T> T execute(String s, byte[] bytes, Collection<byte[]> collection) {
        return null;
    }

    @Override
    public Iterable<RedisClusterNode> clusterGetNodes() {
        return null;
    }

    @Override
    public Collection<RedisClusterNode> clusterGetSlaves(RedisClusterNode redisClusterNode) {
        return null;
    }

    @Override
    public Map<RedisClusterNode, Collection<RedisClusterNode>> clusterGetMasterSlaveMap() {
        return null;
    }

    @Override
    public Integer clusterGetSlotForKey(byte[] bytes) {
        return null;
    }

    @Override
    public RedisClusterNode clusterGetNodeForSlot(int i) {
        return null;
    }

    @Override
    public RedisClusterNode clusterGetNodeForKey(byte[] bytes) {
        return null;
    }

    @Override
    public ClusterInfo clusterGetClusterInfo() {
        return null;
    }

    @Override
    public void clusterAddSlots(RedisClusterNode redisClusterNode, int... ints) {

    }

    @Override
    public void clusterAddSlots(RedisClusterNode redisClusterNode, RedisClusterNode.SlotRange slotRange) {

    }

    @Override
    public Long clusterCountKeysInSlot(int i) {
        return null;
    }

    @Override
    public void clusterDeleteSlots(RedisClusterNode redisClusterNode, int... ints) {

    }

    @Override
    public void clusterDeleteSlotsInRange(RedisClusterNode redisClusterNode, RedisClusterNode.SlotRange slotRange) {

    }

    @Override
    public void clusterForget(RedisClusterNode redisClusterNode) {

    }

    @Override
    public void clusterMeet(RedisClusterNode redisClusterNode) {

    }

    @Override
    public void clusterSetSlot(RedisClusterNode redisClusterNode, int i, AddSlots addSlots) {

    }

    @Override
    public List<byte[]> clusterGetKeysInSlot(int i, Integer integer) {
        return null;
    }

    @Override
    public void clusterReplicate(RedisClusterNode redisClusterNode, RedisClusterNode redisClusterNode1) {

    }

    @Override
    public void bgReWriteAof(RedisClusterNode redisClusterNode) {

    }

    @Override
    public void bgSave(RedisClusterNode redisClusterNode) {

    }

    @Override
    public Long lastSave(RedisClusterNode redisClusterNode) {
        return null;
    }

    @Override
    public void save(RedisClusterNode redisClusterNode) {

    }

    @Override
    public Long dbSize(RedisClusterNode redisClusterNode) {
        return null;
    }

    @Override
    public void flushDb(RedisClusterNode redisClusterNode) {

    }

    @Override
    public void flushAll(RedisClusterNode redisClusterNode) {

    }

    @Override
    public Properties info(RedisClusterNode redisClusterNode) {
        return null;
    }

    @Override
    public Properties info(RedisClusterNode redisClusterNode, String s) {
        return null;
    }

    @Override
    public void shutdown(RedisClusterNode redisClusterNode) {

    }

    @Override
    public Properties getConfig(RedisClusterNode redisClusterNode, String s) {
        return null;
    }

    @Override
    public void setConfig(RedisClusterNode redisClusterNode, String s, String s1) {

    }

    @Override
    public void resetConfigStats(RedisClusterNode redisClusterNode) {

    }

    @Override
    public Long time(RedisClusterNode redisClusterNode) {
        return null;
    }

    @Override
    public List<RedisClientInfo> getClientList(RedisClusterNode redisClusterNode) {
        return null;
    }
}
