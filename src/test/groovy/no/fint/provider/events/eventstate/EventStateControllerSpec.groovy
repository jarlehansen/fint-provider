package no.fint.provider.events.eventstate

import no.fint.event.model.Event
import no.fint.test.utils.MockMvcSpecification
import org.springframework.test.web.servlet.MockMvc

class EventStateControllerSpec extends MockMvcSpecification {
    private EventStateController controller
    private EventStateService eventStateService
    private MockMvc mockMvc

    void setup() {
        eventStateService = Mock(EventStateService)
        controller = new EventStateController(eventStateService: eventStateService)
        mockMvc = standaloneSetup(controller)
    }

    def "GET event states"() {
        when:
        def response = mockMvc.perform(get('/eventStates'))

        then:
        1 * eventStateService.getEventStates() >> [new EventState(new Event(), 42)]
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
    }
}
