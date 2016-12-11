package no.fint.provider.eventstate

import no.fint.event.model.Event
import no.fint.provider.testutils.LocalProfileTest
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@LocalProfileTest
class EventStateRepositorySpec extends Specification {

    @Autowired
    private EventStateRepository repository

    def "Add, check if exists and remove event state"() {
        given:
        def eventState = new EventState(new Event(corrId: 'corrId'))

        when:
        repository.add(eventState)
        def afterAdd = repository.exists('corrId')
        repository.remove('corrId')
        def afterRemove = repository.exists('corrId')

        then:
        afterAdd
        !afterRemove
    }

    def "Add event state and check map with correlation id and value"() {
        given:
        def eventState = new EventState(new Event(corrId: 'corrId'))

        when:
        repository.add(eventState)
        def states = repository.getMap()

        then:
        states.keySet()[0] == 'corrId'
        states.values()[0].event.corrId == 'corrId'
    }

}
