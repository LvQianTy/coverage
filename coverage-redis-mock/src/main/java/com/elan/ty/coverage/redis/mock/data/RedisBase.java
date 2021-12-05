package com.elan.ty.coverage.redis.mock.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.DataType;

public class RedisBase {
    private final Map<Slice, RedisObject> data = new HashMap<>();

    public void clear() {
        data.clear();
    }

    public void del(Slice key) {
        data.remove(key);
    }

    public void stringPut(Slice key, Slice value, Long ttl) {
        RedisObject redisObject = new RedisObject();
        redisObject.setType(DataType.STRING);
        if (ttl == -1) {
            redisObject.setTtl(ttl);
        } else {
            redisObject.setTtl(System.currentTimeMillis() + ttl);

        }
        redisObject.setData(value);
        data.put(key, redisObject);
    }

    public Slice stringGet(Slice key) {
        RedisObject redisObject = data.get(key);
        if (redisObject != null && redisObject.getTtl() != -1
                && redisObject.getTtl() <= System.currentTimeMillis()) {
            data.remove(key);
            return null;
        }
        if(redisObject == null){
            return null;
        }
        if (!redisObject.getType().equals(DataType.STRING)) {
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        return (Slice) redisObject.getData();
    }

    public Map<Slice, Slice> hashGet(Slice key) {
        RedisObject redisObject = data.get(key);
        if (redisObject != null && redisObject.getTtl() != -1
                && redisObject.getTtl() <= System.currentTimeMillis()) {
            data.remove(key);
            return null;
        }
        if (redisObject == null) {
            return null;
        }
        if (!redisObject.getType().equals(DataType.HASH)) {
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        return (Map) redisObject.getData();
    }

    public void hashPut(Slice key, Slice field, Slice value, Long time) {
        RedisObject redisObject = data.get(key);
        if(redisObject != null && redisObject.getType() != DataType.HASH){
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        if (redisObject == null) {
            Map<Slice, Slice> map = new HashMap<>();
            redisObject = new RedisObject();
            redisObject.setData(map);

        }
        if(time == -1){
            redisObject.setTtl(time);
        }else{
            redisObject.setTtl(System.currentTimeMillis() + time);

        }
        redisObject.setType(DataType.HASH);
        Map<Slice, Slice> map = (Map) redisObject.getData();
        map.put(field, value);
        data.put(key, redisObject);
    }

    public LinkedList<Slice> listGet(Slice key) {
        RedisObject redisObject = data.get(key);
        if (redisObject != null && redisObject.getTtl() != -1
                && redisObject.getTtl() <= System.currentTimeMillis()) {
            data.remove(key);
            return null;
        }
        if (redisObject == null) {
            return null;
        }
        if (!redisObject.getType().equals(DataType.LIST)) {
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        return (LinkedList) redisObject.getData();
    }

    public void listLPut(Slice key, Slice value, Long time) {
        RedisObject redisObject = data.get(key);
        if(redisObject != null && redisObject.getType() != DataType.LIST){
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        if (redisObject == null) {
            LinkedList<Slice> list = new LinkedList<>();
            redisObject = new RedisObject();
            redisObject.setData(list);
        }
        if (time == -1) {
            redisObject.setTtl(time);
        } else {
            redisObject.setTtl(System.currentTimeMillis() + time);

        }
        redisObject.setType(DataType.LIST);
        LinkedList<Slice> list = (LinkedList) redisObject.getData();
        list.addFirst(value);
        data.put(key, redisObject);
    }

    public void listRPut(Slice key, Slice value, Long time) {
        RedisObject redisObject = data.get(key);
        if(redisObject != null && redisObject.getType() != DataType.LIST){
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        if (redisObject == null) {
            LinkedList<Slice> list = new LinkedList<>();
            redisObject = new RedisObject();
            redisObject.setData(list);
        }
        if (time == -1) {
            redisObject.setTtl(time);
        } else {
            redisObject.setTtl(System.currentTimeMillis() + time);

        }
        redisObject.setType(DataType.LIST);
        LinkedList<Slice> list = (LinkedList) redisObject.getData();
        list.addLast(value);
        data.put(key, redisObject);
    }

    public Set<Slice> setGet(Slice key) {
        RedisObject redisObject = data.get(key);
        if (redisObject != null && redisObject.getTtl() != -1
                && redisObject.getTtl() <= System.currentTimeMillis()) {
            data.remove(key);
            return null;
        }
        if (redisObject == null) {
            return null;
        }
        if (!redisObject.getType().equals(DataType.SET)) {
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        return (Set) redisObject.getData();
    }

    public void setPut(Slice key, Slice value, Long time) {
        RedisObject redisObject = data.get(key);
        if(redisObject != null && redisObject.getType() != DataType.SET){
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        if (redisObject == null) {
            HashSet<Slice> set = new HashSet<>();
            redisObject = new RedisObject();
            redisObject.setData(set);
        }
        if (time == -1) {
            redisObject.setTtl(time);
        } else {
            redisObject.setTtl(System.currentTimeMillis() + time);

        }
        redisObject.setType(DataType.SET);
        HashSet<Slice> set = (HashSet) redisObject.getData();
        set.add(value);
        data.put(key, redisObject);
    }

    public SortedSet<Slice> zSetGet(Slice key) {
        RedisObject redisObject = data.get(key);
        if (redisObject != null && redisObject.getTtl() != -1
                && redisObject.getTtl() <= System.currentTimeMillis()) {
            data.remove(key);
            return null;
        }
        if (redisObject == null) {
            return null;
        }
        if (!redisObject.getType().equals(DataType.ZSET)) {
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        return (SortedSet) redisObject.getData();
    }

    public void zSetPut(Slice key, Slice value, Long time) {
        RedisObject redisObject = data.get(key);
        if(redisObject != null && redisObject.getType() != DataType.ZSET){
            throw new InvalidDataAccessApiUsageException("WRONGTYPE "
                    + "Operation against a key holding the wrong kind of value");
        }
        if (redisObject == null) {
            TreeSet<Slice> zSet = new TreeSet<>();
            redisObject = new RedisObject();
            redisObject.setData(zSet);
        }
        if (time == -1) {
            redisObject.setTtl(time);
        } else {
            redisObject.setTtl(System.currentTimeMillis() + time);

        }
        redisObject.setType(DataType.ZSET);
        TreeSet<Slice> zSet = (TreeSet) redisObject.getData();
        zSet.add(value);
        data.put(key, redisObject);
    }

    public long setTTL(Slice key, long ttl) {
        RedisObject redisObject = data.get(key);
        if (redisObject != null) {
            if (redisObject.getTtl() != -1) {
                if (redisObject.getTtl() <= System.currentTimeMillis()) {
                    data.remove(key);
                    return 0L;
                }
                redisObject.setTtl(ttl + System.currentTimeMillis());
            }
            return 1l;
        }
        return 0L;
    }

    public Set<Slice> keysSet() {
        for (Slice key : data.keySet()) {
            RedisObject redisObject = data.get(key);
            if (redisObject != null && redisObject.getTtl() != -1
                    && redisObject.getTtl() <= System.currentTimeMillis()) {
                data.remove(key);
            }
        }
        return data.keySet();
    }

    public RedisObject getRedisObject(Slice key) {
        return data.get(key);
    }
}
