package no.fint.provider.eventstate

import no.fint.event.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import redis.embedded.RedisServer
import spock.lang.Specification

@ContextConfiguration(classes = RedisConfiguration.class)
@SpringBootTest(classes = TestApplication.class)
class EventStateServiceSpec extends Specification {

    private EventStateService eventStateService
    @Autowired
    private RedisRepository repository
    private RedisServer redisServer

    void setup() {
        redisServer = new RedisServer(6379)
        redisServer.start();
        repository = new RedisRepository()
        eventStateService = new EventStateService(redisRepository: repository)
    }

    void cleanup() {
        redisServer.stop()

    }

    def "Check if EventState is present i EventStateService"() {
        given:
        Event event1 = new Event("org1", "fk1", "GET", "client1")
        eventStateService.addEventState(event1)

        when:
        boolean exists1 = eventStateService.exists(event1)
        boolean exists2 = eventStateService.exists(new Event("org2", "fk2", "GET", "client2"))

        then:
        exists1 == true
        exists2 == false
    }

    def "Add EventState" () {
        given:
        Event event = new Event("org", "fk", "GET", "client")

        when:
        eventStateService.addEventState(event)

        then:
        eventStateService.getEventStateMap().size() == 1

    }

    def "Clear EventState" () {
        given:
        Event event = new Event("org", "fk", "GET", "client")
        eventStateService.addEventState(event)

        when:
        eventStateService.clearEventState(event)

        then:
        eventStateService.exists(event) == false
        eventStateService.getEventStateMap().size() == 0
    }

}
