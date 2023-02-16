package com.hitebaas.utils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;



@Component
public class RedisUtils {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
	
	
    
    
    
    
	public boolean set(String key, String value) {
		try {
        	stringRedisTemplate.opsForValue().set(key, value);
        	
        	return true;
		} catch (Exception e) {
			e.printStackTrace();
			
           	return false;
    	}
	}
	
	public boolean set(String key, String value, long time, TimeUnit timeUnit){
		try {
			if(time>0){
            	stringRedisTemplate.opsForValue().set(key, value, time, timeUnit);  
			}else{
                set(key, value);
			}
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            
            return false;
        }
    }
	
	public String get(String key){ 
        return key==null?"":stringRedisTemplate.opsForValue().get(key);
    }
	
	
	
	
	
	
	
	
	
	
	
	
    
    public void set1(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public void setWithExpire(String key, Object value, Long seconds, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, seconds, timeUnit);
    }
    
    public Object get1(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public void del(String key) {
        redisTemplate.delete(key);
    }
    
    public void batchDel(Collection<String> keys) {
        redisTemplate.delete(keys);
    }
    
    public byte[] dump(String key) {
        return redisTemplate.dump(key);
    }
    
    public Boolean expire(String key, long seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }
    
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }
    
    public Set<String> getStringKeys(String key) {
        return redisTemplate.keys(key);
    }
    
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }
    
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }
    
    public DataType type(String key) {
        return redisTemplate.type(key);
    }
    
    public String randomKey() {
        return redisTemplate.randomKey();
    }
    
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }
    
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }
    
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }
    
    public Boolean move(String key, int dbIndex) {
        return redisTemplate.move(key, dbIndex);
    }
    
    public String subString(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }
    
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }
    
    public List<Object> multiGetSet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }
    
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }
    
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }
    
    public Long incrementLong(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }
    
    public void incrementDouble(String key, double increment) {
        redisTemplate.opsForValue().increment(key, increment);
    }
    
    public Boolean multiSetIfAbsent(Map<? extends String, ?> map) {
        return redisTemplate.opsForValue().multiSetIfAbsent(map);
    }
    
    public void multiSet(Map<String, String> map) {
        redisTemplate.opsForValue().multiSet(map);
    }
    
    public List<Object> multiGet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }
    
    public Long sizeString(String key) {
        return redisTemplate.opsForValue().size(key);
    }
    
    public void offsetValue(String key, Object value, Long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }
    
    public Boolean getOffsetValue(String key, Long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }
    
    public Boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }
    
    
   
    public void put(String key, Object field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
    
    public void putAll(String key, Map<Object, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }
    
    public Object getMapValue(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }
    
    public Map<Object, Object> getMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    public Boolean putIfAbsent(String key, Object hashKey, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }
    
    public Long del(String key, List<Object> fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }
    
    public Boolean hasKey(String key, Object field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }
    
    public Long incrementLong(String key, Object field, long increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }
    
    public Double incrementDouble(String key, Object field, double increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }
    
    public Set<Object> keys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }
    
    public Long sizeHash(String key) {
        return redisTemplate.opsForHash().size(key);
    }
    
    public List<Object> values(String key) {
        return redisTemplate.opsForHash().values(key);
    }
    
    public Cursor<Map.Entry<Object, Object>> scan(String key, ScanOptions scanOptions) {
        return redisTemplate.opsForHash().scan(key, scanOptions);
    }
    
    
    public Long leftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }
    
    public Long leftPush(String key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }
    
    public Long leftPushAll(String key, List<Object> value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }
    
    public long leftPushIfPresent(String key, Object value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }
    
    public long rightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }
    
    public long rightPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }
    
    public long rightPushAll(String key, List<Object> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }
    
    public Object index(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }
    
    public List<Object> range(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
    
    public Object leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
    
    public Object leftPop(String key, Long seconds, TimeUnit timeUnit) {
        return redisTemplate.opsForList().leftPop(key, seconds, timeUnit);
    }
    
    public Object rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
    
    public Object rightPop(String key, Long seconds, TimeUnit timeUnit) {
        return redisTemplate.opsForList().rightPop(key, seconds, timeUnit);
    }
    
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
    }
    
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey, long seconds, TimeUnit timeUnit) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, seconds, timeUnit);
    }
    
    public Long sizeList(String key) {
        return redisTemplate.opsForList().size(key);
    }
    
    public void trim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }
    
    public Long remove(String key, long index, Object value) {
        return redisTemplate.opsForList().remove(key, index, value);
    }
    
    
    public Long add(String key, Collection<Object> values) {
        return redisTemplate.opsForSet().add(key, values);
    }
    
    public Object pop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }
    
    public Long sizeSet(String key) {
        return redisTemplate.opsForSet().size(key);
    }
    
    public Boolean isMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    
    public Set<Object> intersect(String key, String otherKey) {
        return redisTemplate.opsForSet().intersect(key, otherKey);
    }
    
    public Set<Object> intersect(String key, Collection<String> collection) {
        return redisTemplate.opsForSet().intersect(key, collection);
    }
    
    public Long intersectAndStore(String key1, String key2, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key1, key2, destKey);
    }
    
    public Set<Object> union(String key, String key1) {
        return redisTemplate.opsForSet().union(key, key1);
    }
    
    public Set<Object> union(String key, Collection<String> collection) {
        return redisTemplate.opsForSet().union(key, collection);
    }
    
    public Long unionAndStore(String key, String key1, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, key1, destKey);
    }
    
    public Set<Object> difference(String key, String key1) {
        return redisTemplate.opsForSet().difference(key, key1);
    }
    
    public Set<Object> difference(String key, Collection<String> collection) {
        return redisTemplate.opsForSet().difference(key, collection);
    }
    
    public Long differenceAndStore(String key, String key1, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, key1, destKey);
    }
    
    public Set<Object> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }
    
    public Object randomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }
    
    public List<Object> randomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }
    
    public Set<Object> distinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }
    
    public Cursor<Object> scanSet(String key, ScanOptions scanOptions) {
        return redisTemplate.opsForSet().scan(key, scanOptions);
    }
    
    public Long remove(String key, Collection<Object> objects) {
        return redisTemplate.opsForSet().remove(key, objects);
    }
    
    
    public Boolean add(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }
    
    public Double incrementScore(String key, Object value, double score) {
        return redisTemplate.opsForZSet().incrementScore(key, value, score);
    }
    
    public Long rank(String key, Object object) {
        return redisTemplate.opsForZSet().rank(key, object);
    }
    
    public Long reverseRank(String key, Object object) {
        return redisTemplate.opsForZSet().reverseRank(key, object);
    }
    
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }
    
    public Set<Object> reverseRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }
    
    public Set<Object> reverseRangeByScore(String key, double min, double max, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, start, end);
    }
    
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }
    
    public Long count(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }
    
    public Long sizeZset(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
    
    public Long zCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }
    
    public Double score(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }
    
    public Long removeRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }
    
    public Long removeRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }
    
    public Long zSetUnionAndStore(String key, String key1, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, key1, destKey);
    }
    
    public Long zSetUnionAndStore(String key, Collection<String> collection, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, collection, destKey);
    }
    
    public Long zSetIntersectAndStore(String key, String key1, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, key1, destKey);
    }
    
    public Long zSetIntersectAndStore(String key, Collection<String> collection, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, collection, destKey);
    }
    
    public Long remove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }
	
	
	
	
	
	
	
	
	
	
}