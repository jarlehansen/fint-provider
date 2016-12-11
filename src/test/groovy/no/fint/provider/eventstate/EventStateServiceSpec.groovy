package no.fint.provider.eventstate

import no.fint.event.model.Event
import no.fint.provider.testutils.LocalProfileTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import spock.lang.Specification

@LocalProfileTest
class EventStateServiceSpec extends Specification {

    @Autowired
    private EventStateService eventStateService

    @Autowired
    private EventStateRepository repository

    @Autowired
    private RedisTemplate redisTemplate

    void setup() {
        def keys = redisTemplate.keys("*")
        keys.forEach({
            redisTemplate.delete(it)
        })
    }

    def "Check if EventState is present i EventStateService"() {
        given:
        Event event1 = new Event("org1", "fk1", "GET", "client1")
        eventStateService.add(event1)

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
        eventStateService.add(event)

        then:
        eventStateService.getMap().size() == 1

    }

    def "Clear EventState"() {
        given:
        Event event = new Event("org", "fk", "GET", "client")
        eventStateService.add(event)

        when:
        eventStateService.clear(event)

        then:
        !eventStateService.exists(event)
        eventStateService.getMap().size() == 0
    }

}
