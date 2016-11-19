package no.fint.provider.eventstate

import no.fint.event.model.Event
import spock.lang.Specification


class EventStateServiceSpec extends Specification {

    private EventStateService eventStateService

    void setup() {
        eventStateService = new EventStateService()
    }

    void cleanup() {

    }

    def "Check if EventState is present i EventStateService"() {
        given:
        Event event1 = new Event("org1", "fk1", "GET", "client1")
        eventStateService.addEventState(event1)

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
        eventStateService.addEventState(event)

        then:
        eventStateService.getEventStateMap().size() == 1

    }

    def "Clear EventState"() {
        given:
        Event event = new Event("org", "fk", "GET", "client")
        eventStateService.addEventState(event)

        when:
        eventStateService.clearEventState(event)

        then:
        !eventStateService.exists(event)
        eventStateService.getEventStateMap().size() == 0
    }

}
