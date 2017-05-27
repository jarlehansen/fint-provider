package no.fint.provider.events.eventstate

import no.fint.event.model.Event
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class EventStateServiceSpec extends Specification {
    private EventStateService eventStateService

    void setup() {
        eventStateService = new EventStateService(eventStates: [], timeout: 1)
    }

    def "Only add unique corrIds to event state set"() {
        given:
        def event1 = new Event()

        when:
        eventStateService.add(event1, 1)
        eventStateService.add(event1, 2)
        def eventStates = eventStateService.getEventStates()

        then:
        eventStates.size() == 1
    }

    def "Check if event state exists"() {
        given:
        def event = new Event()
        eventStateService.add(event, 1)

        when:
        def eventState = eventStateService.get(event)

        then:
        eventState.isPresent()
    }

    def "Remote existing event state from set"() {
        given:
        def event = new Event()
        eventStateService.add(event, 1)

        when:
        eventStateService.remove(event)
        def exists = eventStateService.exists(event)

        then:
        !exists
    }

    def "Event state is not expired when the timeout is not exceeded"() {
        given:
        def event = new Event('rogfk.no', 'fk', 'GET', 'vfs')
        eventStateService.add(event)

        when:
        def expired = eventStateService.expired(event)

        then:
        !expired
    }

    def "Return expired when the event corrId is not present"() {
        when:
        def expired = eventStateService.expired(new Event(corrId: '123'))

        then:
        expired
    }
}
