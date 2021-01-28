package no.fint.provider.events

import no.fint.test.utils.MockMvcSpecification
import org.springframework.test.web.servlet.MockMvc

class EventsControllerSpec extends MockMvcSpecification {
    private EventsController eventsController
    private EventsService eventsService
    private MockMvc mockMvc

    def setup() {
        eventsService = Mock()
        eventsController = new EventsController(eventsService)
        mockMvc = standaloneSetup(eventsController)
    }

    def 'Register'() {
        when:
        def response = mockMvc.perform(post('/events').header('x-org-id', 'test.org').header('x-client', 'Spock'))

        then:
        1 * eventsService.register('test.org')
        response.andExpect(status().isAccepted())
    }
}
