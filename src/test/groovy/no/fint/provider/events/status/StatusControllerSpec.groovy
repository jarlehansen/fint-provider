package no.fint.provider.events.status

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.event.model.Event
import no.fint.test.utils.MockMvcSpecification
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

class StatusControllerSpec extends MockMvcSpecification {
    private StatusController controller
    private StatusService statusService
    private MockMvc mockMvc

    void setup() {
        statusService = Mock(StatusService)
        controller = new StatusController(statusService: statusService)
        mockMvc = standaloneSetup(controller)
    }

    def "POST status"() {
        given:
        def event = new Event()
        def eventJson = new ObjectMapper().writeValueAsString(event)

        when:
        def response = mockMvc.perform(
                post('/status')
                        .header('x-org-id', 'rogfk.no')
                        .content(eventJson)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))

        then:
        1 * statusService.updateEventState(_ as Event)
        response.andExpect(status().isOk())
    }
}
