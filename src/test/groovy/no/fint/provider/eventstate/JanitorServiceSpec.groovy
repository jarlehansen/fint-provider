package no.fint.provider.eventstate

import no.fint.event.model.Event
import spock.lang.Specification


class JanitorServiceSpec extends Specification {
    def "Check if the Janitor removes old EventStates"() {
        given:
        Event event1 = new Event("org1", "fk1", "GET", "client1")
        Event event2 = new Event("org2", "fk2", "GET", "client2")
        Event event3 = new Event("org3", "fk3", "GET", "client3")
        Event event4 = new Event("org4", "fk4", "GET", "client4")
        Event event5 = new Event("org5", "fk5", "GET", "client5")
        EventStateService eventStateService = new EventStateService()

        eventStateService.addEventState(event1)
        eventStateService.addEventState(event2)
        eventStateService.addEventState(event3)
        eventStateService.addEventState(event4)
        sleep(8000)
        eventStateService.addEventState(event5)

        when:
        JanitorService janitorService = new JanitorService();
        janitorService.run();

        then:
        eventStateService.getEventStateMap().size() == 4
    }
}
