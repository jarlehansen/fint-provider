package no.fint.provider.eventstate

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.event.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import redis.embedded.RedisServer
import spock.lang.Shared
import spock.lang.Specification

@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest
class EventStateServiceSpec extends Specification {

    @Autowired
    private EventStateService eventStateService

    @Autowired
    private RedisRepository repository

    @Autowired
    private RedisTemplate redisTemplate

    @Shared
    private RedisServer redisServer

    def setupSpec() {
        redisServer = new RedisServer(6379)
        redisServer.start();
    }

    def cleanupSpec() {
        redisServer.stop()
    }

    void setup() {
        def keys = redisTemplate.keys("*")
        keys.forEach({
            redisTemplate.delete(it)
        })
    }

    def "Check if EventState is present i EventStateService"() {
        given:
        Event event1 = new Event("org1", "fk1", "GET", "client1")
        eventStateService.addEventState(event1)
        println new ObjectMapper().writeValueAsString(new Event("hfk.no", "fk", "GET", "client"))

        when:
        boolean exists1 = eventStateService.exists(event1)
        boolean exists2 = eventStateService.exists(new Event("org2", "fk2", "GET", "client2"))

        then:
        exists1
        !exists2
    }

    def "Add EventState"() {
        given:
        Event event = new Event("org", "fk", "GET", "client")

        when:
        eventStateService.addEventState(event)

        then:
        eventStateService.getEventStateMap().size() == 1

    }

    def "Clear EventState"() {
        given:
        Event event = new Event("org", "fk", "GET", "client")
        eventStateService.addEventState(event)

        when:
        eventStateService.clearEventState(event)

        then:
        !eventStateService.exists(event)
        eventStateService.getEventStateMap().size() == 0
    }

}
