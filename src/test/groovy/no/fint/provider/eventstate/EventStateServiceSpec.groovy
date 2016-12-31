package no.fint.provider.eventstate

import no.fint.event.model.Event
import no.fint.event.model.Status
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

    def "Check if EventState is present"() {
        given:
        def event = new Event('org1', 'fk1', 'GET', 'client1')
        eventStateService.add(event)

        when:
        boolean exists1 = eventStateService.exists(event)
        boolean exists2 = eventStateService.exists(new Event('org2', 'fk2', 'GET', 'client2'))

        then:
        exists1
        !exists2
    }

    def "Check if EventState with status DELIVERED_TO_PROVIDER is present"() {
        given:
        def event = new Event('org1', 'fk1', 'GET', 'client1')
        event.setStatus(Status.DELIVERED_TO_PROVIDER)
        eventStateService.add(event)

        when:
        boolean correctStatus = eventStateService.exists(event, Status.DELIVERED_TO_PROVIDER)
        boolean wrongStatus = eventStateService.exists(event, Status.DOWNSTREAM_QUEUE)

        then:
        correctStatus
        !wrongStatus
    }

    def "Add EventState"() {
        given:
        def event = new Event('org', 'fk', 'GET', 'client')

        when:
        eventStateService.add(event)

        then:
        eventStateService.getMap().size() == 1

    }

    def "Clear EventState"() {
        given:
        def event = new Event('org', 'fk', 'GET', 'client')
        eventStateService.add(event)

        when:
        eventStateService.clear(event)

        then:
        !eventStateService.exists(event)
        eventStateService.getMap().size() == 0
    }

    def "Update EventState"() {
        given:
        def event = new Event('org', 'fk', 'GET', 'client')
        eventStateService.add(event)

        when:
        eventStateService.update(new Event(corrId: event.getCorrId(), orgId: 'orgId2'))
        def storedEvent = eventStateService.get(event.getCorrId())

        then:
        storedEvent.isPresent()
        storedEvent.get().event.orgId == 'orgId2'
    }

}
