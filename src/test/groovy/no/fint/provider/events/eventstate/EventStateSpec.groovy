package no.fint.provider.events.eventstate

import no.fint.event.model.Event
import spock.lang.Specification

class EventStateSpec extends Specification {
    private Event event

    void setup() {
        event = new Event('rogfk.no', 'FK', 'GET', 'client')
    }

    def "Create EventState object with ttl"() {
        when:
        def eventState = new EventState(event, 2)

        then:
        eventState.corrId != null
        eventState.event == event
        eventState.expires > 0
        !eventState.expired()
    }

    def "Two events with same information are not equal"() {
        when:
        def event2 = new Event(event)

        then:
        event.equals(event)
        event2.equals(event)
        event2.hashCode() == event.hashCode()

        when:
        event2.corrId = 'Something else'

        then:
        !event2.equals(event)
        event2.hashCode() != event.hashCode()
    }
}
