package com.cxytiandi.kitty.db.redis.spring;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.core.types.RedisClientInfo;

/**
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-04-27 21:02
 */
public class CatMonitorRedisConnection implements RedisConnection {

    private final RedisConnection connection;

    private CatMonitorHelper catMonitorHelper;

    public CatMonitorRedisConnection(RedisConnection connection) {
        this.connection = connection;
        this.catMonitorHelper = new CatMonitorHelper();
    }

    @Override
    public void close() throws DataAccessException {
        connection.close();
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public Object getNativeConnection() {
        return null;
    }

    @Override
    public boolean isQueueing() {
        return false;
    }

    @Override
    public boolean isPipelined() {
        return false;
    }

    @Override
    public void openPipeline() {

    }

    @Override
    public List<Object> closePipeline() throws RedisPipelineException {
        return null;
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return null;
    }

    @Override
    public Object execute(String s, byte[]... bytes) {
        return null;
    }

    @Override
    public void select(int i) {

    }

    @Override
    public byte[] echo(byte[] bytes) {
        return new byte[0];
    }

    @Override
    public String ping() {
        return null;
    }

    @Override
    public Long geoAdd(byte[] bytes, Point point, byte[] bytes1) {
        return null;
    }

    @Override
    public Long geoAdd(byte[] bytes, Map<byte[], Point> map) {
        return null;
    }

    @Override
    public Long geoAdd(byte[] bytes, Iterable<GeoLocation<byte[]>> iterable) {
        return null;
    }

    @Override
    public Distance geoDist(byte[] bytes, byte[] bytes1, byte[] bytes2) {
        return null;
    }

    @Override
    public Distance geoDist(byte[] bytes, byte[] bytes1, byte[] bytes2, Metric metric) {
        return null;
    }

    @Override
    public List<String> geoHash(byte[] bytes, byte[]... bytes1) {
        return null;
    }

    @Override
    public List<Point> geoPos(byte[] bytes, byte[]... bytes1) {
        return null;
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] bytes, Circle circle) {
        return null;
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] bytes, Circle circle, GeoRadiusCommandArgs geoRadiusCommandArgs) {
        return null;
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] bytes, byte[] bytes1, Distance distance) {
        return null;
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] bytes, byte[] bytes1, Distance distance, GeoRadiusCommandArgs geoRadiusCommandArgs) {
        return null;
    }

    @Override
    public Long geoRemove(byte[] bytes, byte[]... bytes1) {
        return null;
    }

    @Override
    public Boolean hSet(byte[] bytes, byte[] bytes1, byte[] bytes2) {
        return null;
    }

    @Override
    public Boolean hSetNX(byte[] bytes, byte[] bytes1, byte[] bytes2) {
        return null;
    }

    @Override
    public byte[] hGet(byte[] bytes, byte[] bytes1) {
        return new byte[0];
    }

    @Override
    public List<byte[]> hMGet(byte[] bytes, byte[]... bytes1) {
        return null;
    }

    @Override
    public void hMSet(byte[] bytes, Map<byte[], byte[]> map) {

    }

    @Override
    public Long hIncrBy(byte[] bytes, byte[] bytes1, long l) {
        return null;
    }

    @Override
    public Double hIncrBy(byte[] bytes, byte[] bytes1, double v) {
        return null;
    }

    @Override
    public Boolean hExists(byte[] bytes, byte[] bytes1) {
        return null;
    }

    @Override
    public Long hDel(byte[] bytes, byte[]... bytes1) {
        return null;
    }

    @Override
    public Long hLen(byte[] bytes) {
        return null;
    }

    @Override
    public Set<byte[]> hKeys(byte[] bytes) {
        return null;
    }

    @Override
    public List<byte[]> hVals(byte[] bytes) {
        return null;
    }

    @Override
    public Map<byte[], byte[]> hGetAll(byte[] bytes) {
        return null;
    }

    @Override
    public Cursor<Entry<byte[], byte[]>> hScan(byte[] bytes, ScanOptions scanOptions) {
        return null;
    }

    @Override
    public Long hStrLen(byte[] bytes, byte[] bytes1) {
        return null;
    }

    @Override
    public Long pfAdd(byte[] bytes, byte[]... bytes1) {
        return null;
    }

    @Override
    public Long pfCount(byte[]... bytes) {
        return null;
    }

    @Override
    public void pfMerge(byte[] bytes, byte[]... bytes1) {

    }

    @Override
    public Long exists(byte[]... bytes) {
        return null;
    }

    @Override
    public Long del(byte[]... bytes) {
        return null;
    }

    @Override
    public Long unlink(byte[]... bytes) {
        return null;
    }

    @Override
    public DataType type(byte[] bytes) {
        return null;
    }

    @Override
    public Long touch(byte[]... bytes) {
        return null;
    }

    @Override
    public Set<byte[]> keys(byte[] bytes) {
        return null;
    }

    @Override
    public Cursor<byte[]> scan(ScanOptions scanOptions) {
        return null;
    }

    @Override
    public byte[] randomKey() {
        return new byte[0];
    }

    @Override
    public void rename(byte[] bytes, byte[] bytes1) {

    }

    @Override
    public Boolean renameNX(byte[] bytes, byte[] bytes1) {
        return null;
    }

    @Override
    public Boolean expire(byte[] bytes, long l) {
        return null;
    }

    @Override
    public Boolean pExpire(byte[] bytes, long l) {
        return null;
    }

    @Override
    public Boolean expireAt(byte[] bytes, long l) {
        return null;
    }

    @Override
    public Boolean pExpireAt(byte[] bytes, long l) {
        return null;
    }

    @Override
    public Boolean persist(byte[] bytes) {
        return null;
    }

    @Override
    public Boolean move(byte[] bytes, int i) {
        return null;
    }

    @Override
    public Long ttl(byte[] bytes) {
        return null;
    }

    @Override
    public Long ttl(byte[] bytes, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Long pTtl(byte[] bytes) {
        return null;
    }

    @Override
    public Long pTtl(byte[] bytes, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public List<byte[]> sort(byte[] bytes, SortParameters sortParameters) {
        return null;
    }

    @Override
    public Long sort(byte[] bytes, SortParameters sortParameters, byte[] bytes1) {
        return null;
    }

    @Override
    public byte[] dump(byte[] bytes) {
        return new byte[0];
    }

    @Override
    public void restore(byte[] bytes, long l, byte[] bytes1, boolean b) {

    }

    @Override
    public ValueEncoding encodingOf(byte[] bytes) {
        return null;
    }

    @Override
    public Duration idletime(byte[] bytes) {
        return null;
    }

    @Override
    public Long refcount(byte[] bytes) {
        return null;
    }

    @Override
    public Long rPush(byte[] bytes, byte[]... bytes1) {
        return null;
    }

    @Override
    public Long lPush(byte[] bytes, byte[]... bytes1) {
        return null;
    }

    @Override
    public Long rPushX(byte[] bytes, byte[] bytes1) {
        return null;
    }

    @Override
    public Long lPushX(byte[] bytes, byte[] bytes1) {
        return null;
    }

    @Override
    public Long lLen(byte[] bytes) {
        return null;
    }

    @Override
    public List<byte[]> lRange(byte[] bytes, long l, long l1) {
        return null;
    }

    @Override
    public void lTrim(byte[] bytes, long l, long l1) {

    }

    @Override
    public byte[] lIndex(byte[] bytes, long l) {
        return new byte[0];
    }

    @Override
    public Long lInsert(byte[] bytes, Position position, byte[] bytes1, byte[] bytes2) {
        return null;
    }

    @Override
    public void lSet(byte[] bytes, long l, byte[] bytes1) {

    }

    @Override
    public Long lRem(byte[] bytes, long l, byte[] bytes1) {
        return null;
    }

    @Override
    public byte[] lPop(byte[] bytes) {
        return new byte[0];
    }

    @Override
    public byte[] rPop(byte[] bytes) {
        return new byte[0];
    }

    @Override
    public List<byte[]> bLPop(int i, byte[]... bytes) {
        return null;
    }

    @Override
    public List<byte[]> bRPop(int i, byte[]... bytes) {
        return null;
    }

    @Override
    public byte[] rPopLPush(byte[] bytes, byte[] bytes1) {
        return new byte[0];
    }

    @Override
    public byte[] bRPopLPush(int i, byte[] bytes, byte[] bytes1) {
        return new byte[0];
    }

    @Override
    public boolean isSubscribed() {
        return false;
    }

    @Override
    public Subscription getSubscription() {
        return null;
    }

    @Override
    public Long publish(byte[] bytes, byte[] bytes1) {
        return null;
    }

    @Override
    public void subscribe(MessageListener messageListener, byte[]... bytes) {

    }

    @Override
    public void pSubscribe(MessageListener messageListener, byte[]... bytes) {

    }

    @Override
    public void scriptFlush() {

    }

    @Override
    public void scriptKill() {

    }

    @Override
    public String scriptLoad(byte[] bytes) {
        return null;
    }

    @Override
    public List<Boolean> scriptExists(String... strings) {
        return null;
    }

    @Override
    public <T> T eval(byte[] script, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return catMonitorHelper.execute(RedisCommand.EVAL, () -> connection.eval(script, returnType, numKeys, keysAndArgs));
    }

    @Override
    public <T> T evalSha(String scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return catMonitorHelper.execute(RedisCommand.EVALSHA, () -> connection.evalSha(scriptSha, returnType, numKeys, keysAndArgs));
    }

    @Override
    public <T> T evalSha(byte[] scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return catMonitorHelper.execute(RedisCommand.EVALSHA, () -> connection.evalSha(scriptSha, returnType, numKeys, keysAndArgs));
    }

    @Override
    public void bgReWriteAof() {
        catMonitorHelper.execute(RedisCommand.BGREWRITEAOF, () -> connection.bgReWriteAof());
    }

    @Override
    public void bgSave() {
        catMonitorHelper.execute(RedisCommand.BGSAVE, () -> connection.bgSave());
    }

    @Override
    public Long lastSave() {
        return catMonitorHelper.execute(RedisCommand.LASTSAVE, () -> connection.lastSave());
    }

    @Override
    public void save() {
        catMonitorHelper.execute(RedisCommand.SAVE, () -> connection.save());
    }

    @Override
    public Long dbSize() {
        return catMonitorHelper.execute(RedisCommand.DBSIZE, () -> connection.dbSize());
    }

    @Override
    public void flushDb() {
        catMonitorHelper.execute(RedisCommand.FLUSHDB, () -> connection.flushDb());
    }

    @Override
    public void flushAll() {
        catMonitorHelper.execute(RedisCommand.FLUSHALL, () -> connection.flushAll());
    }

    @Override
    public Properties info() {
        return catMonitorHelper.execute(RedisCommand.INFO, () -> connection.info());
    }

    @Override
    public Properties info(String section) {
        return catMonitorHelper.execute(RedisCommand.INFO, () -> connection.info(section));
    }

    @Override
    public void shutdown() {
        catMonitorHelper.execute(RedisCommand.SHUTDOWN, () -> connection.shutdown());
    }

    @Override
    public void shutdown(ShutdownOption shutdownOption) {
        catMonitorHelper.execute(RedisCommand.SHUTDOWN, () -> connection.shutdown(shutdownOption));
    }

    @Override
    public Properties getConfig(String pattern) {
        return catMonitorHelper.execute(RedisCommand.CONFIG_GET, () -> connection.getConfig(pattern));
    }

    @Override
    public void setConfig(String param, String value) {
        catMonitorHelper.execute(RedisCommand.CONFIG_SET, () -> connection.setConfig(param, value));
    }

    @Override
    public void resetConfigStats() {
        catMonitorHelper.execute(RedisCommand.CONFIG_RESETSTAT, () -> connection.resetConfigStats());
    }

    @Override
    public Long time() {
        return catMonitorHelper.execute(RedisCommand.TIME, () -> connection.time());
    }

    @Override
    public void killClient(String host, int port) {
        catMonitorHelper.execute(RedisCommand.CLIENT_KILL, () -> connection.killClient(host, port));
    }

    @Override
    public void setClientName(byte[] name) {
        catMonitorHelper.execute(RedisCommand.CLIENT_SETNAME, () -> connection.setClientName(name));
    }

    @Override
    public String getClientName() {
        return catMonitorHelper.execute(RedisCommand.CLIENT_GETNAME, () -> connection.getClientName());
    }

    @Override
    public List<RedisClientInfo> getClientList() {
        return catMonitorHelper.execute(RedisCommand.CLIENT_LIST, () -> connection.getClientList());
    }

    @Override
    public void slaveOf(String host, int port) {
        catMonitorHelper.execute(RedisCommand.SLAVEOF, () -> connection.slaveOf(host, port));
    }

    @Override
    public void slaveOfNoOne() {
        catMonitorHelper.execute(RedisCommand.SLAVEOFNOONE, () -> connection.slaveOfNoOne());
    }

    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option) {
        catMonitorHelper.execute(RedisCommand.MIGRATE, key, () -> connection.migrate(key, target, dbIndex, option));
    }

    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option, long timeout) {
        catMonitorHelper.execute(RedisCommand.MIGRATE, key, () -> connection.migrate(key, target, dbIndex, option, timeout));
    }

    @Override
    public Long sAdd(byte[] key, byte[]... values) {
        return catMonitorHelper.execute(RedisCommand.SADD, key, () -> connection.sAdd(key, values));
    }

    @Override
    public Long sRem(byte[] key, byte[]... values) {
        return catMonitorHelper.execute(RedisCommand.SREM, key, () -> connection.sRem(key, values));
    }

    @Override
    public byte[] sPop(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.SPOP, key, () -> connection.sPop(key));
    }

    @Override
    public List<byte[]> sPop(byte[] key, long count) {
        return catMonitorHelper.execute(RedisCommand.SPOP, key, () -> connection.sPop(key, count));
    }

    @Override
    public Boolean sMove(byte[] srcKey, byte[] destKey, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.SMOVE, srcKey, () -> connection.sMove(srcKey, destKey, value));
    }

    @Override
    public Long sCard(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.SCARD, key, () -> connection.sCard(key));
    }

    @Override
    public Boolean sIsMember(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.SISMEMBER, key, () -> connection.sIsMember(key, value));
    }

    @Override
    public Set<byte[]> sInter(byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.SINTER, () -> connection.sInter(keys));
    }

    @Override
    public Long sInterStore(byte[] destKey, byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.SINTERSTORE, destKey, () -> connection.sInterStore(destKey, keys));
    }

    @Override
    public Set<byte[]> sUnion(byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.SUNION, () -> connection.sUnion(keys));
    }

    @Override
    public Long sUnionStore(byte[] destKey, byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.SUNIONSTORE, destKey, () -> connection.sUnionStore(destKey, keys));
    }

    @Override
    public Set<byte[]> sDiff(byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.SDIFF, () -> connection.sDiff(keys));
    }

    @Override
    public Long sDiffStore(byte[] destKey, byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.SDIFFSTORE, destKey, () -> connection.sDiffStore(destKey, keys));
    }

    @Override
    public Set<byte[]> sMembers(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.SMEMBERS, key, () -> connection.sMembers(key));
    }

    @Override
    public byte[] sRandMember(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.SRANDMEMBER, key, () -> connection.sRandMember(key));
    }

    @Override
    public List<byte[]> sRandMember(byte[] key, long count) {
        return catMonitorHelper.execute(RedisCommand.SRANDMEMBER, key, () -> connection.sRandMember(key, count));
    }

    @Override
    public Cursor<byte[]> sScan(byte[] key, ScanOptions scanOptions) {
        return catMonitorHelper.execute(RedisCommand.SSCAN, key, () -> connection.sScan(key, scanOptions));
    }

    @Override
    public byte[] get(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.GET, key, () -> connection.get(key));
    }

    @Override
    public byte[] getSet(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.GETSET, key, () -> connection.getSet(key, value));
    }

    @Override
    public List<byte[]> mGet(byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.MGET, keys, () -> connection.mGet(keys));
    }

    @Override
    public Boolean set(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.SET, key, () -> connection.set(key, value));
    }

    @Override
    public Boolean set(byte[] key, byte[] value, Expiration expiration, SetOption option) {
        return catMonitorHelper.execute(RedisCommand.SET, key, () -> connection.set(key, value, expiration, option));
    }

    @Override
    public Boolean setNX(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.SETNX, key, () -> connection.setNX(key, value));
    }

    @Override
    public Boolean setEx(byte[] key, long seconds, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.SETEX, key, () -> connection.setEx(key, seconds, value));
    }

    @Override
    public Boolean pSetEx(byte[] key, long milliseconds, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.PSETEX, key, () -> connection.setEx(key, milliseconds, value));
    }

    @Override
    public Boolean mSet(Map<byte[], byte[]> tuple) {
        return catMonitorHelper.execute(RedisCommand.MSET, () -> connection.mSet(tuple));
    }

    @Override
    public Boolean mSetNX(Map<byte[], byte[]> tuple) {
        return catMonitorHelper.execute(RedisCommand.MSETNX, () -> connection.mSetNX(tuple));
    }

    @Override
    public Long incr(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.INCR, key, () -> connection.incr(key));
    }

    @Override
    public Long incrBy(byte[] key, long value) {
        return catMonitorHelper.execute(RedisCommand.INCRBY, key, () -> connection.incrBy(key, value));
    }

    @Override
    public Double incrBy(byte[] key, double value) {
        return catMonitorHelper.execute(RedisCommand.INCRBY, key, () -> connection.incrBy(key, value));
    }

    @Override
    public Long decr(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.DECR, key, () -> connection.decr(key));
    }

    @Override
    public Long decrBy(byte[] key, long value) {
        return catMonitorHelper.execute(RedisCommand.DECRBY, key, () -> connection.decrBy(key, value));
    }

    @Override
    public Long append(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.APPEND, key, () -> connection.append(key, value));
    }

    @Override
    public byte[] getRange(byte[] key, long begin, long end) {
        return catMonitorHelper.execute(RedisCommand.GETRANGE, key, () -> connection.getRange(key, begin, end));
    }

    @Override
    public void setRange(byte[] key, byte[] value, long offset) {
        catMonitorHelper.execute(RedisCommand.SETRANGE, key, () -> connection.setRange(key, value, offset));
    }

    @Override
    public Boolean getBit(byte[] key, long offset) {
        return catMonitorHelper.execute(RedisCommand.GETBIT, key, () -> connection.getBit(key, offset));
    }

    @Override
    public Boolean setBit(byte[] key, long offset, boolean value) {
        return catMonitorHelper.execute(RedisCommand.SETBIT, key, () -> connection.setBit(key, offset, value));
    }

    @Override
    public Long bitCount(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.BITCOUNT, key, () -> connection.bitCount(key));
    }

    @Override
    public Long bitCount(byte[] key, long begin, long end) {
        return catMonitorHelper.execute(RedisCommand.BITCOUNT, key, () -> connection.bitCount(key, begin, end));
    }

    @Override
    public List<Long> bitField(byte[] key, BitFieldSubCommands bitFieldSubCommands) {
        return catMonitorHelper.execute(RedisCommand.BITFIELD, key, () -> connection.bitField(key, bitFieldSubCommands));
    }

    @Override
    public Long bitOp(BitOperation bitOperation, byte[] destination, byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.BITOP, keys, () -> connection.bitOp(bitOperation, destination, keys));
    }

    @Override
    public Long bitPos(byte[] key, boolean b, org.springframework.data.domain.Range<Long> range) {
        return catMonitorHelper.execute(RedisCommand.BITPOS, key, () -> connection.bitPos(key, b, range));
    }

    @Override
    public Long strLen(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.STRLEN, key, () -> connection.strLen(key));
    }

    @Override
    public void multi() {
        catMonitorHelper.execute(RedisCommand.MULTI, () ->  connection.multi());
    }

    @Override
    public List<Object> exec() {
        return catMonitorHelper.execute(RedisCommand.EXEC, () -> connection.exec());
    }

    @Override
    public void discard() {
        catMonitorHelper.execute(RedisCommand.DISCARD, () ->  connection.discard());
    }

    @Override
    public void watch(byte[]... keys) {
        catMonitorHelper.execute(RedisCommand.WATCH, keys, () ->  connection.watch());
    }

    @Override
    public void unwatch() {
        catMonitorHelper.execute(RedisCommand.UNWATCH, () ->  connection.unwatch());
    }

    @Override
    public Boolean zAdd(byte[] key, double score, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.ZADD, key, () -> connection.zAdd(key, score, value));
    }

    @Override
    public Long zAdd(byte[] key, Set<Tuple> tuples) {
        return catMonitorHelper.execute(RedisCommand.ZADD, key, () -> connection.zAdd(key, tuples));
    }

    @Override
    public Long zRem(byte[] key, byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.ZREM, key, () -> connection.zRem(key, keys));
    }

    @Override
    public Double zIncrBy(byte[] key, double increment, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.ZINCRBY, key, () -> connection.zIncrBy(key, increment, value));
    }

    @Override
    public Long zRank(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.ZRANK, key, () -> connection.zRank(key, value));
    }

    @Override
    public Long zRevRank(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.ZREVRANK, key, () -> connection.zRevRank(key, value));
    }

    @Override
    public Set<byte[]> zRange(byte[] key, long start, long end) {
        return catMonitorHelper.execute(RedisCommand.ZRANGE, key, () -> connection.zRange(key, start, end));
    }

    @Override
    public Set<Tuple> zRangeWithScores(byte[] key, long start, long end) {
        return catMonitorHelper.execute(RedisCommand.ZRANGE_WITHSCORES, key, () -> connection.zRangeWithScores(key, start, end));
    }

    @Override
    public Set<Tuple> zRangeByScoreWithScores(byte[] key, Range range, Limit limit) {
        return catMonitorHelper.execute(RedisCommand.ZRANGEBYSCORE_WITHSCORES, key, () -> connection.zRangeByScoreWithScores(key, range, limit));
    }

    @Override
    public Set<byte[]> zRevRange(byte[] key, long start, long end) {
        return catMonitorHelper.execute(RedisCommand.ZREVRANGE, key, () -> connection.zRevRange(key, start, end));
    }

    @Override
    public Set<Tuple> zRevRangeWithScores(byte[] key, long start, long end) {
        return catMonitorHelper.execute(RedisCommand.ZREVRANGE_WITHSCORES, key, () -> connection.zRevRangeWithScores(key, start, end));
    }

    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, Range range, Limit limit) {
        return catMonitorHelper.execute(RedisCommand.ZREVRANGEBYSCORE, key, () -> connection.zRevRangeByScore(key, range, limit));
    }

    @Override
    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, Range range, Limit limit) {
        return catMonitorHelper.execute(RedisCommand.ZREVRANGEBYSCORE_WITHSCORES, key, () -> connection.zRevRangeByScoreWithScores(key, range, limit));
    }

    @Override
    public Long zCount(byte[] key, Range range) {
        return catMonitorHelper.execute(RedisCommand.ZCOUNT, key, () -> connection.zCount(key, range));
    }

    @Override
    public Long zCard(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.ZCARD, key, () -> connection.zCard(key));
    }

    @Override
    public Double zScore(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.ZSCORE, key, () -> connection.zScore(key, value));
    }

    @Override
    public Long zRemRange(byte[] key, long start, long end) {
        return catMonitorHelper.execute(RedisCommand.ZREMRANGE, key, () -> connection.zRemRange(key, start, end));
    }

    @Override
    public Long zRemRangeByScore(byte[] key, Range range) {
        return catMonitorHelper.execute(RedisCommand.ZREMRANGEBYSCORE, key, () -> connection.zRemRangeByScore(key, range));
    }

    @Override
    public Long zUnionStore(byte[] destKey, byte[]... sets) {
        return catMonitorHelper.execute(RedisCommand.ZUNIONSTORE, destKey, () -> connection.zUnionStore(destKey, sets));
    }

    @Override
    public Long zUnionStore(byte[] destKey, Aggregate aggregate, Weights weights, byte[]... sets) {
        return catMonitorHelper.execute(RedisCommand.ZUNIONSTORE, destKey, () -> connection.zUnionStore(destKey, aggregate, weights, sets));
    }

    @Override
    public Long zInterStore(byte[] destKey, byte[]... sets) {
        return catMonitorHelper.execute(RedisCommand.ZINTERSTORE, destKey, () -> connection.zInterStore(destKey, sets));
    }

    @Override
    public Long zInterStore(byte[] destKey, Aggregate aggregate, Weights weights, byte[]... sets) {
        return catMonitorHelper.execute(RedisCommand.ZINTERSTORE, destKey, () -> connection.zInterStore(destKey, aggregate, weights, sets));
    }

    @Override
    public Cursor<Tuple> zScan(byte[] key, ScanOptions scanOptions) {
        return catMonitorHelper.execute(RedisCommand.ZSCAN, key, () -> connection.zScan(key, scanOptions));
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, String min, String max, long offset, long count) {
        return catMonitorHelper.execute(RedisCommand.ZRANGEBYSCORE, key, () -> connection.zRangeByScore(key, min, max, offset, count));
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, Range range, Limit limit) {
        return catMonitorHelper.execute(RedisCommand.ZRANGEBYSCORE, key, () -> connection.zRangeByScore(key, range, limit));
    }

    @Override
    public Set<byte[]> zRangeByLex(byte[] key, Range range, Limit limit) {
        return catMonitorHelper.execute(RedisCommand.ZRANGEBYLEX, key, () -> connection.zRangeByLex(key, range, limit));
    }
}