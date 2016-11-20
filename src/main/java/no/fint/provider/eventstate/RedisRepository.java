package no.fint.provider.eventstate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class RedisRepository {

    @Value("${fint.provider.eventstate.hashmap-name:event-state}")
    private String key;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private HashOperations<String, String, String> hashOps;

    @PostConstruct
    private void init() {
        hashOps = redisTemplate.opsForHash();
    }

    public Boolean exists(String corrId) {
        return hashOps.hasKey(key, corrId);
    }

    public void add(EventState eventState) {
        try {
            String json = objectMapper.writeValueAsString(eventState);
            hashOps.put(key, eventState.getEvent().getCorrId(), json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void remove(String corrId) {
        hashOps.delete(key, corrId);
    }

    public Map<String, EventState> getMap() {
        Map<String, String> entries = hashOps.entries(key);
        Map<String, EventState> eventStateMap = new HashMap<>();
        entries.keySet().forEach(key -> {
            try {
                EventState eventState = objectMapper.readValue(entries.get(key), EventState.class);
                eventStateMap.put(key, eventState);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return eventStateMap;
    }


}
