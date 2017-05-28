package no.fint.provider.events.eventstate

import no.fint.event.model.Event
import spock.lang.Specification

class EventStateServiceSpec extends Specification {
    private EventStateService eventStateService

    void setup() {
        eventStateService = new EventStateService(eventStates: [])
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
