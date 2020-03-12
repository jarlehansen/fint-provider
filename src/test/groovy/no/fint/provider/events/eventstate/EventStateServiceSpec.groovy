package no.fint.provider.events.eventstate

import com.hazelcast.core.HazelcastInstance
import no.fint.event.model.Event
import no.fint.provider.events.ProviderProps
import spock.lang.Specification

class EventStateServiceSpec extends Specification {
    private EventStateService eventStateService
    private HazelcastInstance hazelcastInstance

    void setup() {
        def props = Mock(ProviderProps) {
            getKey() >> 'current-corrids'
        }
        hazelcastInstance = Mock(HazelcastInstance)
        eventStateService = new EventStateService(eventStates: [:], providerProps: props, hazelcastInstance: hazelcastInstance)
    }

    def "Init EventStateService"() {
        when:
        eventStateService.init()

        then:
        eventStateService.eventStates != null
    }

    def "Add and get new EventState"() {
        given:
        def event = new Event('rogfk.no', 'test', 'GET_ALL', 'test')

        when:
        eventStateService.add(event, 2)

        then:
        eventStateService.getEventStates().size() == 1
    }

    def "Remove existing EventState"() {
        given:
        def event = new Event('rogfk.no', 'test', 'GET_ALL', 'test')
        eventStateService.add(event, 2)

        expect:
        eventStateService.getEventStates().size() == 1

        when:
        def eventState = eventStateService.remove(event)

        then:
        eventState.isPresent()
        eventStateService.getEventStates().isEmpty()
    }

    def "Trying to remove EventState that does not exist"() {
        given:
        def event = new Event()

        expect:
        eventStateService.getEventStates().isEmpty()

        when:
        def eventState = eventStateService.remove(event)

        then:
        !eventState.isPresent()
    }

    def "Get expired EventStates"() {
        given:
        def event1 = new Event('rogfk.no', 'test', 'GET_ALL', 'test')
        def event2 = new Event('rogfk.no', 'test', 'GET_ALL', 'test')
        eventStateService.add(event1, -2)
        eventStateService.add(event2, 2)

        when:
        def expired = eventStateService.getExpiredEvents()

        then:
        expired.size() == 1
        eventStateService.getEventStates().size() == 1
    }
}
