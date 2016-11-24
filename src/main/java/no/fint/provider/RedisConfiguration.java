package no.fint.provider;

import no.fint.provider.eventstate.EventState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Profile("local")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class RedisConfiguration {

    private RedisServer redisServer;

    @PostConstruct
    public void init() throws IOException {
        redisServer = new RedisServer(16379);
        redisServer.start();
    }

    @PreDestroy
    public void showdown() {
        redisServer.stop();
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
        jedisConFactory.setHostName("localhost");
        jedisConFactory.setPort(16379);
        return jedisConFactory;
    }

    @Bean
    public RedisTemplate<String, EventState> redisTemplate() {
        RedisTemplate<String, EventState> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setDefaultSerializer(new StringRedisSerializer());
        return template;
    }

}
