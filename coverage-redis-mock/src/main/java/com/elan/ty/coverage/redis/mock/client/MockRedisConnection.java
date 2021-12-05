package com.elan.ty.coverage.redis.mock.client;

import com.elan.ty.coverage.redis.mock.data.RedisBase;
import com.elan.ty.coverage.redis.mock.data.RedisObject;
import com.elan.ty.coverage.redis.mock.data.Slice;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPipelineException;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.data.redis.connection.Subscription;
import org.springframework.data.redis.connection.ValueEncoding;
import org.springframework.data.redis.connection.stream.ByteRecord;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.core.types.RedisClientInfo;

public class MockRedisConnection implements RedisConnection {
    private static RedisBase base = new RedisBase();

    @Override
    public void close() {
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
    public synchronized Object execute(String command, byte[]... args) {
        return null;
    }

    @Override
    public synchronized Boolean hSet(byte[] key, byte[] field, byte[] value) {
        Slice sKey = new Slice(key);
        Slice sFiled = new Slice(field);
        Slice sValue = new Slice(value);
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null || map.get(sFiled) == null) {
            base.hashPut(sKey, sFiled, sValue, -1L);
            return true;
        }
        base.hashPut(sKey, sFiled, sValue, -1L);
        return false;
    }

    @Override
    public synchronized Boolean hSetNX(byte[] key, byte[] field, byte[] value) {
        Slice sKey = new Slice(key);
        Slice sFiled = new Slice(field);
        Slice sValue = new Slice(value);
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null || map.get(sFiled) == null) {
            base.hashPut(sKey, sFiled, sValue, -1L);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized byte[] hGet(byte[] key, byte[] field) {
        Slice sKey = new Slice(key);
        Slice sFiled = new Slice(field);
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null || map.get(sFiled) == null) {
            return new byte[0];
        } else {
            return map.get(sFiled).data();
        }
    }

    @Override
    public synchronized List<byte[]> hMGet(byte[] key, byte[]... fields) {
        Slice sKey = new Slice(key);
        List<byte[]> list = new ArrayList<>();
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null) {
            return list;
        }
        for (int i = 1; i < fields.length; i++) {
            Slice sFiled = new Slice(fields[i]);
            Slice slice = map.get(sFiled);
            if (slice != null) {
                list.add(slice.data());
            }
        }
        return list;
    }

    @Override
    public synchronized void hMSet(byte[] key, Map<byte[], byte[]> hashes) {
        Slice sKey = new Slice(key);
        for (byte[] field : hashes.keySet()) {
            Slice sFiled = new Slice(field);
            Slice sValue = new Slice(hashes.get(field));
            base.hashPut(sKey, sFiled, sValue, -1l);
        }
    }

    @Override
    public synchronized Long hIncrBy(byte[] key, byte[] field, long delta) {
        Slice sKey = new Slice(key);
        Slice sFiled = new Slice(field);
        Slice sDelta = new Slice(delta);
        Slice slice = base.hashGet(sKey).get(sFiled);
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null || map.get(sFiled) == null) {
            base.hashPut(sKey, sFiled, sDelta, -1l);
            return delta;
        } else {
            Long r = Long.valueOf(new String(slice.data())) + delta;
            base.hashPut(sKey, sFiled, new Slice(String.valueOf(r)), -1l);
            return r;
        }
    }

    @Override
    public synchronized Double hIncrBy(byte[] key, byte[] field, double delta) {
        Slice sKey = new Slice(key);
        Slice sFiled = new Slice(field);
        Slice sDelta = new Slice(delta);
        Slice slice = base.hashGet(sKey).get(sFiled);
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null || map.get(sFiled) == null) {
            base.hashPut(sKey, sFiled, sDelta, -1l);
            return delta;
        } else {
            Double r = Long.valueOf(new String(slice.data())) + delta;
            base.hashPut(sKey, sFiled, new Slice(String.valueOf(r)), -1l);
            return r;
        }
    }

    @Override
    public synchronized Boolean hExists(byte[] key, byte[] field) {
        Slice sKey = new Slice(key);
        Slice sFiled = new Slice(field);
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null || map.get(sFiled) == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public synchronized Long hDel(byte[] key, byte[]... fields) {
        Slice sKey = new Slice(key);
        Map<Slice, Slice> map = base.hashGet(sKey);
        Long num = 0l;
        if (map == null) {
            return num;
        }
        for (byte[] field : fields) {
            Slice sFiled = new Slice(field);
            if (map.get(sFiled) != null) {
                map.remove(sFiled);
                num++;
            }
        }
        return num;
    }

    @Override
    public synchronized Long hLen(byte[] key) {
        Slice sKey = new Slice(key);
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null) {
            return 0l;
        }
        return Long.valueOf(map.size());
    }

    @Override
    public synchronized Set<byte[]> hKeys(byte[] key) {
        Slice sKey = new Slice(key);
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null) {
            return new HashSet<>();
        }
        return map.keySet().stream().map(e -> e.data()).collect(Collectors.toSet());
    }

    @Override
    public synchronized List<byte[]> hVals(byte[] key) {
        Slice sKey = new Slice(key);
        Map<Slice, Slice> map = base.hashGet(sKey);
        if (map == null) {
            return new ArrayList<>();
        }
        return map.values().stream().map(e -> e.data()).collect(Collectors.toList());
    }

    @Override
    public synchronized Map<byte[], byte[]> hGetAll(byte[] key) {
        Slice sKey = new Slice(key);
        Map<Slice, Slice> map = base.hashGet(sKey);
        Map<byte[], byte[]> res = new HashMap<>();
        if (map == null) {
            return res;
        }
        for (Slice sField : map.keySet()) {
            res.put(sField.data(), map.get(sField).data());
        }
        return res;
    }

    @Override
    public synchronized Cursor<Map.Entry<byte[], byte[]>> hScan(byte[] key, ScanOptions options) {
        return null;
    }

    @Override
    public Long hStrLen(byte[] key, byte[] field) {
        return null;
    }

    @Override
    public synchronized Boolean exists(byte[] key) {
        Slice sKey = new Slice(key);
        return base.getRedisObject(sKey) != null;
    }

    @Override
    public Long exists(byte[]... keys) {
        return null;
    }

    @Override
    public synchronized Long del(byte[]... keys) {
        Long num = 0l;
        for (byte[] key : keys) {
            Slice sKey = new Slice(key);
            num += del(sKey);
        }
        return num;
    }

    @Override
    public Long unlink(byte[]... keys) {
        return null;
    }

    private Long del(Slice key) {
        Long flag = base.getRedisObject(key) != null ? 1l : 0l;
        base.del(key);
        return flag;
    }

    @Override
    public synchronized DataType type(byte[] key) {
        Slice sKey = new Slice(key);
        RedisObject redisObject = base.getRedisObject(sKey);
        if (redisObject == null) {
            return DataType.NONE;
        } else {
            return redisObject.getType();
        }
    }

    @Override
    public Long touch(byte[]... keys) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> keys(byte[] pattern) {
        Set<byte[]> res = new HashSet<>();
        String regex = new String(pattern);
        regex = regex.replace("*", ".*");
        Set<Slice> set = base.keysSet();
        for (Slice key : set) {
            String str = new String(key.data());
            if (str.matches(regex)) {
                res.add(key.data());
            }
        }
        return res;
    }

    @Override
    public synchronized Cursor<byte[]> scan(ScanOptions options) {
        return null;
    }

    @Override
    public synchronized byte[] randomKey() {
        return new byte[0];
    }

    @Override
    public synchronized void rename(byte[] oldName, byte[] newName) {

    }

    @Override
    public synchronized Boolean renameNX(byte[] oldName, byte[] newName) {
        return null;
    }

    @Override
    public synchronized Boolean expire(byte[] key, long seconds) {
        Slice sKey = new Slice(key);
        if (base.getRedisObject(sKey) != null) {
            base.setTTL(sKey, seconds * 1000);
            return true;
        }
        return false;
    }

    @Override
    public synchronized Boolean pExpire(byte[] key, long millis) {
        Slice sKey = new Slice(key);
        if (base.getRedisObject(sKey) != null) {
            base.setTTL(sKey, millis);
            return true;
        }
        return false;
    }

    /**
     * Expireat 命令用于以 UNIX 时间戳(unix timestamp)格式设置 key 的过期时间。
     * key 过期后将不再可用。
     *
     * @param key
     * @param unixTime
     * @return
     */
    @Override
    public synchronized Boolean expireAt(byte[] key, long unixTime) {
        Slice sKey = new Slice(key);
        if (base.getRedisObject(sKey) != null) {
            base.setTTL(sKey, unixTime * 1000 - System.currentTimeMillis());
            return true;
        }
        return false;
    }


    /**
     * 这个命令和 expireat 命令类似，但它以毫秒为单位设置 key 的过期 unix 时间戳，
     * 而不是像 expireat 那样，以秒为单位。
     *
     * @param key
     * @param unixTimeInMillis
     * @return
     */
    @Override
    public synchronized Boolean pExpireAt(byte[] key, long unixTimeInMillis) {
        long time = unixTimeInMillis - System.currentTimeMillis();
        if (unixTimeInMillis < time) {
            return false;
        }
        return pExpire(key, time);
    }

    /**
     * 移除给定 key 的生存时间，
     * 将这个 key 从“易失的”(带生存时间 key )转换成“持久的”(一个不带生存时间、永不过期的 key )。
     *
     * @param key
     * @return
     */
    @Override
    public synchronized Boolean persist(byte[] key) {
        for (Slice k : base.keysSet()) {
            if (Arrays.equals(k.data(), key)) {
                base.getRedisObject(k).setTtl(-1L);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized Boolean move(byte[] key, int dbIndex) {
        return null;
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     *
     * @param key
     * @return
     */
    @Override
    public synchronized Long ttl(byte[] key) {
        for (Slice k : base.keysSet()) {
            if (Arrays.equals(k.data(), key)) {
                return base.getRedisObject(k).getTtl() / 1000;
            }
        }
        return 0L;
    }

    @Override
    public synchronized Long ttl(byte[] key, TimeUnit timeUnit) {
        return null;
    }

    /**
     * 这个命令类似于 TTL 命令，但它以毫秒为单位返回 key 的剩余生存时间，
     * 而不是像 TTL 命令那样，以秒为单位。
     *
     * @param key
     * @return
     */
    @Override
    public synchronized Long pTtl(byte[] key) {
        for (Slice k : base.keysSet()) {
            if (Arrays.equals(k.data(), key)) {
                return base.getRedisObject(k).getTtl();
            }
        }
        return 0L;
    }

    @Override
    public synchronized Long pTtl(byte[] key, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public synchronized List<byte[]> sort(byte[] key, SortParameters params) {
        return null;
    }

    @Override
    public synchronized Long sort(byte[] key, SortParameters params, byte[] storeKey) {
        return null;
    }

    @Override
    public synchronized byte[] dump(byte[] key) {
        return new byte[0];
    }

    @Override
    public synchronized void restore(byte[] key, long ttlInMillis, byte[] serializedValue) {

    }

    @Override
    public void restore(byte[] key, long ttlInMillis, byte[] serializedValue, boolean replace) {

    }

    @Override
    public ValueEncoding encodingOf(byte[] key) {
        return null;
    }

    @Override
    public Duration idletime(byte[] key) {
        return null;
    }

    @Override
    public Long refcount(byte[] key) {
        return null;
    }

    /**
     * Rpush 命令用于将一个或多个值插入到列表的尾部(最右边)。
     * 如果列表不存在，一个空列表会被创建并执行 RPUSH 操作。
     * 成功返回list里面的数量
     * 当列表存在但不是列表类型时，返回一个错误。
     *
     * @param key
     * @param values
     * @return
     */
    @Override
    public synchronized Long rPush(byte[] key, byte[]... values) {
        Slice sKey = new Slice(key);
        for (byte[] value : values) {
            Slice sValue = new Slice(value);
            try {
                base.listRPut(sKey, sValue, -1l);
            } catch (InvalidDataAccessApiUsageException e) {
                break;
            }
        }
        return (long) base.listGet(sKey).size();
    }

    /**
     * Lpush 命令将一个或多个值插入到列表头部。
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     * 成功返回list里面的数量
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key
     * @param values
     * @return
     */
    @Override
    public synchronized Long lPush(byte[] key, byte[]... values) {
        Slice sKey = new Slice(key);
        for (byte[] value : values) {
            Slice sValue = new Slice(value);
            try {
                base.listLPut(sKey, sValue, -1l);
            } catch (InvalidDataAccessApiUsageException e) {
                break;
            }
        }
        return (long) base.listGet(sKey).size();
    }

    /**
     * Rpushx 命令用于将一个值插入到已存在的列表尾部(最右边)。
     * 如果列表不存在，操作无效。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public synchronized Long rPushX(byte[] key, byte[] value) {
        Slice sKey = new Slice(key);
        LinkedList<Slice> list = base.listGet(sKey);
        if (list == null) {
            return 0l;
        }
        Slice sValue = new Slice(value);
        list.addLast(sValue);
        return (long) list.size();
    }

    @Override
    public synchronized Long lPushX(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public synchronized Long lLen(byte[] key) {
        return null;
    }

    /**
     * Lrange 返回列表中指定区间内的元素，区间以偏移量 START 和 END 指定。
     * 其中 0 表示列表的第一个元素， 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素，
     * -2 表示列表的倒数第二个元素，以此类推。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public synchronized List<byte[]> lRange(byte[] key, long start, long end) {
        Slice sKey = new Slice(key);
        LinkedList<Slice> list = base.listGet(sKey);
        if (start < 0) {
            start = list.size() + start;
            if (start < 0) {
                start = 0;
            }
        }
        if (end < 0) {
            end = list.size() + end;
            if (end < 0) {
                end = 0;
            }
        }
        List<byte[]> res = new ArrayList<>();
        for (int i = (int) start; i <= end && i < list.size(); i++) {
            res.add(list.get(i).data());
        }
        return res;
    }

    @Override
    public synchronized void lTrim(byte[] key, long start, long end) {

    }

    @Override
    public synchronized byte[] lIndex(byte[] key, long index) {
        return new byte[0];
    }

    @Override
    public synchronized Long lInsert(byte[] key, Position where, byte[] pivot, byte[] value) {
        return null;
    }

    @Override
    public synchronized void lSet(byte[] key, long index, byte[] value) {

    }

    @Override
    public synchronized Long lRem(byte[] key, long count, byte[] value) {
        return null;
    }

    @Override
    public synchronized byte[] lPop(byte[] key) {
        return new byte[0];
    }

    @Override
    public synchronized byte[] rPop(byte[] key) {
        return new byte[0];
    }

    @Override
    public synchronized List<byte[]> bLPop(int timeout, byte[]... keys) {
        return null;
    }

    @Override
    public synchronized List<byte[]> bRPop(int timeout, byte[]... keys) {
        return null;
    }

    @Override
    public synchronized byte[] rPopLPush(byte[] srcKey, byte[] dstKey) {
        return new byte[0];
    }

    @Override
    public synchronized byte[] bRPopLPush(int timeout, byte[] srcKey, byte[] dstKey) {
        return new byte[0];
    }

    @Override
    public synchronized boolean isSubscribed() {
        return false;
    }

    @Override
    public synchronized Subscription getSubscription() {
        return null;
    }

    @Override
    public synchronized Long publish(byte[] channel, byte[] message) {
        return null;
    }

    @Override
    public synchronized void subscribe(MessageListener listener, byte[]... channels) {

    }

    @Override
    public synchronized void pSubscribe(MessageListener listener, byte[]... patterns) {

    }

    @Override
    public synchronized void scriptFlush() {

    }

    @Override
    public synchronized void scriptKill() {

    }

    @Override
    public synchronized String scriptLoad(byte[] script) {
        return null;
    }

    @Override
    public synchronized List<Boolean> scriptExists(String... scriptShas) {
        return null;
    }

    @Override
    public synchronized <T> T eval(byte[] script, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return null;
    }

    @Override
    public synchronized <T> T evalSha(String scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return null;
    }

    @Override
    public synchronized <T> T evalSha(byte[] scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return null;
    }

    @Override
    public synchronized void bgWriteAof() {

    }

    @Override
    public synchronized void bgReWriteAof() {

    }

    @Override
    public synchronized void bgSave() {

    }

    @Override
    public synchronized Long lastSave() {
        return null;
    }

    @Override
    public synchronized void save() {

    }

    @Override
    public synchronized Long dbSize() {
        return null;
    }

    @Override
    public synchronized void flushDb() {

    }

    @Override
    public synchronized void flushAll() {

    }

    @Override
    public synchronized Properties info() {
        return null;
    }

    @Override
    public synchronized Properties info(String section) {
        return null;
    }

    @Override
    public synchronized void shutdown() {

    }

    @Override
    public synchronized void shutdown(ShutdownOption option) {

    }

    @Override
    public Properties getConfig(String s) {
        return null;
    }

    @Override
    public void setConfig(String s, String s1) {

    }

    @Override
    public synchronized void resetConfigStats() {

    }

    @Override
    public synchronized Long time() {
        return null;
    }

    @Override
    public synchronized void killClient(String host, int port) {

    }

    @Override
    public synchronized void setClientName(byte[] name) {

    }

    @Override
    public synchronized String getClientName() {
        return null;
    }

    @Override
    public synchronized List<RedisClientInfo> getClientList() {
        return null;
    }

    @Override
    public synchronized void slaveOf(String host, int port) {

    }

    @Override
    public synchronized void slaveOfNoOne() {

    }

    @Override
    public synchronized void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option) {

    }

    @Override
    public synchronized void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option, long timeout) {

    }

    /**
     * 集合
     **/
    @Override
    public synchronized Long sAdd(byte[] key, byte[]... values) {
        for (byte[] value : values) {
            Slice k = new Slice(key);
            Slice v = new Slice(value);
            base.setPut(k, v, -1L);
        }
        return (long) keys(key).size();
    }

    @Override
    public synchronized Long sRem(byte[] key, byte[]... values) {
        Slice k = new Slice(key);
        RedisObject redisObject = base.getRedisObject(k);
        if (null != redisObject) {
            HashSet<Slice> hashSet = (HashSet) redisObject.getData();
            int originalSize = hashSet.size();
            hashSet.removeAll(Arrays.asList(values));
            return (long) originalSize - hashSet.size();
        }
        return 0L;
    }

    @Override
    public synchronized byte[] sPop(byte[] key) {
        Slice k = new Slice(key);
        RedisObject redisObject = base.getRedisObject(k);
        if (null != redisObject) {
            HashSet<Slice> hashSet = (HashSet) redisObject.getData();
            if (hashSet.size() > 0) {
                Iterator<Slice> iterator = hashSet.iterator();
                int randomNum = new Random().nextInt(hashSet.size());
                int count = 0;
                while (iterator.hasNext()) {
                    if (count < randomNum) {
                        count++;
                        iterator.next();
                        continue;
                    }
                    Slice removeSlice = iterator.next();
                    byte[] result = removeSlice.data();
                    hashSet.remove(removeSlice);
                    return result;
                }
            }
        }
        return new byte[]{};
    }

    @Override
    public List<byte[]> sPop(byte[] key, long count) {
        return null;
    }

    @Override
    public synchronized Boolean sMove(byte[] srcKey, byte[] destKey, byte[] value) {
        return null;
    }

    /**
     * 返回集合 key 的基数(集合中元素的数量)。
     *
     * @param key
     * @return
     */
    @Override
    public synchronized Long sCard(byte[] key) {
        Slice k = new Slice(key);
        RedisObject redisObject = base.getRedisObject(k);
        if (null != redisObject) {
            HashSet h = (HashSet<Slice>) redisObject.getData();
            return (long) h.size();
        }
        return 0L;
    }

    @Override
    public synchronized Boolean sIsMember(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> sInter(byte[]... keys) {
        return null;
    }

    @Override
    public synchronized Long sInterStore(byte[] destKey, byte[]... keys) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> sUnion(byte[]... keys) {
        return null;
    }

    @Override
    public synchronized Long sUnionStore(byte[] destKey, byte[]... keys) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> sDiff(byte[]... keys) {
        return null;
    }

    @Override
    public synchronized Long sDiffStore(byte[] destKey, byte[]... keys) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> sMembers(byte[] key) {
        Slice k = new Slice(key);
        RedisObject redisObject = base.getRedisObject(k);
        HashSet<byte[]> hashSet = new HashSet<>();
        if (redisObject != null) {
            HashSet<Slice> data = (HashSet) redisObject.getData();
            data.stream().forEach(d -> {
                hashSet.add(d.data());
            });
        }
        return hashSet;
    }

    @Override
    public synchronized byte[] sRandMember(byte[] key) {
        Slice k = new Slice(key);
        RedisObject redisObject = base.getRedisObject(k);
        if (null != redisObject) {
            HashSet<Slice> hashSet = (HashSet) redisObject.getData();
            if (hashSet.size() > 0) {
                Iterator<Slice> iterator = hashSet.iterator();
                int randomNum = new Random().nextInt(hashSet.size());
                int count = 0;
                while (iterator.hasNext()) {
                    if (count < randomNum) {
                        count++;
                        iterator.next();
                        continue;
                    }
                    return iterator.next().data();
                }
            }
        }
        return new byte[]{};
    }

    @Override
    public synchronized List<byte[]> sRandMember(byte[] key, long count) {
        return null;
    }

    @Override
    public synchronized Cursor<byte[]> sScan(byte[] key, ScanOptions options) {
        return null;
    }

    @Override
    public synchronized byte[] get(byte[] key) {
        Slice sKey = new Slice(key);
        if (base.stringGet(sKey) != null) {
            return base.stringGet(sKey).data();
        } else {
            return new byte[0];
        }

    }

    @Override
    public synchronized byte[] getSet(byte[] key, byte[] value) {
        Slice sKey = new Slice(key);
        Slice sValue = new Slice(value);
        Slice slice = base.stringGet(sKey);
        base.stringPut(sKey, sValue, -1l);
        if (slice != null) {
            return slice.data();
        }
        return new byte[0];
    }

    @Override
    public synchronized List<byte[]> mGet(byte[]... keys) {
        List<byte[]> res = new ArrayList<>();
        for (byte[] key : keys) {
            Slice sKey = new Slice(key);
            if (base.stringGet(sKey) != null) {
                res.add(base.stringGet(sKey).data());
            }
        }
        return res;
    }

    @Override
    public synchronized Boolean set(byte[] key, byte[] value) {
        Slice sKey = new Slice(key);
        Slice sValue = new Slice(value);
        base.stringPut(sKey, sValue, -1l);
        return true;
    }

    @Override
    public synchronized Boolean set(byte[] key, byte[] value, Expiration expiration, SetOption option) {
        return true;
    }

    @Override
    public synchronized Boolean setNX(byte[] key, byte[] value) {
        Slice sKey = new Slice(key);
        Slice sValue = new Slice(value);
        if (base.stringGet(sKey) == null) {
            base.stringPut(sKey, sValue, -1l);
            return true;
        }
        return false;
    }

    @Override
    public synchronized Boolean setEx(byte[] key, long seconds, byte[] value) {
        Slice sKey = new Slice(key);
        Slice sValue = new Slice(value);
        base.stringPut(sKey, sValue, seconds * 1000);
        return true;
    }

    @Override
    public synchronized Boolean pSetEx(byte[] key, long milliseconds, byte[] value) {
        Slice sKey = new Slice(key);
        Slice sValue = new Slice(value);
        base.stringPut(sKey, sValue, milliseconds);
        return true;
    }

    @Override
    public synchronized Boolean mSet(Map<byte[], byte[]> tuple) {
        for (byte[] key : tuple.keySet()) {
            Slice sKey = new Slice(key);
            Slice sValue = new Slice(tuple.get(key));
            base.stringPut(sKey, sValue, -1l);
        }
        return true;
    }

    @Override
    public synchronized Boolean mSetNX(Map<byte[], byte[]> tuple) {
        for (byte[] key : tuple.keySet()) {
            Slice sKey = new Slice(key);
            if (base.stringGet(sKey) != null) {
                return false;
            }
        }
        for (byte[] key : tuple.keySet()) {
            Slice sKey = new Slice(key);
            Slice sValue = new Slice(tuple.get(key));
            base.stringPut(sKey, sValue, -1l);
        }
        return true;
    }

    @Override
    public synchronized Long incr(byte[] key) {
        return incrBy(key, 1l);
    }

    @Override
    public synchronized Long incrBy(byte[] key, long value) {
        Slice sKey = new Slice(key);
        Slice slice = base.stringGet(sKey);
        if (slice == null) {
            base.stringPut(sKey, new Slice(value), -1l);
            return value;
        } else {
            long l = Long.valueOf(new String(slice.data())) + value;
            base.stringPut(sKey, new Slice(l), -1l);
            return l;
        }
    }

    @Override
    public synchronized Double incrBy(byte[] key, double value) {
        Slice sKey = new Slice(key);
        Slice slice = base.stringGet(sKey);
        if (slice == null) {
            base.stringPut(sKey, new Slice(value), -1l);
            return value;
        } else {
            double d = Long.valueOf(new String(slice.data())) + value;
            base.stringPut(sKey, new Slice(d), -1l);
            return d;
        }
    }

    @Override
    public synchronized Long decr(byte[] key) {
        return decrBy(key, -1l);
    }

    @Override
    public synchronized Long decrBy(byte[] key, long value) {
        Slice sKey = new Slice(key);
        Slice slice = base.stringGet(sKey);
        if (slice == null) {
            base.stringPut(sKey, new Slice(value), -1l);
            return value;
        } else {
            long l = Long.valueOf(new String(slice.data())) - value;
            base.stringPut(sKey, new Slice(l), -1l);
            return l;
        }
    }

    @Override
    public synchronized Long append(byte[] key, byte[] value) {
        Slice sKey = new Slice(key);
        Slice sValue = new Slice(value);
        Slice slice = base.stringGet(sKey);
        if (slice == null) {
            base.stringPut(sKey, sValue, -1l);
            return Long.valueOf(value.length);
        }
        byte[] b = new byte[slice.length() + sValue.length()];
        for (int i = 0; i < slice.length(); i++) {
            b[i] = slice.data()[i];
        }
        for (int i = slice.length(); i < slice.length() + sValue.length(); i++) {
            b[i] = sValue.data()[i - slice.length()];
        }
        base.stringPut(sKey, new Slice(b), -1l);
        return Long.valueOf(b.length);
    }

    @Override
    public synchronized byte[] getRange(byte[] key, long begin, long end) {
        return new byte[0];
    }

    @Override
    public synchronized void setRange(byte[] key, byte[] value, long offset) {

    }

    @Override
    public synchronized Boolean getBit(byte[] key, long offset) {
        return null;
    }

    @Override
    public synchronized Boolean setBit(byte[] key, long offset, boolean value) {
        return null;
    }

    @Override
    public synchronized Long bitCount(byte[] key) {
        return null;
    }

    @Override
    public synchronized Long bitCount(byte[] key, long begin, long end) {
        return null;
    }

    @Override
    public List<Long> bitField(byte[] bytes, BitFieldSubCommands bitFieldSubCommands) {
        return null;
    }

    @Override
    public synchronized Long bitOp(BitOperation op, byte[] destination, byte[]... keys) {
        return null;
    }

    @Override
    public Long bitPos(byte[] bytes, boolean b, org.springframework.data.domain.Range<Long> range) {
        return null;
    }

    @Override
    public synchronized Long strLen(byte[] key) {
        return null;
    }

    @Override
    public synchronized void multi() {

    }

    @Override
    public synchronized List<Object> exec() {
        return null;
    }

    @Override
    public synchronized void discard() {

    }

    @Override
    public synchronized void watch(byte[]... keys) {

    }

    @Override
    public synchronized void unwatch() {

    }

    @Override
    public synchronized Boolean zAdd(byte[] key, double score, byte[] value) {
        return null;
    }

    @Override
    public synchronized Long zAdd(byte[] key, Set<Tuple> tuples) {
        return null;
    }


    @Override
    public synchronized Long zRem(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public synchronized Double zIncrBy(byte[] key, double increment, byte[] value) {
        return null;
    }

    @Override
    public synchronized Long zRank(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public synchronized Long zRevRank(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRange(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRangeWithScores(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRangeByScore(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRangeByScoreWithScores(byte[] key, Range range) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRangeByScoreWithScores(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRangeByScore(byte[] key, double min, double max, long offset, long count) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRangeByScoreWithScores(byte[] key, double min, double max, long offset, long count) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRangeByScoreWithScores(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRevRange(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRevRangeWithScores(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRevRangeByScore(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRevRangeByScore(byte[] key, Range range) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRevRangeByScoreWithScores(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRevRangeByScore(byte[] key, double min, double max, long offset, long count) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRevRangeByScore(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRevRangeByScoreWithScores(byte[] key, double min, double max, long offset, long count) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRevRangeByScoreWithScores(byte[] key, Range range) {
        return null;
    }

    @Override
    public synchronized Set<Tuple> zRevRangeByScoreWithScores(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public synchronized Long zCount(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public synchronized Long zCount(byte[] key, Range range) {
        return null;
    }

    @Override
    public synchronized Long zCard(byte[] key) {
        return null;
    }

    @Override
    public synchronized Double zScore(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public synchronized Long zRemRange(byte[] key, long start, long end) {
        return null;
    }

    @Override
    public synchronized Long zRemRangeByScore(byte[] key, double min, double max) {
        return null;
    }

    @Override
    public synchronized Long zRemRangeByScore(byte[] key, Range range) {
        return null;
    }

    @Override
    public synchronized Long zUnionStore(byte[] destKey, byte[]... sets) {
        return null;
    }

    @Override
    public synchronized Long zUnionStore(byte[] destKey, Aggregate aggregate, int[] weights, byte[]... sets) {
        return null;
    }

    @Override
    public Long zUnionStore(byte[] bytes, Aggregate aggregate, Weights weights, byte[]... bytes1) {
        return null;
    }

    @Override
    public synchronized Long zInterStore(byte[] destKey, byte[]... sets) {
        return null;
    }

    @Override
    public synchronized Long zInterStore(byte[] destKey, Aggregate aggregate, int[] weights, byte[]... sets) {
        return null;
    }

    @Override
    public Long zInterStore(byte[] bytes, Aggregate aggregate, Weights weights, byte[]... bytes1) {
        return null;
    }

    @Override
    public synchronized Cursor<Tuple> zScan(byte[] key, ScanOptions options) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRangeByScore(byte[] key, String min, String max) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRangeByScore(byte[] key, Range range) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRangeByScore(byte[] key, String min, String max, long offset, long count) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRangeByScore(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRangeByLex(byte[] key) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRangeByLex(byte[] key, Range range) {
        return null;
    }

    @Override
    public synchronized Set<byte[]> zRangeByLex(byte[] key, Range range, Limit limit) {
        return null;
    }

    @Override
    public synchronized Long pfAdd(byte[] key, byte[]... values) {
        return null;
    }

    @Override
    public synchronized Long pfCount(byte[]... keys) {
        return null;
    }

    @Override
    public synchronized void pfMerge(byte[] destinationKey, byte[]... sourceKeys) {

    }

    @Override
    public synchronized void select(int dbIndex) {

    }

    @Override
    public synchronized byte[] echo(byte[] message) {
        return new byte[0];
    }

    @Override
    public synchronized String ping() {
        return null;
    }

    @Override
    public synchronized Long geoAdd(byte[] key, Point point, byte[] member) {
        return null;
    }

    @Override
    public synchronized Long geoAdd(byte[] key, GeoLocation<byte[]> location) {
        return null;
    }

    @Override
    public synchronized Long geoAdd(byte[] key, Map<byte[], Point> memberCoordinateMap) {
        return null;
    }

    @Override
    public synchronized Long geoAdd(byte[] key, Iterable<GeoLocation<byte[]>> locations) {
        return null;
    }

    @Override
    public synchronized Distance geoDist(byte[] key, byte[] member1, byte[] member2) {
        return null;
    }

    @Override
    public synchronized Distance geoDist(byte[] key, byte[] member1, byte[] member2, Metric metric) {
        return null;
    }

    @Override
    public synchronized List<String> geoHash(byte[] key, byte[]... members) {
        return null;
    }

    @Override
    public synchronized List<Point> geoPos(byte[] key, byte[]... members) {
        return null;
    }

    @Override
    public synchronized GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within) {
        return null;
    }

    @Override
    public synchronized GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within, GeoRadiusCommandArgs args) {
        return null;
    }

    @Override
    public synchronized GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, double radius) {
        return null;
    }

    @Override
    public synchronized GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius) {
        return null;
    }

    @Override
    public synchronized GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius, GeoRadiusCommandArgs args) {
        return null;
    }

    @Override
    public synchronized Long geoRemove(byte[] key, byte[]... members) {
        return null;
    }

    @Override
    public Long xAck(byte[] key, String group, RecordId... recordIds) {
        return null;
    }

    @Override
    public RecordId xAdd(MapRecord<byte[], byte[], byte[]> record) {
        return null;
    }

    @Override
    public Long xDel(byte[] key, RecordId... recordIds) {
        return null;
    }

    @Override
    public String xGroupCreate(byte[] key, String groupName, ReadOffset readOffset) {
        return null;
    }

    @Override
    public Boolean xGroupDelConsumer(byte[] key, Consumer consumer) {
        return null;
    }

    @Override
    public Boolean xGroupDestroy(byte[] key, String groupName) {
        return null;
    }

    @Override
    public Long xLen(byte[] key) {
        return null;
    }

    @Override
    public List<ByteRecord> xRange(byte[] key, org.springframework.data.domain.Range<String> range, Limit limit) {
        return null;
    }

    @Override
    public List<ByteRecord> xRead(StreamReadOptions readOptions, StreamOffset<byte[]>... streams) {
        return null;
    }

    @Override
    public List<ByteRecord> xReadGroup(Consumer consumer, StreamReadOptions readOptions, StreamOffset<byte[]>... streams) {
        return null;
    }

    @Override
    public List<ByteRecord> xRevRange(byte[] key, org.springframework.data.domain.Range<String> range, Limit limit) {
        return null;
    }

    @Override
    public Long xTrim(byte[] key, long count) {
        return null;
    }
}
