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
        return connection.isClosed();
    }

    @Override
    public Object getNativeConnection() {
        return connection.getNativeConnection();
    }

    @Override
    public boolean isQueueing() {
        return connection.isQueueing();
    }

    @Override
    public boolean isPipelined() {
        return connection.isPipelined();
    }

    @Override
    public void openPipeline() {
        connection.openPipeline();
    }

    @Override
    public List<Object> closePipeline() throws RedisPipelineException {
        return connection.closePipeline();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return connection.getSentinelConnection();
    }

    @Override
    public Object execute(String command, byte[]... args) {
        return catMonitorHelper.execute(RedisCommand.EXECUTE, () -> connection.execute(command, args));
    }

    @Override
    public void select(int dbIndex) {
        catMonitorHelper.execute(RedisCommand.SELECT, () -> connection.select(dbIndex));
    }

    @Override
    public byte[] echo(byte[] message) {
        return catMonitorHelper.execute(RedisCommand.ECHO, () -> connection.echo(message));
    }

    @Override
    public String ping() {
        return catMonitorHelper.execute(RedisCommand.PING, () -> connection.ping());
    }

    @Override
    public Long geoAdd(byte[] key, Point point, byte[] member) {
        return catMonitorHelper.execute(RedisCommand.GEOADD, key, () -> connection.geoAdd(key, point, member));
    }

    @Override
    public Long geoAdd(byte[] key, Map<byte[], Point> memberCoordinateMap) {
        return catMonitorHelper.execute(RedisCommand.GEOADD, key, () -> connection.geoAdd(key, memberCoordinateMap));
    }

    @Override
    public Long geoAdd(byte[] key, Iterable<GeoLocation<byte[]>> locations) {
        return catMonitorHelper.execute(RedisCommand.GEOADD, key, () -> connection.geoAdd(key, locations));
    }

    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2) {
        return catMonitorHelper.execute(RedisCommand.GEODIST, key, () -> connection.geoDist(key, member1, member2));
    }

    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2, Metric metric) {
        return catMonitorHelper.execute(RedisCommand.GEODIST, key, () -> connection.geoDist(key, member1, member2, metric));
    }

    @Override
    public List<String> geoHash(byte[] key, byte[]... members) {
        return catMonitorHelper.execute(RedisCommand.GEOHASH, key, () -> connection.geoHash(key, members));
    }

    @Override
    public List<Point> geoPos(byte[] key, byte[]... members) {
        return catMonitorHelper.execute(RedisCommand.GEOPOS, key, () -> connection.geoPos(key, members));
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within) {
        return catMonitorHelper.execute(RedisCommand.GEORADIUS, key, () -> connection.geoRadius(key, within));
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within, GeoRadiusCommandArgs args) {
        return catMonitorHelper.execute(RedisCommand.GEORADIUS, key, () -> connection.geoRadius(key, within, args));
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member,
                                                             Distance radius) {
        return catMonitorHelper.execute(RedisCommand.GEORADIUSBYMEMBER, key, () -> connection.geoRadiusByMember(key, member, radius));
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member,
                                                             Distance radius, GeoRadiusCommandArgs args) {
        return catMonitorHelper.execute(RedisCommand.GEORADIUSBYMEMBER, key, () -> connection.geoRadiusByMember(key, member, radius, args));
    }

    @Override
    public Long geoRemove(byte[] key, byte[]... members) {
        return catMonitorHelper.execute(RedisCommand.GEOREMOVE, key, () -> connection.geoRemove(key, members));
    }

    @Override
    public Boolean hSet(byte[] key, byte[] field, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.HSET, key, () -> connection.hSet(key, field, value));
    }

    @Override
    public Boolean hSetNX(byte[] key, byte[] field, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.HSETNX, key, () -> connection.hSetNX(key, field, value));
    }

    @Override
    public byte[] hGet(byte[] key, byte[] field) {
        return catMonitorHelper.execute(RedisCommand.HGET, key, () -> connection.hGet(key, field));
    }

    @Override
    public List<byte[]> hMGet(byte[] key, byte[]... fields) {
        return catMonitorHelper.execute(RedisCommand.HMGET, key, () -> connection.hMGet(key, fields));
    }

    @Override
    public void hMSet(byte[] key, Map<byte[], byte[]> hashes) {
        catMonitorHelper.execute(RedisCommand.HMSET, key, () -> connection.hMSet(key, hashes));
    }

    @Override
    public Long hIncrBy(byte[] key, byte[] field, long delta) {
        return catMonitorHelper.execute(RedisCommand.HINCRBY, key, () -> connection.hIncrBy(key, field, delta));
    }

    @Override
    public Double hIncrBy(byte[] key, byte[] field, double delta) {
        return catMonitorHelper.execute(RedisCommand.HINCRBY, key, () -> connection.hIncrBy(key, field, delta));
    }

    @Override
    public Boolean hExists(byte[] key, byte[] field) {
        return catMonitorHelper.execute(RedisCommand.HEXISTS, key, () -> connection.hExists(key, field));
    }

    @Override
    public Long hDel(byte[] key, byte[]... fields) {
        return catMonitorHelper.execute(RedisCommand.HDEL, key, () -> connection.hDel(key));
    }

    @Override
    public Long hLen(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.HLEN, key, () -> connection.hLen(key));
    }

    @Override
    public Set<byte[]> hKeys(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.HKEYS, key, () -> connection.hKeys(key));
    }

    @Override
    public List<byte[]> hVals(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.HVALS, key, () -> connection.hVals(key));
    }

    @Override
    public Map<byte[], byte[]> hGetAll(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.HGETALL, key, () -> connection.hGetAll(key));
    }

    @Override
    public Cursor<Entry<byte[], byte[]>> hScan(byte[] key, ScanOptions options) {
        return catMonitorHelper.execute(RedisCommand.HSCAN, key, () -> connection.hScan(key, options));
    }

    @Override
    public Long hStrLen(byte[] key, byte[] field) {
        return catMonitorHelper.execute(RedisCommand.HSTRLEN, key, () -> connection.hStrLen(key, field));
    }

    @Override
    public Long pfAdd(byte[] key, byte[]... values) {
        return catMonitorHelper.execute(RedisCommand.PFADD, key, () -> connection.pfAdd(key, values));
    }

    @Override
    public Long pfCount(byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.PFCOUNT, keys, () -> connection.pfCount(keys));
    }

    @Override
    public void pfMerge(byte[] destinationKey, byte[]... sourceKeys) {
        catMonitorHelper.execute(RedisCommand.PFMERGE, destinationKey, () -> connection.pfMerge(destinationKey, sourceKeys));
    }

    @Override
    public Long exists(byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.EXISTS, keys, () -> connection.exists(keys));
    }

    @Override
    public Long del(byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.DEL, keys, () -> connection.del(keys));
    }

    @Override
    public Long unlink(byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.UNLINK, keys, () -> connection.unlink(keys));
    }

    @Override
    public DataType type(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.TYPE, key, () -> connection.type(key));
    }

    @Override
    public Long touch(byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.TOUCH, keys, () -> connection.touch(keys));
    }

    @Override
    public Set<byte[]> keys(byte[] pattern) {
        return catMonitorHelper.execute(RedisCommand.KEYS, pattern, () -> connection.keys(pattern));
    }

    @Override
    public Cursor<byte[]> scan(ScanOptions options) {
        return catMonitorHelper.execute(RedisCommand.SCAN, () -> connection.scan(options));
    }

    @Override
    public byte[] randomKey() {
        return catMonitorHelper.execute(RedisCommand.RANDOMKEY, () -> connection.randomKey());
    }

    @Override
    public void rename(byte[] oldName, byte[] newName) {
        catMonitorHelper.execute(RedisCommand.RENAME, () -> connection.rename(oldName, newName));
    }

    @Override
    public Boolean renameNX(byte[] oldName, byte[] newName) {
        return catMonitorHelper.execute(RedisCommand.RENAMENX, () -> connection.renameNX(oldName, newName));
    }

    @Override
    public Boolean expire(byte[] key, long seconds) {
        return catMonitorHelper.execute(RedisCommand.EXPIRE, key, () -> connection.expire(key, seconds));
    }

    @Override
    public Boolean pExpire(byte[] key, long millis) {
        return catMonitorHelper.execute(RedisCommand.PEXPIRE, key, () -> connection.pExpire(key, millis));
    }

    @Override
    public Boolean expireAt(byte[] key, long unixTime) {
        return catMonitorHelper.execute(RedisCommand.EXPIREAT, key, () -> connection.expireAt(key, unixTime));
    }

    @Override
    public Boolean pExpireAt(byte[] key, long unixTimeInMillis) {
        return catMonitorHelper.execute(RedisCommand.PEXPIREAT, key, () -> connection.pExpireAt(key, unixTimeInMillis));
    }

    @Override
    public Boolean persist(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.PERSIST, key, () -> connection.persist(key));
    }

    @Override
    public Boolean move(byte[] key, int dbIndex) {
        return catMonitorHelper.execute(RedisCommand.MOVE, key, () -> connection.move(key, dbIndex));
    }

    @Override
    public Long ttl(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.TTL, key, () -> connection.ttl(key));
    }

    @Override
    public Long ttl(byte[] key, TimeUnit timeUnit) {
        return catMonitorHelper.execute(RedisCommand.TTL, key, () -> connection.ttl(key, timeUnit));
    }

    @Override
    public Long pTtl(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.PTTL, key, () -> connection.pTtl(key));
    }

    @Override
    public Long pTtl(byte[] key, TimeUnit timeUnit) {
        return catMonitorHelper.execute(RedisCommand.PTTL, key, () -> connection.pTtl(key, timeUnit));
    }

    @Override
    public List<byte[]> sort(byte[] key, SortParameters params) {
        return catMonitorHelper.execute(RedisCommand.SORT, key, () -> connection.sort(key, params));
    }

    @Override
    public Long sort(byte[] key, SortParameters params, byte[] storeKey) {
        return catMonitorHelper.execute(RedisCommand.SORT, key, () -> connection.sort(key, params, storeKey));
    }

    @Override
    public byte[] dump(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.DUMP, key, () -> connection.dump(key));
    }

    @Override
    public void restore(byte[] key, long ttlInMillis, byte[] serializedValue) {
        catMonitorHelper.execute(RedisCommand.RESTORE, key, () -> connection.restore(key, ttlInMillis, serializedValue));
    }

    @Override
    public void restore(byte[] key, long ttlInMillis, byte[] serializedValue, boolean replace) {
        catMonitorHelper.execute(RedisCommand.RESTORE, key, () -> connection.restore(key, ttlInMillis, serializedValue, replace));
    }

    @Override
    public ValueEncoding encodingOf(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.ENCODING, key, () -> connection.encodingOf(key));
    }

    @Override
    public Duration idletime(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.IDLETIME, key, () -> connection.idletime(key));
    }

    @Override
    public Long refcount(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.REFCOUNT, key, () -> connection.refcount(key));
    }

    @Override
    public Long rPush(byte[] key, byte[]... values) {
        return catMonitorHelper.execute(RedisCommand.RPUSH, key, () -> connection.rPush(key, values));
    }

    @Override
    public Long lPush(byte[] key, byte[]... values) {
        return catMonitorHelper.execute(RedisCommand.LPUSH, key, () -> connection.lPush(key, values));
    }

    @Override
    public Long rPushX(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.RPUSHX, key, () -> connection.rPushX(key, value));
    }

    @Override
    public Long lPushX(byte[] key, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.LPUSHX, key, () -> connection.lPushX(key, value));
    }

    @Override
    public Long lLen(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.LLEN, key, () -> connection.lLen(key));
    }

    @Override
    public List<byte[]> lRange(byte[] key, long start, long end) {
        return catMonitorHelper.execute(RedisCommand.LRANGE, key, () -> connection.lRange(key, start, end));
    }

    @Override
    public void lTrim(byte[] key, long start, long end) {
        catMonitorHelper.execute(RedisCommand.LTRIM, key, () -> connection.lTrim(key, start, end));
    }

    @Override
    public byte[] lIndex(byte[] key, long index) {
        return catMonitorHelper.execute(RedisCommand.LINDEX, key, () -> connection.lIndex(key, index));
    }

    @Override
    public Long lInsert(byte[] key, Position where, byte[] pivot, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.LINSERT, key, () -> connection.lInsert(key, where, pivot, value));
    }

    @Override
    public void lSet(byte[] key, long index, byte[] value) {
        catMonitorHelper.execute(RedisCommand.LSET, key, () -> connection.lSet(key, index, value));
    }

    @Override
    public Long lRem(byte[] key, long count, byte[] value) {
        return catMonitorHelper.execute(RedisCommand.LREM, key, () -> connection.lRem(key, count, value));
    }

    @Override
    public byte[] lPop(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.LPOP, key, () -> connection.lPop(key));
    }

    @Override
    public byte[] rPop(byte[] key) {
        return catMonitorHelper.execute(RedisCommand.RPOP, key, () -> connection.rPop(key));
    }

    @Override
    public List<byte[]> bLPop(int timeout, byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.BLPOP, keys, () -> connection.bLPop(timeout, keys));
    }

    @Override
    public List<byte[]> bRPop(int timeout, byte[]... keys) {
        return catMonitorHelper.execute(RedisCommand.RPOPLPUSH, keys, () -> connection.bRPop(timeout, keys));
    }

    @Override
    public byte[] rPopLPush(byte[] srcKey, byte[] dstKey) {
        return catMonitorHelper.execute(RedisCommand.RPOPLPUSH, srcKey, () -> connection.rPopLPush(srcKey, dstKey));
    }

    @Override
    public byte[] bRPopLPush(int timeout, byte[] srcKey, byte[] dstKey) {
        return catMonitorHelper.execute(RedisCommand.BRPOPLPUSH, srcKey, () -> connection.bRPopLPush(timeout, srcKey, dstKey));
    }

    @Override
    public boolean isSubscribed() {
        return connection.isSubscribed();
    }

    @Override
    public Subscription getSubscription() {
        return connection.getSubscription();
    }

    @Override
    public Long publish(byte[] channel, byte[] message) {
        return catMonitorHelper.execute(RedisCommand.PUBLISH, () -> connection.publish(channel, message));
    }

    @Override
    public void subscribe(MessageListener listener, byte[]... channels) {
        catMonitorHelper.execute(RedisCommand.SUBSCRIBE, () -> connection.subscribe(listener, channels));
    }

    @Override
    public void pSubscribe(MessageListener listener, byte[]... patterns) {
        catMonitorHelper.execute(RedisCommand.PSUBSCRIBE, () -> connection.pSubscribe(listener, patterns));
    }

    @Override
    public void scriptFlush() {
        catMonitorHelper.execute(RedisCommand.SCRIPT_FLUSH, () -> connection.scriptFlush());
    }

    @Override
    public void scriptKill() {
        catMonitorHelper.execute(RedisCommand.SCRIPT_KILL, () -> connection.scriptKill());
    }

    @Override
    public String scriptLoad(byte[] script) {
        return catMonitorHelper.execute(RedisCommand.SCRIPT_LOAD, () -> connection.scriptLoad(script));
    }

    @Override
    public List<Boolean> scriptExists(String... scriptShas) {
        return catMonitorHelper.execute(RedisCommand.SCRIPT_EXISTS, () -> connection.scriptExists(scriptShas));
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