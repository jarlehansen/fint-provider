package no.fint.provider.eventstate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@Repository
public class RedisRepository {

    @Value("${fint.provider.eventstate.hashmap-name:event-state}")
    private String key;

    private RedisTemplate<String, EventState> redisTemplate;
    private HashOperations hashOps;

    @Autowired
    private RedisRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisRepository() {

    }

    @PostConstruct
    private void init() {
        hashOps = redisTemplate.opsForHash();
    }

    public Boolean exists(String corrId) {
        return hashOps.hasKey(key, corrId);
    }

    public void add(EventState eventState) {
        hashOps.put(key, eventState.getEvent().getCorrId(), eventState);
    }

    public void remove(String corrId) {
        hashOps.delete(key, corrId);
    }

    public Map<String, EventState> getMap() {
        return hashOps.entries(key);
    }



}
