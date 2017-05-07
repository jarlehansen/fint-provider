package no.fint.provider.eventstate

import no.fint.event.model.Event
import spock.lang.Specification

class EventStateServiceSpec extends Specification {
    private EventStateService eventStateService

    void setup() {
        eventStateService = new EventStateService(eventStates: [])
    }

    def "Only add unique corrIds to event state set"() {
        given:
        def event1 = new Event()

        when:
        eventStateService.add(event1)
        eventStateService.add(event1)
        def eventStates = eventStateService.getEventStates()

        then:
        eventStates.size() == 1
    }

    def "Check if event state exists"() {
        given:
        def event = new Event()
        eventStateService.add(event)

        when:
        def exists = eventStateService.exists(event)

        then:
        exists
    }

    def "Remote existing event state from set"() {
        given:
        def event = new Event()
        eventStateService.add(event)

        when:
        eventStateService.remove(event)
        def exists = eventStateService.exists(event)

        then:
        !exists
    }
}
