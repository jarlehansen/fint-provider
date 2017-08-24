package no.fint.provider.events.eventstate

import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.provider.events.ProviderProps
import org.redisson.api.RedissonClient
import spock.lang.Specification

class EventStateServiceSpec extends Specification {
    private EventStateService eventStateService
    private FintEvents fintEvents

    void setup() {
        fintEvents = Mock(FintEvents)
        def props = Mock(ProviderProps) {
            getKey() >> 'current-corrids'
        }
        eventStateService = new EventStateService(eventStates: [], fintEvents: fintEvents, providerProps: props)
    }

    def "Init EventStateService"() {
        given:
        RedissonClient client = Mock(RedissonClient)

        when:
        eventStateService.init()

        then:
        1 * fintEvents.getClient() >> client
        1 * client.getSet('current-corrids')
    }

    def "Add and get new EventState"() {
        given:
        def event = new Event('rogfk.no', 'test', 'GET_ALL', 'test')

        when:
        eventStateService.add(event, 2)
        def eventState = eventStateService.get(event)

        then:
        eventState.isPresent()
        eventState.get().event == event
        eventStateService.getEventStates().size() == 1
    }

    def "Remove existing EventState"() {
        given:
        def event = new Event('rogfk.no', 'test', 'GET_ALL', 'test')
        eventStateService.add(event, 2)

        when:
        eventStateService.remove(event)
        def eventState = eventStateService.get(event)

        then:
        !eventState.isPresent()
    }

    def "Trying to remove EventState that does not exist"() {
        given:
        def event = new Event()

        when:
        eventStateService.remove(event)
        def eventState = eventStateService.get(event)

        then:
        !eventState.isPresent()
    }
}
