package no.fint.provider.events

import no.fint.events.FintEvents
import no.fint.provider.admin.AdminService
import no.fint.test.utils.MockMvcSpecification
import org.springframework.test.web.servlet.MockMvc

class EventsControllerSpec extends MockMvcSpecification {
    private EventsController eventsController
    private EventsService eventsService
    private AdminService adminService
    private FintEvents fintEvents
    private MockMvc mockMvc

    def setup() {
        eventsService = Mock()
        adminService = Mock()
        fintEvents = Mock()
        eventsController = new EventsController(eventsService, adminService, fintEvents)
        mockMvc = standaloneSetup(eventsController)
    }

    def 'Register'() {
        when:
        def response = mockMvc.perform(post('/events').header('x-org-id', 'test.org').header('x-client', 'Spock'))

        then:
        1 * eventsService.register('test.org')
        1 * adminService.register('test.org', 'Spock') >> true
        1 * fintEvents.registerDownstreamListener('test.org', eventsService)
        response.andExpect(status().isAccepted())
    }
}
