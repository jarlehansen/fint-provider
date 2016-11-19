package no.fint.provider.eventstate

import no.fint.event.model.Event
import spock.lang.Specification


class JanitorServiceSpec extends Specification {
    private EventStateService eventStateService
    private JanitorService janitorService

    void setup() {
        eventStateService = Mock(EventStateService)
        janitorService = new JanitorService(eventStateService: eventStateService, eventStateTimeToLive: 0)
    }

    def "Check if the Janitor removes old EventStates"() {
        given:
        Event event1 = new Event("org1", "fk1", "GET", "client1")
        Event event2 = new Event("org2", "fk2", "GET", "client2")
        def eventStates = [(event1.corrId): new EventState(event1), (event2.corrId): new EventState(event2)]

        eventStateService.addEventState(event1)
        eventStateService.addEventState(event2)

        when:
        janitorService.run()

        then:
        1 * eventStateService.getEventStateMap() >> eventStates
        2 * eventStateService.clearEventState(_ as Event)
    }
}
