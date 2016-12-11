package no.fint.provider;

import no.fint.provider.eventstate.EventState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class RedisConfiguration {

    private static final int REDIS_EMBEDDED_PORT = 16379;

    @Value("${fint.provider.eventstate.host:localhost}")
    private String host;

    @Value("${fint.provider.eventstate.port:6379}")
    private int port;

    @Value("${fint.provider.test-mode:false}")
    private String testMode;

    private RedisServer redisServer;

    @PostConstruct
    public void init() throws IOException {
        if (test()) {
            redisServer = new RedisServer(REDIS_EMBEDDED_PORT);
            redisServer.start();
        }
    }

    @PreDestroy
    public void showdown() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
        jedisConFactory.setHostName(host);
        if (test()) {
            jedisConFactory.setPort(REDIS_EMBEDDED_PORT);
        } else {
            jedisConFactory.setPort(port);
        }
        return jedisConFactory;
    }

    @Bean
    public RedisTemplate<String, EventState> redisTemplate() {
        RedisTemplate<String, EventState> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(EventState.class));
        return template;
    }


    private boolean test() {
        return (Boolean.valueOf(testMode));
    }
}
