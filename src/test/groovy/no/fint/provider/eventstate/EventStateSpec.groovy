package no.fint.provider.eventstate

import no.fint.event.model.Event
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class EventStateSpec extends Specification {

    def "Create EventState Object"() {
        given:
        Event event = new Event('rogfk.no', 'FK', 'GET', 'client')

        when:
        EventState eventState = new EventState(event)

        then:
        eventState.timestamp > 0
        eventState.orgId == 'rogfk.no'
    }
}
