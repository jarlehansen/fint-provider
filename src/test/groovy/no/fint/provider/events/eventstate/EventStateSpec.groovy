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

}
